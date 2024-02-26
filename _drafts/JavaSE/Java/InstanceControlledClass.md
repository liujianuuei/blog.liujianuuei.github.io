# 实例受控（instance-controlled）

## 单例

最常用到的就是单体类，也叫单例，一个JVM里只允许存在一个实例。[这里](https://community.oracle.com/docs/DOC-918906)是Oracle官方提供的一个实现单体的各种方法的文档。接下来我们自己尝试做一个，下面是一个支持并行、序列化并尝试抵御反射攻击的实现，可酌情取其子集：

```Java
public class Singleton implements Serializable {

    private static final long serialVersionUID = 8089116755832779955L;

    // support serialization: transient is to make singleton written to memory
    // instantly and will not return a null value for readResolve(). // #Todo# transient why?? - NB: comment is no more correct.
    private static transient volatile Singleton instance;

    ...

    private Singleton() throws Throwable {
        // Defend against the attack that a privileged client can invoke the
        // private constructor reflectively with the aid of the
        // AccessibleObject.setAccessible method. And no need to synchronize
        // here as instance field is declared as transient. - NB: comment is no more correct.
        if (instance != null) {
            throw new AssertionError("single instance already exits");
        } else {
            synchronized (Singleton.class) {
                if (instance != null) {
                    throw new AssertionError("single instance already exits");
                }
                ...
                instance = this;
            }
        }
    }

    /**
     * Return a instance of this singleton.
     *
     * @return a instance of this singleton
     */
    public static Singleton getInstance() {
        if (instance == null) {
            synchronized (Singleton.class) {
                if (instance == null) {
                    try {
                        instance = new Singleton();
                    } catch (Throwable willNotHappen) {
                    }
                }
            }
        }
        return instance;
    }

    // support serialization
    private Object readResolve() {
        // Return the one true instance which is serialized and must be already
        // exits and let the garbage collector take care of the EeClipperFactory
        // impersonator.
        return instance;
    }

}
```

注：由于返回的是已经存在的实例，反序列化的对象会被忽略，所以单例的实例序列化形式不需要包含任何实际的数据，所有的非静态（静态实例域也不会被序列化）实例域都应该被声明为`transient`的。

另外，可以通过Enum机制来实现单例：

```Java
// Enum singleton - the preferred approach
public enum Singleton {

    instance;

    public void otherMethods() {
        ...
    }
}
```

其原生支持多线程、序列化和抵御反射攻击，绝对防止多次实例化（包括反序列化实例），可以说是实现单例的最佳方法。为什么Enum会原生支持这些特性呢，接下来我们来看Enum机制。

## 枚举

枚举是实例受控的，本质上是单例的泛型化。`java.lang.Enum`是Java里所有enums的基类，但是不能在enums的私有构造器里调用超类java.lang.Enum的构造器。**enums中的所有enum value都是继承自Enum的类（称作枚举类型）的实例**，并且必须定义在其它属性或方法的前面，其默认通过无参构造器来创建，如果枚举类型没有提供无参构造器，则必须为enum实例添加参数。另外，关于给Enum添加构造器的问题，枚举类型的构造器只被允许声明为没有`ACCESS-MODIFIER`（其实，内部机制限制其仍是只允许类内部实例化）的或者`private`的，即禁止外部实例化。总结enums的特性如下：

+ enums本身就是一个类，是继承自java.lang.Enum的类，但不能被扩展。
+ enums却可以实现自某个接口。
+ enums不能被外部实例化。
+ 所有enum value都是public-static-final的实例。
+ enums不能被序列化。
+ enums不能被clone。
+ enums不能被finalize。
+ enums用`==`实现`equals`方法。
+ 枚举类型允许添加任意方法或成员变量来增强枚举类型。

作为最佳实践，一般不需要为枚举类型声明成员变量和方法，再复杂点的需求，需要枚举实例提供一些内置的信息，则要为枚举类型提供方法，也就是一个声明的抽象方法，则每个枚举实例必须实现该方法，再复杂点的需求，需要把枚举实例和外部的数据关联起来，则需要枚举类型提供有参数的构造器来给成员变量赋值（不建议通过set方法来做，且成员变量应修饰为`private final`），同时提供公有的访问方法，再复杂一点，需要把枚举实例和某种行为以及外部数据关联起来，则枚举类型需要定义一个抽象的公有方法来约束每个枚举实例都必须实现的行为，同时该抽象方法是带有参数的，可以允许外部传入参数。如：

```Java
//Enum type with constant-specific class bodies and data
public enum Operation {
    PLUS("+") {
        @Override
        public double apply(final double x, final double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(final double x, final double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(final double x, final double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(final double x, final double y) {
            return x / y;
        }
    };

    private final String symbol;

    Operation(final String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    public abstract double apply(double x, double y);
}
```

另外，如果你重写了toString()方法，则需要考虑提供一个fromString(String)方法来代替自带的valueOf(String)方法，提供相同的功能：

```Java
private static final Map<String, Operation> stringToEnum = new HashMap<String, Operation>();

static { // Initialize map from constant name to enum constant
	for (Operation op : values()) {
		stringToEnum.put(op.toString(), op);
	}
}

// Returns Operation for string, or null if string is invalid
public static Operation fromString(final String symbol) {
	return stringToEnum.get(symbol);
}
```

最后，我们来看《Effective Java（Second Edition）》关于枚举使用策略的建议：

> In summary, the advantages of enum types over int constants are compelling. Enums are far more readable, safer, and more powerful. Many enums require no explicit constructors or members, but many others benefit from associating data with each constant and providing methods whose behavior is affected by this data. Far fewer enums benefit from associating multiple behaviors with a single method. In this relatively rare case, prefer constant-specific methods to enums that switch on their own values. Consider the strategy enum pattern if multiple enum constants share common behaviors.

**EnumSet用法**

EnumSet作为位域（见[Java基础](Java-Fundamental)主题）的一个建议替代，来处理**枚举的集合**，而免去了操作位运算的麻烦。EnumSet本身就是一个Set，故原生支持所有Set的操作，并且也提供了丰富的静态工厂来创建集合，示例代码如：

```Java
// EnumSet - a modern replacement for bit fields
public class Text {
    public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

    // Any Set could be passed in, but EnumSet is clearly best
    public void applyStyles(Set<Style> styles) { ... }
}
```

客户端代码：

```Java
text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC));
```

**EnumMap用法**

通过EnumMap的定义，我们知道它本身就是一个Map，而且其Key值的类型是Enum类型，所以EnumMap提供了**枚举到其它数据对象的映射**：

枚举类：

```Java
public class Herb {
    public enum Type { ANNUAL, PERENNIAL, BIENNIAL }

    private final String name;
    private final Type type;

    Herb(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override public String toString() {
        return name;
    }
}
```

使用EnumMap来**分类与关联**（映射：枚举->其它数据对象）：

```Java
// Using an EnumMap to associate data with an enum
Map<Herb.Type, Set<Herb>> herbsByType = new EnumMap<Herb.Type, Set<Herb>>(Herb.Type.class);

for (Herb.Type t : Herb.Type.values()) {
    herbsByType.put(t, new HashSet<Herb>());
}

for (Herb h : garden) {
    herbsByType.get(h.type).add(h);
}

System.out.println(herbsByType);
```

如果，所要表示的映射关系是多级的，就使用多级EnumMap：`EnumMap<K extends Enum<K>, EnumMap<K extends Enum<K>, V>>`。

**扩展枚举**

我们知道枚举是不能被扩展的，我们定义一个枚举，不能把它作为父类再定义一个类。但是有时候，我们初始定义一个枚举可能并不完备（没有尽可能的包含所有枚举实例），而之后的系统扩展被要求不能对之前的代码进行修改，比如之前的已经定义的枚举已经打包发布了等，但问题是我们又必须支持更多的枚举实例，这个时候，理论上我们需要*扩展*枚举。对于这种可能性非常小的情况，我们的解决方案是通过让**枚举实现接口**来达到这种效果：**任何可以使用原有枚举的地方都可以使用新扩展的枚举，因为它们都实现了同一个接口**。

但即便这样，本质上仍然是接口的扩展而非枚举，所以原有的枚举实例和枚举类中的方法并没有被继承过来，这也限制了这种模式的应用范围。如果想了解具体实现细节，请查看《Effective Java（Second Edition）》第34条。

另外，一个小的技巧，`Class`类提供了一个`public T[] getEnumConstants()`方法，用来得到枚举（如果是的话）的所有实例元素，与枚举的`public T[] values()`完全一样的功能，只不过是从Class类发起调用，所以可以适配任何枚举类型，我们用getEnumConstants()方法来实现values()方法：

```Java
public static <T extends Enum<T>> T[] values(final Class<T> enumClass) {
    return enumClass.getEnumConstants();
}
```

EnumMap之所以需要传入枚举类的类（Class）也是这个原因。

**还有一点**

如果我们查看JDK源代码，我们会看到枚举的超类是这样的定义形式：

```Java
public abstract class Enum<E extends Enum<E>> implements Comparable<E>, Serializable {...}
```

泛型`<E extends Enum<E>>`为什么会定义成这样？首先，这种自我递归式的泛型，规定的类型是其子类，也即是**枚举类型（也就是我们在程序中通过`enum`关键字定义的枚举）本身**；其次，超类操作的方法，无论传入参数或返回参数都被约束为其子类型（对枚举来说，就是枚举类型本身）。

也就是说，确保定义在超类里的方法`public final Class<E> getDeclaringClass()`返回的就是调用它的枚举的类型，同样传入`public final int compareTo(E o)`的参数也是调用它的枚举的类型本身。

一句话，就是为了超类（java.lang.Enum）可以定义**对于子类类型安全**的方法；也就是说需要直接引用到子类类型，但在定义这个超类的方法的时候还没有（不知道）具体实现的子类，所以用一个自我递归的泛型表示，这个自我递归的泛型就是其子类。

但这里还有个问题，即便是自我递归泛型也不能完全限定就是枚举类型本身，我们来看这样三个类：

```Java
public abstract class Foo<E extends Foo<E>> implements Comparable<E>, Serializable {...}
```

```Java
public class Foo2 extends Foo<Foo2> {...}
```

```Java
public class Foo3 extends Foo<Foo2> {...}
```

Foo模拟Enum类，Foo2相当于我们定义的枚举类型继承自Foo类，Foo3也相当于继承自Foo的枚举类型，但其类型参数传入的却不是其本身，这在编译上是能过的，这就违反了必须是子类本身的约束。原因在于`<E extends Foo<E>>`只规定了类型参数的范围，却没有规定与当前类（如Foo）的约束关系，这也是泛型系统本身的限制。就枚举来说，由于是通过enum关键字来标记一个类为枚举类型，所以也就不存在这个问题了。

注：更多关于泛型的介绍，请查看[泛型](Generics.md)主题。

## 结语

实例受控一般要么只允许一个，要么就没有任何限制，很少限制到大于1的每个数，但是有一类控制实例的用法是通过Map来控制用于不同目的的类的个数，但也是一个键只对应一个实例，虽然其本身可能并不是单例，这时候需要在`put`方法上进行同步。
