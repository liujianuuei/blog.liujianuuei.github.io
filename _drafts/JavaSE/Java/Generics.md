# 泛型

从编程角度看，泛型技术的本质在于方法或者类（包括接口）的逻辑实现与被操作的对象解耦。逻辑实现不会绑定在具体类型的对象上，无论什么类型的对象只要想被这样的逻辑处理，就可以作为参数传递进去。泛型也称作参数化的类型。

泛型支持方法和类以及接口的定义，我们先来看更常见的泛型方法。我们可以定义一个泛型方法，来满足任何数组是不是为null或者空的判断，而不管数组元素的类型是什么：

```Java
public static <T> boolean isNullOrEmptyArray(final T[] array) {
    return array == null || array.length == 0;
}
```

在这个非常简单的示例里，`T`就代表数组元素的类型，因为方法的逻辑和数组元素类型无关，所以我们用泛型来达到解耦的目的。泛型方法的定义：

```Java
modifiers <GT1, GT2, ...> GTn method(GTn arg, ...) { } // GTn stands for anyone of GT1, GT2, ...
```

泛型（的形式类型参数列表）定义在方法返回类型的前面，用`<`和`>`括起来，可以用任意有效标识符来给类型参数命名，一般习惯用一个大写字母来表示，这些泛型代表着任何类或接口，但不能是基本数据类型。方法的返回类型、参数类型以及局部变量类型等等，都可以声明为形式类型参数列表中的一种。

泛型类的典型应用可以在Java的集合框架中找到，比如`ArrayList`的定义：

```Java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    ... // Remainder omitted
}
```

类方法的定义就可以把类声明的类型参数当做和任何其他任何类或接口一样的类型来用：

```Java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // Increments modCount!!
    elementData[size++] = e;
    return true;
}
```

类的形式类型参数列表的定义紧跟在类名后面，用`<`和`>`括起来，可以用任意有效标识符来给类型参数命名，一般习惯用一个大写字母来表示，这些泛型代表着任何类或接口，但不能是基本数据类型。泛型列表中的泛型可以作为类的成员变量的类型、方法的返回类型以及局部变量的类型等等。

```Java
modifier class ClassName<GT1, GT2, ...> extends ClassType<GTn> implements InterfaceType<GTn> { } // GTn stands for anyone of GT1, GT2, ...
```

综上所述，泛型（参数化类型）的定义如下：

```Java
<GT1, GT2, ..., GTn>
```

其中，类型参数可被限定为某类型（类或接口）的所有子类型，称作**有限泛型**，反之则是无限泛型；有限泛型只能是**向下限制**，即子类型：

```Java
<GT1 extends SomeType>
```

同时，还可以为其指定用于限制的额外的**接口**（注意，只能是接口，但不限个数），则受检实例的类型必须为所有参数列表里的类型的子类型，即如果是类的话，就是子类，如果是接口的话，就是已经实现了该接口：

```Java
<GT1 extends SomeType1 & SomeInterface2 & ... & SomeInterfacen>
```

我们来看代码示例：

```Java
public class DelayQueue<E extends Delayed> extends AbstractQueue<E> implements BlockingQueue<E> {
    ... // Remainder omitted
}
```

```Java
// Returns the maximum value in a list - uses recursive type bound
public static <T extends Comparable<T>> T max(List<T> list) {
    Iterator<T> i = list.iterator();
    T result = i.next();
    while (i.hasNext()) {
        T t = i.next();
        if (t.compareTo(result) > 0)
        result = t;
    }
    return result;
}
```

总而言之，**泛型是一个类型系统**，为了更安全的数据结构，把运行时类型检查提前到编译阶段执行。泛型只支持引用类型即类类型和数组，而不支持基本类型。

## 泛型数组

创建泛型数组（即不可具体化的类型的数组）是不被允许的：

```Java
new T[1];
```

提示下面的编译时错误信息（根本原因请查看《Effective Java（Second Edition）》第25条）：

```
Cannot create a generic array of T
```

但是数组也可以声明为参数化类型的，也就是开头的例子，`T[]`是可以的：

```Java
public static <T> boolean isNullOrEmptyArray(final T[] array) {
    return array == null || array.length == 0;
}
```

所以，相比数组要更多的使用集合类。

## 纵向通配

泛型（参数化类型）是**非协变**的，换句话说，有`GT1 extends GT2`，但不能导出`<GT1> extends <GT2>`，而数组却可以导出`GT1[] extends GT2[]`，所以数组是协变的，下面的代码显示了这种差异：

```Java
List<Object> list = new ArrayList<String>(); // Error: Type mismatch: cannot convert from ArrayList<String> to List<Object>
List<Object> list = new ArrayList<?>(); // Error: Type mismatch: cannot convert from ArrayList<?> to List<Object>
Object[] array = new String[1];
```

为了规避这种限制，Java提供了通配符（?）来达到纵向扩展的目的。通配符分为**向下通配**、**向上通配**以及**无限通配**：

+ **向下通配** - 就是所有参数化类型的子类型都被允许，用`<? extends GT>`来表示。
+ **向上通配** - 就是所有参数化类型的超类型都被允许，用`<? super GT>`来表示。
+ **无限通配** - 就是任何参数化类型都被允许，用`?`来表示，相当于`<? extends Object>`，所以其实也是向下通配。

向下通配，编译器无法确定具体子类型，因此无法写入元素；向上通配，编译器无法确定具体父类型，因此无法读出元素。下面的助记符有助于记忆什么时候用哪种通配符：

**OEIS**（**O**ut-**e**xtends，**I**n-**s**uper），also known as **[PECS](http://stackoverflow.com/questions/4343202/difference-between-super-t-and-extends-t-in-java)** and **Get and Put Principle**。

也就是当参数化类型是用于给外部发放（或称作生产）GT，就使用向下通配；如果是用于接收到内部（或称作消费）GT，就使用向上通配。如果某个参数化类型既发放又接收，那么就需要完全的类型匹配，而不能使用统配类型。比如下面来自于`java.util.Collections`类的代码，很好的说明了这个原则：

```Java
public static <T> void copy(List<? super T> dest, List<? extends T> src) { ... }
```

通配更符合程序的真正逻辑，而不是限定于某种参数化的类型上，是一种纵向的扩展。但有一个限制，通配类型作为方法的返回类型是不被允许的，比如下面这段代码是无法通过编译的：

```Java
public static <?> void swap(final List<? extends Number> list, final int i, final int j) { ... } // Syntax error on token "?", invalid TypeParameter
```

我们把返回类型的通配符去掉，变成下面的代码，方法声明是没问题了，但方法体还是有编译错误：

```Java
public static void swap(final List<? extends Number> list, final int i, final int j) {
    list.set(i, list.set(j, list.get(i))); // set failed: The method set(int, capture#2-of ? extends Number) in the type List<capture#2-of ? extends Number> is not applicable for the arguments (int, capture#3-of ? extends Number)
}
```

原因就在于，**通配类型无法匹配到参数化类型**，两者不对称，通配类型的范围明显比某个参数化类型要大，无法做出类型推断。具体到这个例子，列表的`set`方法声明成参数化类型，而我们传递的是通配类型。解决方法就是利用通配类型和参数化类型的双重性：

```Java
public static void swap(final List<? extends Number> list, final int i, final int j) {
    doSwap(list, i, j);
}

public static <E> void doSwap(final List<E> list, final int i, final int j) {
    list.set(i, list.set(j, list.get(i)));
}
```

最后，原生类型（raw type）是为了兼容性才设计的，其与无限通配类型有根本的区别，不要使用原生类型，同时也避免类型的强转。

## 类型擦除

最后，让我们再深入一点。首先声明，这一部分对实际编程不会有什么帮助，只是让你懂得为什么。众所周知，由于泛型引入的时间比较晚，为了保持兼容性，在编译时所有泛型会被（先检查）擦除，对有限通配替换以其指定的边界类型，对无限通配则替换以Object类。比如下面的代码：

```Java
public interface List<E> extends Collection<E> {

    boolean add(E e);

}
```

```Java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, java.io.Serializable {

    public boolean add(E e) { ... }

}
```

```Java
List<String> list = new ArrayList<>(); // 客户端代码
```

编译时，先做类型检查，然后类型参数就会被擦除，JVM看到的就是无类型参数的代码：

```Java
public interface List extends Collection {

    boolean add(Object e);

}
```

```Java
public class ArrayList extends AbstractList implements List, RandomAccess, Cloneable, java.io.Serializable {

    public boolean add(Object e) { ... }

}
```

```Java
List list = new ArrayList(); // 客户端代码
```

记得我们上面说过，泛型（参数化类型）是**非协变**的，换句话说，有`GT1 extends GT2`，但不能导出`<GT1> extends <GT2>`。之所以这样，根本原因就是类型被擦除，导致无法进行推导。

另外，还有必要时的强制类型转换和所谓*桥接方法*来处理类型擦除所引起的方法重写问题，以及其它问题（比如，equals方法不能泛型化，Throwable以及子类不能泛型化等。），这里就不详细说了，可另行[查看](http://docs.oracle.com/javase/tutorial/java/generics/erasure.html)。

就这样。
