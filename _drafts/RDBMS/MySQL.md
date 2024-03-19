# MySQL

MySQL 是一个被普遍使用的关系型（SQL）数据库。其架构核心点在于"处理"（服务器）和"存储"的分离，这样可以按需替换不同存储引擎（比如，InnoDB 是 MySQL 默认的事务型引擎）。服务器和存储通过低级 API 交互。

每个客户端（应用）连接都会在服务器进程中占用一个线程。需要注意的是，服务器也会缓存线程，不会频繁的创建销毁线程。

![](rdbms-mysql-arch.jpg)

后续所有讨论都基于 InnoDB 存储引擎。

## 并发控制

参阅[数据库编程的并发控制](../JavaSE/Java/Concurrency.md#数据库编程的并发控制)。

### 锁

**锁的分类**

- 表锁
- 读锁
- 写锁
- 行锁，比如 Oracle、MySQL InnoDB 引擎、Oceanbase
- 共享锁（Shared Lock），也叫读锁：允许获得共享锁的事务读取数据，阻止其它事务获得相同数据集的排它锁，但仍允许其它事务获取共享锁
- 排它锁（Exclusive Lock），也叫写锁：允许获得排它锁的事务更新数据，阻止其它事务取得相同数据集的共享锁与排它锁

- 更新锁： 预定的排它锁， 允许其他事务读，但不允许其它事务获取共享锁或排它锁或更新锁；修改数据的一种方式是首先获得一个共享锁，读取数据，然后将共享锁升级为排他锁，再执行修改操作；这样如果有两个或多个事务同时对数据集获得了共享锁，在修改数据时，这些事务都要将共享锁升级为排它锁；这时，这些事务都不会释放共享锁，而是一直等待对方释放，这样就造成了死锁；如果一个数据集在修改前直接申请更新锁，在数据修改时再升级为排它锁，就可以避免死锁
- 页锁： 粒度介于行级锁和表级锁中间的一种锁，比如 MySQL BDB 引擎

- 乐观锁： 乐观锁是一种特殊类型的锁，顾名思义，就是很乐观，每次去拿数据的时候都认为别人不会修改，所以，不会上锁，但是在更新的时候会判断一下在此期间别人有没有更新这个数据，可以使用版本号等机制，为数据增加一个版本标识，在基于数据库表的版本解决方案中，一般是通过为数据库表增加一个“version”字段来实现；每次更新把这个字段加1，读取数据的时候把 version 读出来，更新的时候比较 version，如果还是开始读取的 version 就可以更新了，如果现在的 version 比之前读取出来的 version 大，说明有其他事务更新了该数据，并增加了版本号，这时候得到一个无法更新的通知，用户自行根据这个通知来决定怎么处理，比如重新开始一遍；这里的关键是判断 version 和更新 version 两个动作需要作为一个原子操作执行；如果，现有数据库不能增加新字段，则可以考虑用既有字段或者所有字段，但思想是一样的

- 死锁，以及如何系统判断死锁：
  - 超时法：如果某个事物的等待时间超过指定时限，则判定为出现死锁
  - 等待图法：如果事务等待图中出现了回路，则判断出现了死锁

**加锁的方式**

隐式的加锁： 对于 Update、Delete、insert 语句，InnoDB 会自动给涉及的数据集隐式的加上排它锁。

显式的加锁： 除了隐式锁以外，还可以通过显式的方式获取共享锁或者排它锁。

共享锁加锁：`select * from table where ... lock in sahre mode;`

排他锁加锁：`select * from table where ... for update;`

另外，在 InnoDB 中，行锁是通过给索引上的索引项加锁来实现的，根据针对 SQL 语句检索条件的不同，加锁又有以下三种情形：

Record lock：对索引项加锁

Gap lock：对索引项之间的间隙加锁

Next-key lock：对记录前面的间隙加锁

InnoDB 针对索引进行加锁的实现方式意味着，只有通过索引条件检索或者更新数据，InnoDB 才使用行级锁，否则 InnoDB 将会使用表锁更新数据，极大地降低数据库的并发执行性能。

另外，除了通过锁的方式解决事务冲突，还可以事先通过 DML/DDL 分析将可能冲突的事务排队后执行（Oceanbase）。

## 事务

事务具有四种属性（ACID）：

- 原子性/A
- 一致性/C
- 隔离性/I
- 持久性/D

事务的问题：

① 更新丢失（Lost Update）：两个并行操作，后进行的操作覆盖掉了先进行操作的操作结果，被称作更新丢失。

② 脏读：一个事务在提交之前，在事务过程中修改的数据，被其他事务读取到了。

③ 不可重复读：一个事务在提交之前，在事务过程中读取以前的数据却发现数据发生了改变。

④ 幻读：一个事务按照相同的条件重新读取以前检索过的数据时，却发现了其他事务插入的新数据。

对于上面这几个问题，更新丢失可以通过应用程序完全避免，而其他的问题则通过调整数据库**事务隔离级别**来解决，事务的隔离机制的实现手段之一就是利用**锁**。

### 隔离级别

隔离级别高意味着可靠性高，但并发量低，而隔离级别低则意味着可靠性低，但并发量高。事务的隔离级别有如下四种：



## 索引

**一级索引/主键索引**

一级索引也就是主键索引，MySQL 默认会为主键创建**聚簇索引**，叶子结点存储的不是主键值而是整行实际数据。

注：一张表只能有一个聚簇索引。

**二级索引/辅助索引**

二级索引是用户通过 SQL 显式创建的索引。二级索引是非聚簇索引，叶子节点存储主键值（返回结果时，通过主键回表获取整行实际数据）。

唯一索引属于二级索引。

**索引实现**

B-Tree 索引：适用于范围、多列组合查询（最左匹配）等。

Hash 索引：适用于等值查询，在有大量重复键值情况下，哈希索引的效率也是极低的，因为存在所谓的哈希碰撞问题。

A unique constraint is a type of column restriction within a table, which dictates that all values in that column must be unique though may be null.

To ensure that a column is UNIQUE and cannot contain null values, the column must be specified as NOT NULL.

A unique constraint is defined at the time a table is created. A unique constraint allows null values. Initially, this may seem like a contradiction, but a null is the complete absence of a value (not a zero or space). Thus, it is not possible to say that the value in that null field is not unique, as nothing is stored in that field. A null value cannot be compared to an actual value. For example, the Queen of America cannot be compared to the Queen of England because the Queen of America is a null that does not exist.

## 优化

MySQL 服务器会对提交的的 SQL 进行优化。用户还可以通过 `hint` 提示服务器的优化策略，以及通过 `explain` 查看优化过程。

除了索引之外，服务器还支持查询缓存，通过缓存提高查询效率，但根据经验命中缓存的概率不高。

## 复制（高可用）

一般一主库两从库一备库架构。主库负责写和事务内（或事务后压秒范围内）查询，从库负责读（查询），从库辅助离线查询等。

## 扩展（分库分表）

分库分表（sharding）是 MySQL 横向扩容的一种常见方式。

## ORM——SQL Mapper

ORM(Object-Relational Mapping) 允许通过 OOP 的方式访问关系型数据库。

**MyBatis - SQL Mapper Framework for Java**

略。
