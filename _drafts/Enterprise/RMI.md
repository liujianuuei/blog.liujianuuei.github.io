# RMI - http://www.grpc.io TODO

如何调用另一个 JVM 中的对象，换句话说对远程分布式**对象**的透明访问？我们知道，我们不能取得另一个 JVM 堆的对象引用，只能引用和当前代码同一堆空间的对象。答案就是**远程方法调用**（Remote Method Invocation），以下简称 RMI。

RMI 是客户端程序如同调用本地方法一样，调用远程方法（即真正的服务所在）的技术。位于本地的远程对象的代理（远程代理）会通过网络发送真正的方法调用，网络和 I/O 的确是存在的，但是对客户端透明。

RMI 的核心就是**远程代理**。远程代理就好比远程对象的本地代表，也就是代理模式在远程对象上的应用。所谓**远程对象**就是存在于不同的 JVM 堆中的对象，而其代理对象就是供本地其它对象调用的**本地代表**，其内部会把本地调用行为转发到远程对象上再返回远程对象的返回结果给本地调用者。这样，就好比调用本地对象一样，调用了远程对象，代理会处理所有网络通信的底层细节。最新的 Java 不再需要我们手动生成这些代理类，而是系统自动在运行时生成[动态代理](../Languages/DesignPattern/TheProxyPattern.md)。

## 如何将对象变成服务

换句话说，RMI 的本质就是**将对象变成可以远程访问的服务**。所谓服务就是提供某种功能的接口（这里的接口指 Java 语言层面的接口和类）。可以远程访问的服务，就是可以通过远程即跨网访问提供某种功能的接口。

## Java 为我们提供了什么

Java 已经内置远程调用的功能以及所有运行时的基础设施，比如查找远程服务（lookup services）等，我们只需要实现符合 RMI 规范的业务代码即可。

首先，我们来看客户端的代码：

```Java
SomeRemoteService someRemoteService = (SomeRemoteService) Naming.lookup("rmi://host:port#/SomeRemoteService"); // service 的概念我们已经很熟悉了，就是具备某种功能的接口（包括类）；这里的 service 还需要继承自 java.rmi.Remote 接口，以表示它是一个远程服务接口，从而可以被 RMI 系统识别到。
// Do something using service interface. // 当我们拿到 service 接口，就可以做任何我们想做的事情。
```

客户端通过主机地址和端口号找到服务，当客户端得到这个服务，就可以做任何想做的事情。具体来看，客户端程序其实是从服务端的 RMI registry 中查找到的服务。关于 registry 我们来看下官方文档的[说明](http://docs.oracle.com/javase/7/docs/technotes/tools/solaris/rmiregistry.html)：

> A remote object registry is a bootstrap naming service that is used by RMI servers on the same host to bind remote objects to names. Clients on local and remote hosts can then look up remote objects and make remote method invocations.

我们再来看服务端的代码：

```Java
SomeRemoteService someRemoteService = new SomeRemoteServiceImpl(); // 远程服务接口的实现
LocateRegistry.createRegistry(port#); // 绑定服务端口号，可用 rmiregistry 命令代替。
Naming.bind("rmi://host:port#/SomeRemoteService", someRemoteService); // 绑定服务地址。
```

**注**：关于 `rmiregistry` 命令的更多信息，请查看 [rmiregistry - The Java Remote Object Registry](http://docs.oracle.com/javase/6/docs/technotes/tools/solaris/rmiregistry.html)。

服务端提供服务实现，并注册到 RMI registry 中，这样客户端就可以查找到该服务并调用。

其它代码：

```Java
public interface SomeRemoteService extends Remote { // Remote 是一个标记接口，没有任何方法，注意这并不是接口的正常用法。
    public SomeEntity someMethod() throws RemoteException;
}

public class SomeRemoteServiceImpl extends UnicastRemoteObject implements SomeRemoteService {

    public SomeRemoteServiceImpl() throws RemoteException {

    }

    @Override
    public SomeEntity someMethod() {
        // Construct and return Entity, or do something else as needed.
    }

}

public class SomeEntity implements Serializable {

    private static final long serialVersionUID = 2641473377521635318L;

    // Some fields

    // Some methods
}
```

注意，由于实体类要通过网络传输，也即在非运行环境中存在，而且还要被还原到运行环境中继续被调用，所以实体类需要被指定成可序列化的，事实上**远程方法调用涉及到的所有参数变量和返回值都必须是可序列化的**。

另外，如果使用的是最新的 Java，则我们不再需要生成 Stubs 和 Skeletons。继承 `UnicastRemoteObject` 类并调用其构造方法，就会默认动态生成代理类和远程对象通信，也就是把一个普通的对象变成了可以远程访问的服务。请看下面来自官方文档的[说明](https://docs.oracle.com/javase/8/docs/api/java/rmi/server/UnicastRemoteObject.html)：

> public class UnicastRemoteObject extends RemoteServer

> Used for exporting a remote object with JRMP and obtaining a stub that communicates to the remote object. Stubs are either generated at runtime using dynamic proxy objects, or they are generated statically at build time, typically using the rmic tool.

> **Deprecated: Static Stubs.** Support for statically generated stubs is deprecated. This includes the API in this class that requires the use of static stubs, as well as the runtime support for loading static stubs. Generating stubs dynamically is preferred, using one of the five non-deprecated ways of exporting objects as listed below. Do not run rmic to generate static stub classes. It is unnecessary, and it is also deprecated.

> There are six ways to export remote objects:

> 1. Subclassing UnicastRemoteObject and calling the UnicastRemoteObject() constructor.
> 2. Subclassing UnicastRemoteObject and calling the UnicastRemoteObject(port) constructor.
> 3. Subclassing UnicastRemoteObject and calling the UnicastRemoteObject(port, csf, ssf) constructor.
> 4. Calling the exportObject(Remote) method. **Deprecated.**
> 5. Calling the exportObject(Remote, port) method.
> 6. Calling the exportObject(Remote, port, csf, ssf) method.

## 最后

RMI 其实可以被看作是 RPC 的 Java 版本。RPC 即远程过程调用，相当于 IPC（Inter-process communication）在网络上的扩展。IPC 使得本地进程间可以相互通信，RPC 使得处于网络上的进程间也可以相互通信。RPC 框架有很多，比如 [CORBA](JavaIDL.md)、[RMI](RMI.md)、[JAX-RPC](WebServices.md)、Hessian、Thrift、RESTful Web Services 等等。

JAX-RPC，也就是基于 SOAP 协议的 [Web Services](WebServices.md)。JAX-RPC 在网络上传递的是基于 XML 的 SOAP 消息。RMI 则直接**跨网传递 Java 对象**，所以其先天具有面向对象的优势，为开发分布式应用系统提供了纯 Java 的解决方案。

RMI 使用 Java 远程消息交换协议 JRMP（Java Remote Messaging Protocol）进行通信。但由于JRMP 是专为 Java 对象制定的，因此，RMI 不能与用非 Java 语言开发的系统进行通信，这是 RMI 的最大弊端，不过可以通过 JNI 绕开这一限制。
