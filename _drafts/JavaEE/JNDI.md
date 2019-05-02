# JNDI

## Naming Service

Naming Service 提供一种通过名字定位对象的服务。一个命名服务系统最核心的功能是通过某种命名范式，把对象索引起来，即建立起名字到对象的绑定，而且客户端可以通过提供的名字找到相应的对象。

我们来具体看下常见的 Naming Service（引用自 [JavaWorld: JNDI overview, Part 1: An introduction to naming services](http://www.javaworld.com/article/2076888/core-java/jndi-overview--part-1--an-introduction-to-naming-services.html)）：

- **LDAP (Lightweight Directory Access Protocol)**: Developed by the University of Michigan; as its name implies, it is a lightweight version of DAP (Directory Access Protocol), which in turn is part of X.500, a standard for network directory services. Currently, over 40 companies endorse LDAP.
- **COS (Common Object Services) Naming**: The naming service for CORBA applications; allows applications to store and access references to CORBA objects.
- **DNS (Domain Name System)**: The Internet's naming service; maps people-friendly names (such as www.etcee.com) into computer-friendly IP (Internet Protocol) addresses in dotted-quad notation (207.69.175.36). Interestingly, DNS is a distributed naming service, meaning that the service and its underlying database is spread across many hosts on the Internet.
- **NIS (Network Information System) and NIS+**: Network naming services developed by Sun Microsystems. Both allow users to access files and applications on any host with a single ID and password.

以及 Java 自带的 Java Remote Method Invocation (RMI) Registry。下文我们就用 Java 自带的 RMI 来演示具体的代码实例。

## What Is JNDI

Java Naming and Directory Interface (JNDI) 是 Java 提供的一套访问 Naming Service 的 API，其允许通过名字查找和访问绑定的对象。

我们先来看下 JNDI 的架构图（引用自 [The Java™ Tutorials: Overview of JNDI](http://docs.oracle.com/javase/tutorial/jndi/overview/index.html)）：

![JNDI Arch](theJNDIArch.gif)

JNDI 是一套通用框架，需要具体的 Naming Service 提供商提供基于 SPI 的实现。JDK 已经包含了如下 SPI 实现：

- Lightweight Directory Access Protocol (LDAP)
- Common Object Request Broker Architecture (CORBA) Common Object Services (COS) name service
- Java Remote Method Invocation (RMI) Registry
- Domain Name Service (DNS)

## When To Use JNDI

Java Enterprise 一般通过如下几种方式使用 JNDI（引用自 [JavaWorld: J2EE or J2SE? JNDI works with both](http://www.javaworld.com/article/2074186/jndi/j2ee-or-j2se--jndi-works-with-both.html)）：

- As a means to store application configuration information in a centralized, hierarchical database;
- As a repository for live objects shared among application components, which can run in different JVMs or on different systems, e.g. RMI;
- As an interface to existing directory services like LDAP (using a provider specific to that external service);
- As a lightweight, hierarchical database for storing transient application state.

### 实例

接下里，我们讨论如何使用 JNDI。一切的基础，需要先拿到  initial context，然后就可以对 name-object binding 进行增删改查。但一般的应用场景是，分为服务端和客户端，服务端负责名字和对象的绑定，并维护这种关系，客户端通过名字获取相应的对象。

#### Java Remote Method Invocation (RMI) Registry

我们使用 [Java Remote Method Invocation (RMI) Registry](RMI.md) 作为我们的 JNDI 实现。

服务端代码：

```Java
OracleDataSource oracleDataSource = new OracleDataSource(); // 使用 Oracle 的 JDBC 实现，需添加相应 jar 文件。
oracleDataSource.setURL("jdbc:oracle:thin:system/manager@localhost:1521:XE"); // 使用 Oracle 数据库。

Hashtable<String, String> env = new Hashtable<>();
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory"); // RMI can be considered as a JNDI provider, actually RMI is a of JNDI.
env.put(Context.PROVIDER_URL, "rmi://localhost:1099");
Context ctx = new InitialContext(env);

ctx.rebind("java:/comp/env/jdbc/oracledb", oracleDataSource); // RegistryContext: object to bind must be Remote, Reference, or Referenceable
```

客户端代码：

```Java
Hashtable<String, String> env = new Hashtable<>();
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
env.put(Context.PROVIDER_URL, "rmi://localhost:1099");
Context ctx = new InitialContext(env);

DataSource ds = (DataSource) ctx.lookup("java:/comp/env/jdbc/oracledb"); // JNDI hierarchy is similar to file system.

conn = ds.getConnection();
Statement stmt = conn.createStatement();
ResultSet rs = stmt.executeQuery("select sysdate from dual");
while (rs.next()) {
    System.out.println(rs.getObject("sysdate"));
}
```

在运行程序之前，需先启动 RMI Registry：

```Batch
\>rmiregistry 1099
```

#### File System Service Provider

再来看一个例子，我们用 File System Service Provider 作为 JNDI 实现：

```Java
Hashtable<String, String> env = new Hashtable<>();
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
env.put(Context.PROVIDER_URL, "file:///C:/SAP/JNDI-­Directory"); // regular file path on file system
Context context = new InitialContext(env);

((Reference) context.lookup("/config/App/Dispatcher/Log/Error")).getClassName(); // return enabled which is configured in .bindings file
```

由于是基于文件系统的实现，我们可以看到底层 bindings 的存储结构：

```.bindings
#This file is used by the JNDI FSContext.
#Mon Dec 26 19:11:06 CST 2016
/config/App/Dispatcher/Log/Error/ClassName=enabled
```

这就是 JNDI 用作应用配置的例子。这个 .bindings 文件可以手动修改，但也可以通过程序生成：

```Java
Hashtable<String, String> env = new Hashtable<>();
env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
env.put(Context.PROVIDER_URL, "file:///C:/SAP/JNDI­-Directory");
Context context = new InitialContext(env);
context.rebind("/config/App/Dispatcher/Log/Error", new Reference("enabled"));
```

File System Service Provider 是第三方的实现（一个 jar 文件）。

## 最后

JNDI 的层级模型非常类似文件系统，因此，除了 `InitialContext`，还提供了 `InitialDirContext` 便于目录操作。更多细节，请参考 [The Java™ Tutorials: Trail: Java Naming and Directory Interface](http://docs.oracle.com/javase/tutorial/jndi/)。
