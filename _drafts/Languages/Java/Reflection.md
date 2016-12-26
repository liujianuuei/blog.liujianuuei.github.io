# 反射

## 为什么要用反射？

反射（Reflection）是一种在**运行时**取得类的**元数据**（类的方法、成员变量等构成类的信息，以及超类的信息等）的机制。同时，在获得这些元数据的基础上可以对类进行操作，比如创建一个实例（通过动态机制，而非`new`）、调用其中的方法等。

需要通过反射机制实例化并调用的类，往往是**在编译时还未知的类（实现稍后由第三方提供）**，比如数据库驱动类。数据库驱动类是由数据库厂商提供的，我们在编写数据库访问程序的时候是基于JDBC框架，编写统一的程序，这时候我们甚至还没有把数据库驱动加到classpath里，等我们的程序编译通过运行时，再依照具体的数据库把相应的驱动加进来。反射就为我们提供了这种在**运行时加载某个编译时还未知的类并实例化**的功能，如下面代码：

```Java
Class.forName("oracle.jdbc.driver.OracleDriver")
```

接下来`DriverManager`就可以通过`getConnection`方法取得数据库连接，进而进行数据库访问。至于DriverManager如何得知我们动态加载了驱动类的，查看驱动的实现，在`OracleDriver`类里，我们可以看到下面的代码，这就一目了然了：

```Java
static {
    ...

    defaultDriver = new oracle.jdbc.OracleDriver();
    DriverManager.registerDriver(defaultDriver);

    ...
}
```

## Class对象

Class是反射的故事起源，针对任何你想探勘的类，唯有先为它产生一个Class对象，然后才可以调用Class的诸多元数据相关的方法，比如通过`getConstructor().newInstance()`创建实例等。通过以下方法，我们可以得到这个与每个Object关联的Class对象：

```Java
Class.forName("fully qualified class name"); // this method will also cause the class to be initialized
X.class; // X is a class
x.getClass(); // x is an instance of class X
```

另外，除了用于反射的方法之外，Class 对象还提供了一些一般的工具方法，比如  `public T cast(Object obj)`、`public <U> Class<? extends U> asSubclass(Class<U> clazz)` 等。

## 最后

反射的一个用例就是解析Retention是RUNTIME的[注解](Annotation.md)，比如Google 的DI框架Guice就是依次原理来实现的[对象创建](ObjectCreation.md)。
