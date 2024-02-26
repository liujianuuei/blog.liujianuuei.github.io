# 序列化

序列化的本质是**把运行环境中的对象转化为在非运行环境（比如网络、磁盘文件等）中存在的形式的过程**，即运行环境之外的对象表示法，这种形式又可以被反序列化成运行环境中的对象。也就是说一个对象只要脱离运行环境存在，就需要被序列化。序列化和持久化的区别在于，持久化是指长久保持对象的方式，而序列化是指一个对象脱离运行环境存在的方式。

具体地说，序列化把JVM中的对象转化成字节流编码在比如网络等的通信传输管道上传输或存储在如磁盘的存储介质上，反序列化又把已有的字节流编码转换成Java对象加载到JVM里，需要注意的是该对象相当于是原有对象的深复制，也就是说内存地址是新分配的，但是所有引用值是相等的，即和原有对象做`equals()`比较结果是为真的（当然，也一定程度上基于`equals()`的实现，这里讨论的是理论情况），这意味着反序列化的是整个对象拓扑图，而不仅仅是当前对象及其引用。可以把反序列化看成是**用字节流作为唯一参数的构造器**。

那么，如何序列化一个对象呢？一般意义上只需要该对象所属的类或父类实现`Serializable`接口即可（标记其可被序列化，否则抛出`java.io.NotSerializableException`；但需要注意的是其所有成员变量所属类也必须是Serializable的，而且其超类要么实现Serializable接口，要么提供无参构造器，没有则会抛出`java.io.InvalidClassException`异常），极限情况下都不用声明`serialVersionUID`。然后客户端代码通过`ObjectOutputStream`把对象写到输出流里：

```Java
public static byte[] serialize(final Serializable object) throws IOException {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutput out = null;
    try {
        out = new ObjectOutputStream(baos);
        out.writeObject(object);
        return baos.toByteArray();
    } finally {
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException ignored) {
            // ignore close exception
        }
        try {
            baos.close();
        } catch (IOException ignored) {
            // ignore close exception
        }
    }
}
```

那么，如何反序列化，把字节流恢复成Java对象呢？相对于序列化，只需要逆操作：

```Java
public static Object deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {
    ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
    ObjectInput in = null;
    try {
        in = new ObjectInputStream(bais);
        return in.readObject();
    } finally {
        try {
            bais.close();
        } catch (IOException ignored) {
            // ignore close exception
        }
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ignored) {
            // ignore close exception
        }
    }
}
```

需要注意的是，返回的是Object对象，需要强转成具体类型的对象。

除我们自己实现外，Apache还提供了现成的库帮助我们做些事，Apache的Commons Lang库包含了序列化相关的Utility类`SerializationUtils`，我们来看Out-of-box的序列化代码，并且加了Base64编码将其转换成字符串：

```Java
public static String serialize(final Serializable object) {
    byte[] bytes = SerializationUtils.serialize(object);
    return Base64.encodeBase64String(bytes);
}

public static Object deserialize(final String base64SerializedString) {
    byte[] bytes = Base64.decodeBase64(base64SerializedString);
    return SerializationUtils.deserialize(bytes);
}
```

注：Base64编码的功能是Apache Commons Codec提供的，当然我们也可以替换其他方案。

## 序列化原理

以上是具体操作部分，接下来我们来看序列化的原理。当序列化的时候发生了什么呢？序列化算法一般会按步骤做如下事情（我们想象一个机械臂在操作）：

1. 输出对象所属类的元数据。
2. 移动到超类递归第1步，直到没有超类。
3. 输出对象（当前为最顶端的超类对象）的实际数据（成员变量值），如果有未描述（输出元数据）的类，则回到第1步开始执行。
4. 移动到子类对象，递归第3步，直到没有子类。

那么这些元数据都包括哪些呢？我们可以以十六进制形式查看序列化后的字节流，可以看到元数据包括：类名称、serialVersionUID、该类所包含的成员变量个数、成员变量的类型、成员变量名称、超类的元数据等。

我们可以看到元数据具备序列化和恢复一个对象的全部信息，那么元数据中的`serialVersionUID`是干什么用的？似乎并没有用到它。我们首先不声明serialVersionUID，执行序列化和反序列化没有任何问题，一切运行正常，直到我们对类作了修改，比如改了成员变量名，然后我们用老的字节流反序列化新的对象，就会抛出如下异常：

```
java.io.InvalidClassException: xxx.xxx.XxxXxx; local class incompatible: stream classdesc serialVersionUID = -1450792935286580910, local class serialVersionUID = 1835749730874507377
```

当为新的类显式地声明了和字节流中存储的serialVersionUID相同的serialVersionUID时，运行时异常不见了，但问题是仍然不能完全恢复到之前的对象状态，假如成员变量名称变了，则会被初始化到默认值（需注意的是如果类名变了则会抛出`ClassNotFoundException`）。那么serialVersionUID的作用是什么呢，即便我们不设定serialVersionUID，也可以实现这样的效果，通过序列化流的信息先找到对应的类（类名不可以更改），再查找对应成员变量，找到则反序列化相应成员变量，没找到则忽略，还有类新增加的成员变量则初始化为默认值，一样的效果，完全可以实现。所以，serialVersionUID的可能的唯一用处就是破坏这种兼容性质的序列化（某些情况下可能会用到），即当一个类改变以后，人为阻止反序列化的发生，当然是通过设定不一样的serialVersionUID值来完成的。但基于目前的JVM实现，反而为了支持类改变后和字节流的兼容性，我们不得不为每一个实现了Serializable接口的类，提供一个serialVersionUID，除非你想放弃这种可以接受（部分成员变量无法被反序列化而初始化为默认值）的兼容。

## 施加控制

序列化完全依赖`ObjectOutputStream`和`ObjectInputStream`这两个IO流，这两个流提供很多机制允许客户端对其行为施加控制（本节内容同样可以作为[IO](IO)的一部分，但因为跟序列化紧密相关，故在这里一并叙述）。接下来，我们来讨论如何对默认的序列化和反序列化行为施加外部的控制以及自定义序列化。

自定义序列化行为的一个方案是在实现Serializable接口的类中，定义`private void readObject(ObjectInputStream stream)`和`private void writeObject(ObjectOutputStream stream)`方法，则序列化或反序列化的时候该方法就会被调用，默认的序列化行为被忽略。自定义序列化的一种典型用法是为反序列化增加额外的检查，尤其是超类没有实现Serializable接口，但是提供了无参构造器（上面提到的情况），反序列化就会调用这个无参构造器，问题是如果超类的成员变量有业务逻辑的限制不能为初始化的默认值，则我们就可以通过自定义的序列化和反序列化行为为其提供有意义的值，而不是默认的无参构造。我们来看下面摘自《Effective Java（Second Edition）》的代码示例。带有无参构造器的超类没有实现Serializable接口（如果实现Serializable接口，则不会调用无参构造器，直接进行反序列化），而且成员变量有约束（必须被初始化过），不能直接调用默认构造器来进行反序列化：

```Java
// Nonserializable stateful class allowing serializable subclass
public abstract class AbstractFoo {

    private int x, y; // Our state
    // This enum and field are used to track initialization
    private enum State { NEW, INITIALIZING, INITIALIZED };
    private final AtomicReference<State> init = new AtomicReference<State>(State.NEW);

    public AbstractFoo(int x, int y) { initialize(x, y); }

    // This constructor and the following method allow
    // subclass's readObject method to initialize our state.
    protected AbstractFoo() { }

    protected final void initialize(int x, int y) {
        if (!init.compareAndSet(State.NEW, State.INITIALIZING)) {
            throw new IllegalStateException("Already initialized");
        }
        this.x = x;
        this.y = y;
        ... // Do anything else the original constructor did
        init.set(State.INITIALIZED);
    }

    // These methods provide access to internal state so it can
    // be manually serialized by subclass's writeObject method.
    protected final int getX() { checkInit(); return x; }
    protected final int getY() { checkInit(); return y; }

    // Must call from all public and protected instance methods
    private void checkInit() {
        if (init.get() != State.INITIALIZED)
        throw new IllegalStateException("Uninitialized");
    }

    ... // Remainder omitted
}
```

子类自定义序列化行为，对成语变量进行初始化满足约束：

```Java
// Serializable subclass of nonserializable stateful class
public class Foo extends AbstractFoo implements Serializable {

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        // Manually deserialize and initialize superclass state
        int x = s.readInt();
        int y = s.readInt();
        initialize(x, y);
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        // Manually serialize superclass state
        s.writeInt(getX());
        s.writeInt(getY());
    }

    // Constructor does not use the fancy mechanism
    public Foo(int x, int y) { super(x, y); }

    private static final long serialVersionUID = 1856835860954L;
}
```

除此之外，如果**一个对象的物理表示法（算法实现）远不同于它的逻辑状态**（摘自《Effective Java（Second Edition）》）就应该考虑自定义序列化行为，我们还是借鉴《Effective Java（Second Edition）》的代码示例：

```Java
// StringList with a reasonable custom serialized form
public final class StringList implements Serializable {

    private transient int size = 0;
    private transient Entry head = null;

    // No longer Serializable!
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    // Appends the specified string to the list
    public final void add(String s) { ... }

    /**
     * Serialize this {@code StringList} instance.
     *
     * @serialData The size of the list (the number of strings
     * it contains) is emitted ({@code int}), followed by all of
     * its elements (each a {@code String}), in the proper
     * sequence.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeInt(size);
        // Write out all elements in the proper order.
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        int numElements = s.readInt();
        // Read in all elements and insert them in list
        for (int i = 0; i < numElements; i++) {
            add((String) s.readObject());
        }
    }

    ... // Remainder omitted

}
```

注：当你提供了自定义的序列化形式，大多数或者所有成员变量都应该被标记成transient的，即排除在序列化之外（静态成员变量也不会被默认序列化，反序列化时，初始化为默认值或声明时赋的值），但是还应该在读写数据之前调用`defaultXxxObject()`，以保证以后的版本增加非瞬时的成员变量。

另外就反序列化来说，我们一开始提到反序列化可以看成是**用字节流作为唯一参数的构造器**，也就是`private void readObject(ObjectInputStream stream)`可以单独出现进行对象的语言之外的创建（无论显式还是隐式的readObject都会返回一个新建的实例）及增加约束关系检查等。关于如何保护性的编写readObject方法，请查看让人叹服的《EJ 2nd Edition）》第76条和第77条，以及[不可变类](不可变类)主题。需要特别提到的是：

> 每当你编写readObject方法的时候，都要这样想：你正在编写一个公有的构造器，无论给它传递什么样的字节流，它都必须产生一个有效的实例。不要假设这个字节流一定代表着一个真正被序列化过的实例。...下面以摘要的形式给出一些指导方针，有助于编写出更加健壮的readObject方法：

> + For classes with object reference fields that must remain private, defensively copy each object in such a field. Mutable components of immutable classes fall into this category.
> + Check any invariants and throw an InvalidObjectException if a check fails. The checks should follow any defensive copying.
> + If an entire object graph must be validated after it is deserialized, use the ObjectInputValidation interface [JavaSE6, Serialization].
> + Do not invoke any overridable methods in the class, directly or indirectly.

有选择性的序列化和反序列化某些成员变量的另一个方案是实现`Externalizable`接口，其不对对象的任何实际数据进行序列化（元数据还是会自动序列化），需要我们自定义`void writeExternal(ObjectOutput out)`和`void readExternal(ObjectInput in)`方法进行对象实际数据的序列化和反序列化。但是当成员变量比较多的时候，这样做会很麻烦，特别我们只想个别成员变量，或者那些**不属于对象逻辑状态的成员变量**排除在序列化之外，而`transient`关键字搭配`Serializable`就可以达到这样的效果。

再来考虑这样一种情况，从流中读取对象时，由于目标类的**超类**（为什么是超类，因为相较于成员变量，超类是必须*存在*的，也即非null）与流中提供的类的**超类**不同，导致流中目标类的超类的成员变量没有相对应的值，怎么办？除了初始化成默认值，ObjectInputStream还为我们提供了接口允许我们自定义赋值操作。当上述情况发生的时候，`private void readObjectNoData()`就会被调用，我们就可以为其提供比默认值更有价值的赋值。

注：需要注意的是，如果超类没有实现Serializable接口，也没有提供无参构造器，则会抛出`java.io.InvalidClassException`异常。

## 序列化代理

序列化代理的意思是在写入序列化形式到流中之前，我们替换将要写入的对象；在返回反序列化得到的对象之前我们替换为我们想要让程序返回的对象的一种方式。序列化代理可以避免用语言之外的方式创建对象，从而可以极大地减少出错和出现安全问题的可能性。

序列化代理，首先，为可序列化的类添加一个私有静态嵌套类，精确地**表示外围类的实例的逻辑状态**。这个静态类就称作序列化代理。它要有一个单独的构造器，其参数就是外围类，其实现就是从参数复制数据到自己的成员变量。

我们还是借用《Effective Java（Second Edition）》的代码示例：

```Java
// Immutable class that uses defensive copying
public final class Period {

    private final Date start;
    private final Date end;

    /**
     * @param start the beginning of the period
     * @param end the end of the period; must not precede start
     * @throws IllegalArgumentException if start is after end
     * @throws NullPointerException if start or end is null
     */
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
    }

    public Date start () { return new Date(start.getTime()); }

    public Date end () { return new Date(end.getTime()); }

    public String toString() { return start + " - " + end; }

    ... // Remainder omitted

    // Serialization proxy for Period class
    private static class SerializationProxy implements Serializable {

        private final Date start;
        private final Date end;

        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        // readResolve method for Period.SerializationProxy
        private Object readResolve() {
            return new Period(start, end); // Uses public constructor
        }

        private static final long serialVersionUID = 234098243823485285L;

    }

    // writeReplace method for the serialization proxy pattern
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // readObject method for the serialization proxy pattern
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }

}
```

这里起作用的是`ANY-ACCESS-MODIFIER Object writeReplace()`方法，在序列化写入流之前被调用，从而真正写入流的是SerializationProxy实例，代替外围类的实例，这样永远不会产生外围类的序列化实例。还要禁止攻击者调用外围类的readObject方法伪造序列化流。最后，反序列化时，代理类的readResolve方法将序列化代理转变回外围类的实例。这样因为反序列化实例是通过与任何其它实例相同的构造器、静态工厂方法而创建的，就不必显式的进行约束检查。

JDK自带的有一个应用序列化代理的例子，就是EnumSet的序列化实现：

```Java
// EnumSet's serialization proxy
private static class SerializationProxy <E extends Enum<E>> implements Serializable {

    // The element type of this enum set.
    private final Class<E> elementType;

    // The elements contained in this enum set.
    private final Enum[] elements;

    SerializationProxy(EnumSet<E> set) {
        elementType = set.elementType;
        elements = set.toArray(EMPTY_ENUM_ARRAY); // (Item 43)
    }

    private Object readResolve() {
        EnumSet<E> result = EnumSet.noneOf(elementType);
        for (Enum e : elements) {
            result.add((E)e);
        }
        return result;
    }

    private static final long serialVersionUID = 362491234563181265L;

}
```

反序列化代理（概念上）就是在返回反序列化得到的对象之前，用我们期望的对象进行替换。一个典型的用法就是实例受控类的序列化。

在[实例受控类](InstanceControlledClass.md)主题中我们提供了一个单例的实现，我们定义了一个方法`readResolve()`来避免反序列化创建新对象从而违反了单例的约束。`ANY-ACCESS-MODIFIER Object readResolve()`允许在返回反序列化的对象给调用者之前替换序列化的对象，因此，调用顺序是，先调用 `readObject()`，再调用 `readResolve()`。

注：反序列化的对象不需要包含任何实际数据，故所有的非静态成员变量都要声明为`transient`的，排除在序列化之外。

*彩蛋*：由于《Effective Java（Second Edition）》的作者同时也是JDK集合框架的作者，我们可以在JDK的源代码里看到如下的注释：

```Java
// readObject method for the serialization proxy pattern
// See Effective Java, Second Ed., Item 78.
private void readObject(java.io.ObjectInputStream stream)
    throws java.io.InvalidObjectException {
    throw new java.io.InvalidObjectException("Proxy required");
}
```

## 结语

借用《Effective Java（Second Edition）》的一句话，一定要谨慎地实现Serializable接口，实现Serializable接口并不是看上去那么容易。
