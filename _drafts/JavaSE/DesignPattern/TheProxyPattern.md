# 设计模式之代理模式

## 什么是代理模式

所谓代理模式就是代理者代理被代理者做被代理者要做的事情。代理模式非常类似现实生活当中的委托诉讼代理人：

![](theProxyPattern.png)

发生民事纠纷后，我们不直接起诉或者和法院打交道，而是委托给经验丰富的律师作为代理人替我们去诉讼。我们原本要和法院直接打交道，但现在我们避开法院和代理律师打交道，代理律师再和法院之间发生关系，这样就相当于我们和法院发生关系一样。可以看到，从始自终，**我们要做的事情是没有变化的**，都是诉讼，只是谁直接去和下游发生关系的问题，这是代理模式的根本。

就软件而言，也是同样的意思。代理模式是为另一个对象提供一个替身或占位符以控制对这个对象的访问。使用代理模式创建**代理对象**，让代理对象控制对**被代理对象**的访问，被代理的对象可以是远程的对象、创建开销大的对象或需要安全控制的对象。

从语言层面看，代理类与被代理类有同样的接口，代理类主要负责为被代理类预处理消息、过滤消息、把消息转发给被代理类，以及事后处理消息等。代理类与被代理之间通常会存在关联关系，一个代理类的对象与一个被代理类的对象关联，代理类的对象本身并不真正实现服务，而是通过调用被代理类的对象的相关方法，来提供特定的服务。

按照代理的创建方式，代理类可以分为两种:

+ 静态代理：由程序员创建或特定工具自动生成源代码，再对其编译。在程序运行前，代理类的.class文件就已经存在了。
+ 动态代理：在程序运行时，运用[反射](Reflection.md)机制动态创建而成。

## 静态代理

我们来看静态代理的实现，首先定义*行为类*并面向接口编程，会有下面的代码：

```Java
public interface SomeInterface {
    public void doSomething();
}
```

```Java
public class SomeInterfaceImpl implements SomeInterface {

    @Override
    public void doSomething() {
        ...
    }

}
```

一个接口定义行为，一个实现类实现行为。客户端代码毫无疑问一般的做法是：

```Java
SomeInterface some = new SomeInterfaceImpl();
some.doSomething();
```

这就是亲力亲为，自己直接提供特定的服务。如果应用了静态代理模式会是什么样呢？我们首先为要代理的对象创建一个代理类：

```Java
public class Proxy implements SomeInterface {

    private SomeInterface target; // 被代理对象

    public Proxy(SomeInterface target) {
        this.target = target;
    }

    @Override
    public void doSomething() {
        // * Before really do something. 神奇就发生在这里和
        target.doSomething();
        // * After really did something. 这里。
    }

}
```

它和行为类实现统一接口，唯一不同的是要传入行为类的实例作为被代理的对象，也是行为的直接实现者。客户端代码是什么样呢：

```Java
SomeInterface target = new SomeInterfaceImpl();
Proxy proxy = new Proxy(target);
proxy.doSomething();
```

看上去，似乎并没有什么不同，代理类的方法还是调用被代理类的相同的方法。但关键就在于代理类调用被代理类的同名方法之前和之后的那段时间，就是代码中标注**\***的地方（如果找不到，Ctrl+f全文搜索\*）。当代理类能拿到被代理类的方法调用之前和之后的时间，那么就可以额外施加任何控制，比如在行为类的所有方法被调用之前在控制台打印出信息，这就是**AOP**的核心，也是动态代理的原理。

## 动态代理

静态代理的局限在于每一个代理类只能为一个接口服务，这样一来程序开发中必然会产生过多的代理，而且，所有的代理操作除了调用的方法不一样之外，其他的操作都一样，则此时肯定是重复代码。解决这一问题最好的做法是可以通过一个代理类完成全部的代理功能，那么此时就必须使用动态代理完成。如下代码：

```Java
final SomeInterface someInterfaceImpl = new SomeInterfaceImpl();

SomeInterface proxy = (SomeInterface) java.lang.reflect.Proxy.newProxyInstance(someInterfaceImpl.getClass().getClassLoader(), someInterfaceImpl.getClass().getInterfaces(), new InvocationHandler() {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // * Before really do something. 神奇就发生在这里和
        return method.invoke(someInterfaceImpl, args);
        // * After really did something. 这里。
    }
});

proxy.doSomething();
```

可以看到，Java提供的动态代理是通过[反射](Reflection.md)方式，传入的是Class对象，返回的是和静态代理一样的和被代理对象接口相同的子对象，即我们上面为静态代理显式实现的代理类，对于动态代理这个代理类会自动地**通过反射被动态生成**。拦截被代理对象的方法的代码位于一个称作InvocationHandler的接口抽象方法内，就像静态代理我们可以在真正的行为被执行前后附加任何第三方的行为。由于处理的是Class对象，所以任何类都可以被代理，不再需要我们为每一个要被代理的类单独写一个代理类，所以称作动态代理。

## 还有什么问题

Java自带的动态代理依靠接口实现，如果有些类并没有实现接口，则不能使用Java自带的动态代理，[cglib](https://github.com/cglib/cglib)为我们弥补了这一缺陷。cglib是针对类来实现代理的，他的原理是对指定的目标类生成一个子类，并覆盖其中方法实现增强，但因为采用的是继承，所以也不能对final修饰的类进行代理。具体详情，请另行查阅。
