# 嵌套类

**嵌套类（nested class）**是指被定义在另一个类的内部的类。嵌套类又分四种：**静态成员类（static member class）**、**非静态成员类（nonstatic member class）**、**匿名类（anonymous class）**和**局部类（local class）**。除了第一种之外，其它三种又被称为**内部类（inner class）**。

#### 外部性

静态成员类如果被声明为外部可见（比如，用public修饰），则和任何其他普通类没有区别。它就是普通类，只是碰巧被声明在另一个类的内部而已。这时候，你可以像使用其他任何普通类一样来使用外部可见的静态成员类，下面两种方式都可以：

```Java
new A(); // A is static member class of X
new X.A(); // X is enclosing class of A
```

**注意**：一个普通类（外部类）是不能被`static`修饰符锁修饰的。

非静态成员类则不同，非静态成员类即便外部可见，也必须通过外围类的一个实例来调用（即便，不是外部显式调用非静态成员类，而是外围类的某个实例方法内部调用非静态成员类的时候，这种关联关系也会被自动建立起来）：

```Java
x.new A(); // where x is an instance of enclosing class of A
```

否则报如下编译错误：

```
No enclosing instance of type BootstrappingDialog is accessible. Must qualify the allocation with an enclosing instance of type BootstrappingDialog (e.g. x.new A() where x is an instance of BootstrappingDialog).
```

所以，从这一点上也可以看出，非静态成员类其实是一个内部类，这就是为什么我们把非静态成员类划归内部类的原因，而不是静态成员类，虽然静态成员类通过私有修饰符也可以变成外部不可访问，但这是可见性范畴，属另一码事。

**注意**：由于额外的域外围实例的关联，非静态成员类的开销比静态成员类的要大，所以一般情况，我们都应该选择使用静态成员类，但静态成员类的问题在于其是静态的，所以遵从和外围类的任何其它静态成员（比如静态方法）一样的访问性，比如不能访问非静态的成员变量等，所以，这也是一个考虑的因素。

## 私有静态成员类

我们现在来单独看一种类型的成员类——私有静态成员类。我们知道相对于其它三种内部类，静态成员类其实是一种外部类（普通类），那为什么还要把它声明成私有，从而避免在外部被调用呢？

私有静态成员类的一种常见用法是用来代表外围类所代表的对象的**组件**。比如HashMap的静态成员类Entry，再比如[这个实例](https://github.com/prairie/Eemory/blob/master/com.prairie.eemory/src/com/prairie/eemory/ui/BootstrappingDialog.java)。总之，基于效率和可见性的考虑，我们可以优先考虑使用私有静态成员类。

## 非成员类

非静态成员类的使用并不是那么广泛，更多的时候，我们使用匿名类和局部类。匿名类和局部类也叫非成员类的内部类，就是说他们是直接定义在方法里的，也就是直到用的时候，才定义并实例化。

当且仅当匿名类出现在非静态的环境中时，它才有外围实例。匿名类不能做任何需要命名类的事情，比如实现一个接口。匿名类的一种常见用法是动态的创建**函数对象**，另一种常见用法是创建**过程对象**，第三种常见用法是在静态工厂方法的内部。

局部类相较匿名类使用更少一些。在任何可以声明局部变量的地方，都可以声明局部类，并且局部类也遵守同样的作用域规则。

和匿名类一样，局部类不能包含静态成员。The field y cannot be declared static in a non-static inner type, unless initialized with a constant expression

## 最后

引用《EJ 2nd edition》里的一段话，作为结语：

```
there are four different kinds of nested classes, and each has its place. If a nested class needs to be visible outside of a single method or is too long to fit comfortably inside a method, use a member class. If each instance of the member class needs a reference to its enclosing instance, make it nonstatic; otherwise, make it static. Assuming the class belongs inside a method, if you need to create instances from only one location and there is a preexisting type that characterizes the class, make it an anonymous class; otherwise, make it a local class.
```
