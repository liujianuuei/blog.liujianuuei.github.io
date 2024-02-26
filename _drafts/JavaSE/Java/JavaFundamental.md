# Java基础

## 关键字

主要介绍不常用到的关键字：

+ **native** - 通常用来修饰方法，一个native方法就是一个Java调用非Java代码的接口（A native method is a Java method whose implementation is provided by non-java code.），也就是说该方法的实现由非Java语言提供。另外，调用native方法的其他类甚至不知道它所调用的是一个本地方法，JVM将控制调用本地方法的所有细节，即JNI框架，这里就不展开叙述了。

+ **default** - 定义[注解](Annotation.md)时用于返回属性的默认值。Java 8用default来绕开已发布的接口无法再修改的障碍。在Java 8中，接口中的方法可以被实现。接口中被实现的方法叫做default方法，用关键字default作为修饰符来标识。当一个类实现一个接口的时候，它可以实现已经在接口中被实现过的方法，但这不是必须的。这个类会继承default方法。这就是为什么当接口发生改变的时候，实现类不需要做改动的原因。

+ **volatile** - 被其修饰的域在每次被线程访问时，都强迫从主内存中重读该成员变量的值；而且当成员变量发生变化时，强迫线程将变化值回写到主内存。这样在任何时刻，两个不同的线程总是看到域的同一个值，也称作*线程可见性*。

    Java语言规范中指出：为了获得最佳速度，允许**线程保存共享成员变量的私有拷贝**，而且只当线程进入或者离开同步代码块时才与共享成员变量的原始值对比。这样当多个线程同时读写某个对象，并且没有进行同步时（不在同步代码快内），就需要让线程及时的得到共享成员变量的变化。而volatile关键字就是告诉JVM对于这个域不能保存它的私有拷贝，而应直接与其它线程共享。

    由此可以，使用volatile屏蔽掉了JVM中必要的代码优化，所以在效率上比较低，因此一定在必要时才使用此关键字。

+ **transient** - 被其修饰的域不会被序列化，请查看[序列化](Serialization.md)。

+ **strictfp** - 修饰符的一种，可应用于类、接口或方法。使用 strictfp 关键字声明一个方法或类时，该方法或类中所有的float和double表达式都严格遵守FP-strict的限制,符合IEEE-754规范。通常处理器都各自实现浮点运算，各自专业浮点处理器为实现最高速，计算结果会和IEEE标准有细小差别。比如intel主流芯片的浮点运算，内部是80bit高精运算，只输出64bit的结果。IEEE只要求64bit精度的计算，你更精确反而导致结果不一样。所以设立**严格浮点计算**，保证在各平台间结果一致，IEEE标准优先，性能其次；而非严格的浮点计算是性能优先，标准其次。

+ **const** - Java保留字，不支持使用。

+ **goto** - Java保留字，不支持使用。

## 基本数据类型

|Data Type|Length(bit) |Default Value (for fields) |
|---------|------------|---------------------------|
|boolean  |1           |false                      |
|byte	  |8           |(byte)0                          |
|short	  |2-byte          |(short)0                          |
|char     |2-byte          |'\u0000'                   |
|int      |4-byte          |0                          |
|long     |8-byte          |0L                         |
|float    |4-byte          |0.0f                       |
|double   |8-byte          |0.0d                       |

这八个基本数据类型可以分为两大类，一类是**整型**，另一类是**浮点型**。浮点型包括float和double类型，剩下的可以归为整型。整型是精确的，而浮点型是不精确的。如果要求数值的绝对精确，不要用浮点型，更多细节请查看[精密计算](PreciseCalculation.md)。

除了上述八个基本数据类型, Java还通过类类型提供了对`char`串的支持。

|Data Type             |Default Value (for fields)|
|----------------------|--------------------------|
|String (or any object)|null                      |

这里还涉及到字符编码（比如ASCII和Unicode编码）等知识，请另行查阅。

另外，Java还提供了与基本数据类型相关的类，即包装类。尤其需要提到的是`char`的包装类`Character`，而不是`String`。为什么需要包装类这种东西？因为，**基本类型不是对象**，下面的代码在Java里不被允许：

```Java
7.toString();
```

也正是这一点，有人说Java不是完全面向对象的语言，而比如Ruby，其基本类型也是对象。所以在需要对象的地方，就需要与基本类型对应的包装类实例。

## 进制

Java支持二进制、八进制、十进制和十六机制的数值表示：

+ 二进制：数字前加0b前缀，如0b101。
+ 八进制：数字前加0前缀，如0107。
+ 十进制：数字前不加前缀，如109。
+ 十六机制：数字前加0x前缀，如0x10F。

## 运算符

主要介绍不常用到的位运算符，位运算符都是相对**整型二进制数位**来说的：

+ `<<` 假设a是一个被移位的整型数据，n是位移量。`a<<n`运算的结果是通过将a的所有位都左移n位，每左移一位，左边的高阶位上的0或1被移出丢弃，并用0填充右边的低位。更多细节请另行查阅。
+ `>>` 假设a是一个被移位的整型数据，n是位移量。`a>>n`运算的结果是通过将a的所有位都右移n位，每右移一位，右边的低阶位上的0或1被移出丢弃，并用0或1填充左边的高位。a是正数时用0填充，是负数时用1填充。更多细节请另行查阅。
+ `>>>` 假设a是一个被移位的整型数据，n是位移量。`a>>>n`运算的结果是通过将a的所有位都右移n位，每右移一位，右边的低阶位上的0或1被移出丢弃，并用0填充左边的高位。更多细节请另行查阅。

+ `&(&=)` 按位与。
+ `|(|=)` 按位或。
+ `~` 按位非；不能作用于boolean型数值。
+ `^(^=)` 按位异或，相同为0，不同为1。

按位运算符也可以操作逻辑型数据。按位运算的一个典型应用是定义一组常量，将2的不同倍数赋予每个常量：

```Java
// Bit field enumeration constants - OBSOLETE!
public class Text {
    public static final int STYLE_BOLD = 1 << 0; // 1
    public static final int STYLE_ITALIC = 1 << 1; // 2
    public static final int STYLE_UNDERLINE = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    // Parameter is bitwise OR of zero or more STYLE_ constants
    public void applyStyles(int styles) {
        if ((styles & STYLE_BOLD) == STYLE_BOLD) {
			System.out.println("bold");
		}
		if ((styles & STYLE_ITALIC) == STYLE_ITALIC) {
			System.out.println("italic");
		}
		if ((styles & STYLE_UNDERLINE) == STYLE_UNDERLINE) {
			System.out.println("underline");
		}
		if ((styles & STYLE_STRIKETHROUGH) == STYLE_STRIKETHROUGH) {
			System.out.println("strikethrough");
		}
    }
}
```

这种表示法允许你用按位或`|`运算符将几个常量合并到一个表达式中，称作**位域（bit field）**：

```Java
text.applyStyles(STYLE_BOLD | STYLE_ITALIC);
```

位域表示法也允许利用位运算符，执行联合、差集以及交集等操作。

#### 访问权限

|Modifier   |Class	|Package |Subclass |World|
|-----------|-------|--------|---------|-----|
|public	    |Y	    |Y	     |Y	       |Y    |
|protected  |Y	    |Y	     |Y	       |N    |
|package-private(default)|Y	    |Y	     |N	       |N    |
|private    |Y	    |N	     |N	       |N    |

几个值得注意的事项：构造方法可以被任何可见性修饰符修饰；子类 Override 父类方法，可以减弱访问权限，但不能增强；类不能被 private 修饰符修饰，这很好理解，用反推法，如果一个类被 private 修饰，则意味着它不能被任何其它类调用（对外不可见），也就没有任何意义。

另外，一个非常令人费解的问题，**顶层类可以用默认修饰符修饰，却不能用可见性更高的 protected 修饰符修饰**。StackOverflow
 上的一个[答案](http://stackoverflow.com/a/16293034)勉强可以解释这是为什么：

> As you know default is for package level access and protected is for package level plus non-package classes but which extends this class(Point to be noted here is you can extend the class only if it is visible!). lets put it in this way:
>
> + protected top-level class would be visible to classes in its package.
> + now making it visible outside the package (subclasses ) is bit confusing and tricky. Which classes should be allowed to inherit our protected class?
> + If all the classes are allowed to subclass then it will be similar to public access specifier.
> + If none then it is similar to Default.
>
> Since there is no way to restrict this class being subclassed by only few classes ( we cannot restrict class being inherited by only few classes out of all the available classes in a package/outsite of a package or do we??!!), there is no use of protected access specifiers for top level classes. Hence it is not allowed.

## 递归

\#Todo#

## 标签

Java允许通过标签（结合关键字`break`和`continue`）实现流程控制，但最好不这样做：

```Java
outer: while (...) {
    inner: for (...) {
        if (...) {
            break inner;
        }
        continue outer;
    }
}
```

## 接口

语言层面上，相较于类，接口有着自己的一些特性。

接口的所有成员变量都是 `public static final` 的，所有的成员方法都是 `public` 的，无论你显式声明与否。如果方法有默认实现则需用 `default` 修饰，称作默认方法，否则即为 `abstract` 的，即便没有显式 `abstract` 修饰。

为什么是这样，引用 SO 上的两个答案感受一下：

>Protected methods are intended for sharing implementation with subclasses. Interfaces have nothing to offer as far as implementation sharing goes, because they have no implementation at all. Therefore all methods on interfaces must be public.

>Because an interface is supposed to mean "what you can see from outside the class". It would not make sense to add non-public methods.

但是这里有个问题，因为 Java 8 支持接口提供默认的方法实现，这样的话，如果一个接口是为了被其它接口继承而设计的，受保护（`protected`）的方法是不是就有意义了呢？当然，这也是一种非常极端的用法，可能并不提倡。

接口本身是 `public abstract` 的，`abstract` 修饰符可以不显式指定。

## 静态

静态代码的真正意义是什么？静态代码是属于类的代码，即便对象不存在，属于类的代码也存在也可访问，只要类已经被加载。当 Java 程序执行时，类的字节码文件被加载到内存，如果没有创建该类的对象，类的实例变量不会被分配内存，实例方法不会被分配入口地址，只有创建该类的对象时（使用 `new` 操作符），类中的实例变量才会被分配内存空间，实例方法才会被分配入口地址，然后执行构造方法中的语句，完成必要的初始化；但是，在类被加载到内存时，类中的静态变量和静态方法等，就被分配了相应的内存空间和入口地址，所以才可以在没有创建对象的情况下调用静态代码。一个直接的推论就是，不允许在静态代码里调用属于实例的代码，因为这时候实例变量可能还没被分配内存空间，实例方法还没被分配入口地址，除非就地创建对象出来。另外，静态变量的内存空间直到程序退出运行才释放所占有的内存。

## 构造过程

就编程可感知的角度来说，当一个类被调用 `new` 操作符进行构造的时候到构造完毕，会经历如下阶段，按顺序分别是：

1. 给静态成员变量分配内存空间并赋初始值；
2. 执行静态代码块；
3. 静态部分构造完毕；
4. 给成员变量分配内存空间并赋初始值；
5. 执行构造方法；
6. 构造完毕。

当只是一个类的静态方法被调用时，会经历如下阶段，按顺序分别是：

1. 给静态成员变量分配内存空间并赋初始值；
2. 执行静态代码块；
3. （静态部分）构造完毕。

**注意**：这时候这个类的对象还没有也不需要被构造出来，但我们仍然把这个准备过程称为广义上的构造。

另外，如果类包含了 `main` 方法，则会在*静态部分*构造完毕的时候，即上述第3步结束后，进入 `main` 方法，开始执行。

## 值传递（pass-by-value）

Java 是值传递，这句话意味着什么？

```Java
public static void main(String[] args) {
    String x = new String("ab");
    change(x);
    System.out.println(x); // ==> it prints "ab".
}

public static void change(String x) {
    x = "cd";
}
```

代码最后打印出“ab”。

![pass by value](thePassByValue.jpeg)

> The variable x contains a reference to the string object. x is not a reference itself! It is a variable that stores a reference(memory address).

> Java is pass-by-value ONLY. When x is passed to the change() method, a copy of value of x (a reference) is passed. ...

注：以上代码、图片和文字来自于 [Program Creek](http://www.programcreek.com/2013/09/string-is-passed-by-reference-in-java)

值传递意味着，不会真正传递任何东西，传递的都是**值的复制品**，这也就意味着每一次传递，虚拟机都会用一个新的变量来存放**值的复制品**。所以对 var2（新的变量） 的重新赋值，不会影响 var1（原始变量）。但是对 var2 所引用的对象的修改（在重新赋值之前），会影响 var1 所引用的对象，因为它们都引用同一个对象。

《Thinking in Java 4th Edition》里面的一段话：

>This brings up the terminology issue, which always seems good for an argument. The term is "pass by value," and the meaning depends on how you perceive the operation of the program. The general meaning is that you get a local copy of whatever you’re passing, but the real question is how you think about what you’re passing. When it comes to the meaning of "pass by value," there are two fairly distinct camps:
>
>1. Java passes everything by value. When you’re passing primitives into a method, you get a distinct copy of the primitive. When you’re passing a handle into a method, you get a copy of the handle. Ergo, everything is pass by value. Of course, the assumption is that you’re always thinking (and caring) that handles are being passed, but it seems like the Java design has gone a long way toward allowing you to ignore (most of the time) that you’re working with a handle. That is, it seems to allow you to think of the handle as "the object," since it implicitly dereferences it whenever you make a method call.
>
>2. Java passes primitives by value (no argument there), but objects are passed by reference. This is the world view that the handle is an alias for the object, so you don’t think about passing handles, but instead say "I'm passing the object." Since you don’t get a local copy of the object when you pass it into a method, objects are clearly not passed by value. There appears to be some support for this view within Sun, since one of the "reserved but not implemented" keywords is byvalue. (There’s no knowing, however, whether that keyword will ever see the light of day.)
>
>Having given both camps a good airing and after saying "It depends on how you think of a handle," I will attempt to sidestep the issue for the rest of the book. In the end, it isn't that important – what is important is that you understand that passing a handle allows the caller’s object to be changed unexpectedly.

其实，对于Java这种纯面向对象的语言，**无所谓值传递还是引用传递**，更*直观*的理解就是**传递的就是那个对象**，所以对传递进来的对象的修改，会反映到原来的对象上；而*赋值操作*并不是对对象的修改，可以看作是*丢弃*当前对象，赋予变量一个新的对象，所以不会影响原来的对象。
