# JTA and JTS

## 为什么需要事务

事务保证用户操作的 ACID（即原子、一致、隔离、持久）属性。

## 什么是分布式事务

对于只操作单一数据源的应用，可以通过本地数据源接口（比如[数据库的事务](SQL.md#事务)）实现事务管理；对于跨数据源的应用，则必须使用分布式事务（Distributed Transaction）服务以保证用户操作的事务性，即所有事务源位于一个全局事务之内。分布式事务的核心是**多个异构本地事务源的协调和控制**。

## Java 为我们提供了什么

JTA 为 Java Enterprise 提供分布式事务服务，它是一组接口的定义，包括全局事务管理器（Transaction Manager）和一个或多个支持 XA 协议的本地资源管理器 ( Resource Manager )。本地资源管理器可以被看做任意类型的持久化数据存储服务，由各提供商（数据库，JMS 等）依据规范提供事务源管理功能；全局事务管理器负责协调多个异构事务源，由开发人员使用。使用分布式事务，需要实现和配置所支持 XA 协议的事务源，如 JMS、JDBC 数据库连接池等。

编写分布式事务应用非常考验编程技术，不同的应用场景，其逻辑也不尽相同。这里，我精简出一个最主体的编程模型，仅供说明从编程角度，JTA 如何运行的。简化后的模型如下：

```Java
UserTransaction userTx = getUserTransaction(); // 自定义方法，目的是获取 UserTransaction，有可能是通过 JNDI 获取，或者直接创建。
try {
    userTx.begin();

    transactA(getDataSourceA()); // 两个方法都是自定义方法，可以根据需要自行变化，唯一目的就是拿到事务源，做相应改动。
    transactB(getDataSourceB()); // 两个方法都是自定义方法，可以根据需要自行变化，唯一目的就是拿到事务源，做相应改动。

    userTx.commit();
} catch (Exception e) {
    userTx.rollback();
}
```

第一步，就是获取 `UserTransaction`，无论通过什么方式，JNDI 还是直接创建，只要得到 `UserTransaction` 就行；第二步，开始分布式事务；第三步，对涉及到的所有分布式事务资源进行相应修改；第四步，就是整体提交；最后，如果发生异常等破坏事务性约束的错误，则整体回滚。

一般来说，这个编程模型可以套用到任何 JTA 的提供商的实现上面。下面，我们就 JTA 的各个实现，逐个来应用此模型。

## SimpleJTA - A Simple Java Transaction Manager

## JOTM - Java Open Transaction Manager

## Atomikos TransactionsEssentials
