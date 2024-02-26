# 不可变类

## 什么是不可变类

所谓**不可变类**就是由该类创建的对象的**值或约束**一旦创建之后不可更改的特性，比如`String`类，即是不可变类，由不可变类创建的对象称为不可变对象。不可变类一般可以运用享元模式或其他模式共享一个**等价**的对象。基本类型数据都是不可变的。

对不可变对象的修改，如果允许的话，都会创建一个新的对象，每次对一个字符串的修改都会返回一个新创建的对象（某种意义上正确，因为享元模式的运用，有可能不是新创建的对象，但绝对是和当前对象不同的另一个对象）。

## 自己构造一个不可变类

程序员自己构造一个不可变类，并不是想象的那么容易。最容易忽略的就是要构造的**不可变类引用了可变类**，而没有对可变类进行保护性复制（defensive copy），而导致的域泄露。接下来，我们动手构造一个不可变类，来感官体验一下，构造一个不可变类需要注意的几点。

我们定义一个表示**时期**的类，它包含开始日期和结束日期两个时间点，也即成员变量，这类我们要做成不可变的：

```Java
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
        if (start.compareTo(end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
        }
        this.start = start;
        this.end = end;
    }

    public Date start() {
        return start;
    }

    public Date end() {
        return end;
    }

    ... // Remainder omitted
}
```

很直观地可以发现，如果我们要设计一个不可变类，要满足以下两点：

(1). 类必须是不可被子类修改的，也即被`final`修饰。
(2). 不能提供`setter`方法，以及类似效果的用以修改成员变量的方法。

那么，满足了这两点之外，还有什么问题呢？看上面的代码，由于Date类是可变的，而我们又提供了访问成员变量的`getter`方法，这就导致成员变量可以被外部修改，同样的问题存在于构造方法。所以，第三点就是：

(3). 对所有从客户端得到（比如构造器传入的参数等）或者返回到客户端的可变对象（比如getter方法暴露出的成员变量）都要进行**保护性复制**。

我们来看，修改后的代码，这里对象复制用的是类似复制构造器的方式：

```Java
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
    	this.start = new Date(start.getTime()); // 使用复制构造器，而不是clone方法，因为start是外部传入参数，clone可能被恶意修改
    	this.end = new Date(end.getTime()); // 使用复制构造器，而不是clone方法，因为end是外部传入参数，clone可能被恶意修改
    	if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + " after " + end);
    	}
    }

    public Date start() {
    	return new Date(start.getTime()); // 这里也可以用clone方法，因为这里我们确认成员变量是真正的java.util.Date对象
    }

    public Date end() {
    	return new Date(end.getTime()); // 这里也可以用clone方法，因为这里我们确认成员变量是真正的java.util.Date对象
    }

    ... // Remainder omitted
}
```

除了保护性复制之外，另一种构造不可变类的方式就是:

(4). 在对象内部数据结构中，尽可能**使用不可变的对象**。

这样也就不用再进行保护性复制。上面的代码，因为Date类是可变的，我们保存它的毫秒值替代Date对象本身：

```Java
public final class Period {
    private final long start;
    private final long end;

    /**
    * @param start the beginning of the period
    * @param end the end of the period; must not precede start
    * @throws IllegalArgumentException if start is after end
    * @throws NullPointerException if start or end is null
    */
    public Period(Date start, Date end) {
    	this.start = start.getTime();
    	this.end = end.getTime();
    	if (this.start > this.end) {
            throw new IllegalArgumentException(start + " after " + end);
    	}
    }

    public Date start() {
    	return new Date(start);
    }

    public Date end() {
    	return new Date(end);
    }

    ... // Remainder omitted
}
```

这里有个延伸的问题，对于任何类（即便不是不可变类）如果有从客户端得到或者返回到客户端的可变对象，而且如果你不能接受对象内数据结构被修改的话，就需要考虑进行保护性复制，或者通过转化成不可变对象进入内部数据结构，从而切断与外界的直接联系。那些专门为了返回其它类的对象的类除外，比如工厂类。还有，这样做有些时候也需要考虑性能的开销。

还有一点:

(5). 如果上述类要**支持序列化**，则针对反序列化作为一个隐含构造器的事实，也需要像正常的构造器或者方法那样，进行保护性复制。

更多序列化相关的细节，请参考[序列化](Serialization.md)主题。这里我们来看一下最后的实现：

```Java
// readObject method with validity checking
private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();

    // Defensively copy our mutable components
    start = new Date(start.getTime());
    end = new Date(end.getTime());

    // Check that our invariants are satisfied
    if (start.compareTo(end) > 0) {
        throw new InvalidObjectException(start +" after "+ end);
    }
}
```

给上面的Period类添加一个readObject方法，以保证针对反序列化进行约束检查和保护性复制。这样，才得到一个真正的不可变类，即便反序列化这种语言之外的机制也不能破坏它。

## 类的约束

类的约束同样也是一种不可变性。比如，我们对传入类内部的参数有某种限制，上面的Period类就是个很好的例子。对约束的检查，我们要针对类本身的变量，而不是针对原始的传入的参数。这是因为一种被称为TOCTOU攻击的存在。

另外，实例构造过程中（构造方法内部），不应该调用该对象的任何非final和非private的方法。否则，子类的重写可能导致不如预期的结果。同样的道理，如果传入的是参数类型（**针对任何方法都适用**）可以被不可信任方子类化的参数对象，也不应该使用那些可被子类重写的方法。特别的，如果传入对象类型和被传入对象类型相同，即访问性控制的**类内部**，则应该直接调用传入对象的成员变量，而不是通过getter方法。比如下面的来自实际项目的代码（略作删改）：

```Java
public class GeomPoint {

    private int x;
    private int y;

    public GeomPoint(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public GeomPoint(final GeomPoint point) {
        x = point.x; // do not use getX()
        y = point.y; // do not use getY()
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public move(final GeomPoint point) {
        x = point.x; // do not use getX()
        y = point.y; // do not use getY()
    }

    ... // Remainder omitted
}
```

同样，如果支持序列化，则针对反序列化，也要考虑上述适用于普通方法和参数的约束性检查。

## final关键字

最后，我们来探讨一下`final`关键字的作用。final是不可再更改之意，所以被final修饰，就意味着一旦取得值（针对基本类型）或者取得对象引用（针对类类型），就不可以再更改：

+ final 不能用来修饰接口，接口是要被实现的。
+ 被 final 修饰的类，不能被子类化，即不能被其他类继承。
+ 被 final 修饰的方法不能被子类重写。
+ 被 final 修饰的基本类型成员变量不能被二次赋值，只能在声明的时候赋值。
+ 被 final 修饰的成员对象不能在非构造器的普通方法中二次初始化，只能在声明的时候初始化，或**在构造器里初始化**。
+ 被 final 修饰的局部变量（包括基本类型变量和对象引用）不能被二次赋值，只能在声明的时候赋值。
+ 被 final 修饰的成员变量不占用内存。

就这样。
