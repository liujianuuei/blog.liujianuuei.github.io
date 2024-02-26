# 对象复制

在实际编程过程中，我们常常要遇到这种情况：有一个对象A，在某一时刻A中已经包含了一些有效值，此时可能会需要一个和A完全相同新对象B，并且此后对B任何改动都不会影响到A中的值，也就是说，A与B是两个独立的对象，但B的初始值是由A对象确定的。也即`x.clone() != x`为真，`x.clone().getClass() == x.getClass()`应该为真，但不是必须的，`x.clone().equals(x)`应该也为真，但也不是必须的。

为了达到这样的效果，我们有很多方法，最直接的无非就是新建一个同类型的对象（成员变量也全部重新按照目标对象创建），然后依次把所有的成员变量都依照目标对象的值赋给新创建的对象。这就是对象复制：我们确实有两个（地址不同的）对象，而且它们有着同样的值（成员变量）。

我们把上述操作放在一个方法内部，起名叫`public Object clone()`，然后放到要被复制的对象所属的类里，这就是标准的**深复制**。我们来看代码示例：

```Java
public class Attribute {

    private String name;
    private String displayName;
    private boolean required;
    private boolean searchable;
    private int position;
    private DataType dataType;
    private boolean multiValued;
    private String operator;
    //Add for BMDM-BMDM-443
    private boolean nillable;

    private List<String> valueList;
    // * * * * * * * * Attachments * * * * * * * * * * */
    private Map<String, DataHandler> attachments;
    // * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //For import event details
    private String type;
    private String description;

    //For query record
    private boolean caseSensitive;

    ... // Remainder omitted

    public Attribute clone() {
        Attribute attribute = null;
        try {
            attribute = new Attribute();
            ... // 针对基本类型和不可变类型，这里进行逐域赋值
            // 接下来手动复制可变成员对象
            attribute.valueList = new ArrayList<String>(this.valueList);
            attribute.attachments = new HashMap<String, DataHandler>();
            Iterator<Entry<String, DataHandler>> it = this.attachments.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, DataHandler> entry = it.next();
                DataSource dataSource = new FileDataSource(entry.getValue().getDataSource().getName());
                DataHandler data = new DataHandler(dataSource);
                attribute.addAttachment(entry.getKey(), data);
            }
        } catch (CloneNotSupportedException e) {
        }
        return attribute;
    }

}
```
从逻辑角度，这段代码没有任何问题，满足所有关于对象复制的检查，但问题在于违背了复制所蕴含的本质（创建一个新实例，同时复制内部的数据结构，且这个过程中没有调用构造器）。

## Java提供的对象复制机制

针对对象复制，Java给我们提供了`native`的支持，即`Object`类里的`clone()`方法。所以，运用原生的对象复制机制，修改上面的代码：

```Java
    @Override
    public Attribute clone() {
        attribute = (Attribute) super.clone();
        // 针对基本类型和不可变类型，不再需要逐域赋值，因为Java已经帮我们完成了这些操作
        ... // Remainder omitted
    }
```

**注意**：覆盖clone方法不是必须的，只是出于约定俗成的考虑。

如果，我们尝试去运行新修改后的代码，会得到一个`java.lang.CloneNotSupportedException`异常，这是因为如果我们要调用Object的clone方法（`super.clone();`），就必须还要让该对象实现一个`Cloneable`接口。最后，完整的代码：

```Java
public class Attribute implements Cloneable {

    private String name;
    private String displayName;
    private boolean required;
    private boolean searchable;
    private int position;
    private DataType dataType;
    private boolean multiValued;
    private String operator;
    //Add for BMDM-BMDM-443
    private boolean nillable;

    private List<String> valueList;
    // * * * * * * * * Attachments * * * * * * * * * * */
    private Map<String, DataHandler> attachments;
    // * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //For import event details
    private String type;
    private String description;

    //For query record
    private boolean caseSensitive;

    ... // Remainder omitted

    @Override
    public Attribute clone() { // 协变返回类型作为泛型，可以返回被覆盖方法的返回类型的子类型。
        Attribute attribute = null;
        try {
            attribute = (Attribute) super.clone();
            // 接下来手动复制可变成员对象
            attribute.valueList = new ArrayList<String>(this.valueList);
            attribute.attachments = new HashMap<String, DataHandler>();
            Iterator<Entry<String, DataHandler>> it = this.attachments.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, DataHandler> entry = it.next();
                DataSource dataSource = new FileDataSource(entry.getValue().getDataSource().getName());
                DataHandler data = new DataHandler(dataSource);
                attribute.addAttachment(entry.getKey(), data);
            }
        } catch (CloneNotSupportedException e) {
        }
        return attribute;
    }

}
```

**注意**：Cloneable接口并不要求实现任何方法，也就是说是一个空接口。为什么会这样？这就是《EJ 2nd Edition》提到的接口的极端非典型用法，它决定了Object类里native方法clone的行为。

这里，还有个问题，如果对象是继承自另外一个对象呢？我们必须确保**所有父类都要实现有效的clone方法**（不必须声明实现Cloneable接口），这样才能确保对象复制机制正确运作。

还有个问题，这时候还需要我们显式地复制可变对象吗？答案是肯定的，Java提供的native复制机制是所谓的**浅复制**，也就是不会深度复制可变对象，而只会在新的实例里复制它们的地址，我们要**显式地逐层地（树遍历式的）复制所有可变对象**。这里，就又引出一个问题，因为我们无法给你final修饰的成员域赋值，所以：Java提供的对象复制机制与引用可变对象的final域的正常用法是不兼容的。

如同构造器一样，clone方法不应该在构造的过程中，调用任何非final的和非private的方法。如果，调用了一个被覆盖的方法，那么很可能发生意想不到的结果。所以，上面代码的：

```Java
attribute.addAttachment(entry.getKey(), data); // addAttachment 是公有非final的方法
```

应该改成：

```Java
attribute.attachments.put(entry.getKey(), data);
```

这样，就避免了子类修改`addAttachment`方法，导致不可预料的行为。

**总结**

当我们打算使一个类支持复制的时候，我们应该做什么呢？下面给出可参考的步骤：

1. 如果该类继承自其它类，则必须确保所有父类都有实现良好的`clone`方法。
2. 为该类声明实现`Cloneable`接口。
3. 覆盖`clone`方法：
    0. 修改返回类型为`public`。
    1. 调用`super.clone();`获得浅复制的对象。
    2. 强制类型转换成该类的类型。
    3. 显式复制所有可变成员对象如果有的话。（不要调用任何非`final`的和非`private`的方法）
    4. 修改`clone`方法的返回类型为该类的类型。
    5. 内部捕获`CloneNotSupportedException`异常，摒弃异常抛出声明。

如果使一个专门为了继承而设计的类支持复制，则我们需要模仿Object的clone实现行为：

1. 如果该类继承自其它类，则必须确保所有父类都有实现良好的`clone`方法。
2. 覆盖`clone`方法：
    0. 保持返回类型为`protected`。
    1. 调用`super.clone();`获得浅复制的对象。
    2. 强制类型转换成该类的类型。
    3. 显式复制所有可变成员对象如果有的话。（不要调用任何非`final`的和非`private`的方法）
    4. 修改`clone`方法的返回类型为该类的类型。
    5. 声明抛出`CloneNotSupportedException`异常。

这样做可以使子类具有实现或不实现Cloneable接口的自由，就仿佛它们直接扩展了Object一样。

**注意**：基本类型或者不可变成员域不需要显式复制这条规则也有例外，比如，代表唯一标识符的域，或者其它具有唯一识别作用的域，都需要重新赋值。

## 复制构造器 & 复制工厂

除了典型Java提供的复制机制，我们还可以通过提供一个**复制构造器（copy constructor）**或**复制工厂（copy factory）**来达到同样的目的。我们来看复制构造器的实现：

```Java
public class Attribute {

    private String name;
    private String displayName;
    private boolean required;
    private boolean searchable;
    private int position;
    private DataType dataType;
    private boolean multiValued;
    private String operator;
    //Add for BMDM-BMDM-443
    private boolean nillable;

    private List<String> valueList;
    // * * * * * * * * Attachments * * * * * * * * * * */
    private Map<String, DataHandler> attachments;
    // * * * * * * * * * * * * * * * * * * * * * * * * * * */

    //For import event details
    private String type;
    private String description;

    //For query record
    private boolean caseSensitive;

    ... // Remainder omitted

    // copy constructor
    public Attribute(Attribute sourceAttribute) {
        Attribute attribute = new Attribute();
        ... // 针对基本类型和不可变类型，这里进行逐域赋值，比如：attribute.name = sourceAttribute.name;
        // 接下来手动复制可变成员对象
        attribute.valueList = new ArrayList<String>(sourceAttribute.valueList); // 任何时候集合类型返回值都不要为null
        attribute.attachments = new HashMap<String, DataHandler>();
        Iterator<Entry<String, DataHandler>> it = sourceAttribute.attachments.entrySet().iterator(); // 任何时候集合类型返回值都不要为null
        while (it.hasNext()) {
            Entry<String, DataHandler> entry = it.next();
            DataSource dataSource = new FileDataSource(entry.getValue().getDataSource().getName());
            DataHandler data = new DataHandler(dataSource);
            attribute.attachments.put(entry.getKey(), data);
        }
        return attribute;
    }

}
```

可以看到，复制构造器的实现几乎和我们开头第一个版本的clone方法的实现一样，不同的就是变量值来源不同。复制工厂的实现也类似，只不过是放在静态工厂方法里。

这样做的一个好处就是不会与final域的正常用法发生冲突，另外一个好处就是允许客户选择复制的实现类型而不是原封不动的复制，比如`new TreeSet(new HashSet());`，这其实已经超出了对象复制的范畴，属于对象转换，就不详述了。

另外一点是出于安全角度的考虑，比如下面的代码（也可以查看[不可变类](ImmutableClass.md)下面的一个例子）：

```Java
public class Date extends java.util.Date {

    @Override
    public Object clone() {
        return new java.sql.Date(System.currentTimeMillis()); // 恶意返回其它类型的对象
    }

}
```

在客户端，任何可以接受java.util.Date的地方，都可以接受这个恶意构造的子类，而它的clone方法返回的是另一个类型的对象，无疑会导致致命的错误，如果弃用自带的clone方法，而是通过提供的复制构造器来复制对象，则从语法层面就可以避免这攻击。同时，也可以看出，上面提到的实现一个健壮的clone方法的*第5点*是多么重要。引用《EJ 2nd Edition》的一条建议：

```
对于参数类型可以被不可信任方子类化的参数，请不要使用clone方法进行保护性复制。
```

## 其它

[反序列化](Serialization.md)为我么提供了一种语言之外的对象复制机制。通过反序列化，我们得到的肯定是一个新的实例。
