# JTA and JTS

## 为什么需要事务

事务保证用户操作的 ACID（即原子、一致、隔离、持久）属性。

## 什么是分布式事务

对于只操作单一数据源的应用，可以通过本地数据源接口（比如[数据库的事务](SQL.md#事务)）实现事务管理；对于跨数据源的应用，则必须使用分布式事务（Distributed Transaction）服务以保证用户操作的事务性，即所有事务源位于一个全局事务之内。分布式事务的核心是**多个异构本地事务源的协调和控制**。

## JTA

JTA 为 Java Enterprise 提供分布式事务服务，它是一组接口的定义，包括全局事务管理器（Transaction Manager）和一个或多个支持 XA 协议的本地资源管理器 ( Resource Manager )。本地资源管理器可以被看做任意类型的持久化数据存储服务，由各提供商（数据库，JMS 等）依据规范提供事务源管理功能；全局事务管理器负责协调多个异构事务源，由开发人员使用。使用分布式事务，需要实现和配置所支持 XA 协议的事务源，如 JMS、JDBC 数据库连接池等。

编写分布式事务应用非常考验编程技术，不同的应用场景，其逻辑也不尽相同。这里，我精简出一个最主体的编程模型，仅供说明从编程角度，JTA 如何运行的。简化后的模型如下：

```Java
UserTransaction userTx = getUserTransaction(); // 自定义方法，目的是获取 UserTransaction，有可能是通过 JNDI 获取，或者直接创建。
try {
    userTx.begin(); // 开始全局事务。

    transactA(getDataSourceA()); // 两个方法都是自定义方法，可以根据需要自行变化，唯一目的就是拿到事务源，做相应改动。
    transactB(getDataSourceB()); // 两个方法都是自定义方法，可以根据需要自行变化，唯一目的就是拿到事务源，做相应改动。

    userTx.commit(); // 提交全局事务。
} catch (Exception e) {
    userTx.rollback();
}
```

第一步，就是获取 `UserTransaction`，无论通过什么方式，JNDI 还是直接创建，只要得到 `UserTransaction` 就行；第二步，开始分布式事务；第三步，对涉及到的所有分布式事务资源进行相应修改；第四步，就是整体提交；最后，如果发生异常等破坏事务性约束的错误，则整体回滚。

一般来说，这个编程模型可以套用到任何 JTA 的提供商的实现上面。下面，我们就 JTA 的各个实现，逐个来应用此模型。

## JTA Provider

在此之前，我们先对上述编程模型做部分加工：

```Java
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

public abstract class JTADemo {

    public final void demoJTA() throws Exception {
        UserTransaction userTx = getUserTransaction();
        try {
            userTx.begin();

            transactA(getDataSourceA());
            transactB(getDataSourceB());

            userTx.commit();
            System.out.println("Success");
        } catch (Exception e) {
            userTx.rollback();
            System.out.println("Exception");
        }
    }

    protected abstract UserTransaction getUserTransaction() throws Exception;

    protected abstract void transactA(DataSource ds) throws SQLException;

    protected abstract void transactB(DataSource ds) throws SQLException;

    protected abstract DataSource getDataSourceA();

    protected abstract DataSource getDataSourceB();

}
```
可以看到，代码核心部分我们并没有任何变动，只是使其更像是程序。接下来的例子，都是基于该抽象类完成。所有例子都需要 `javax.transaction` 包，也就是 JTA 的支持。另外，我们用 Gradle 语法来表示第三方依赖。

完整的工程可以在

### [SimpleJTA - A Simple Java Transaction Manager](http://simplejta.sourceforge.net/)

```Java
package me.jta.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.simplejta.tm.datasource.SimpleDerbyXADataSource;
import org.simplejta.tm.datasource.SimpleOracleXADataSource;
import org.simplejta.tm.ut.SimpleUserTransaction;

/*
 * Read http://simplejta.sourceforge.net/usermanual.html for more details.
 */
public class SimpleJTA extends JTADemo {

    private SimpleUserTransaction userTx;

    public static void main(String[] args) throws Exception {
        SimpleJTA simpleJTA = new SimpleJTA();
        simpleJTA.demoJTA();
        simpleJTA.shutdown();
    }

    @Override
    protected UserTransaction getUserTransaction() throws SystemException {
        if (userTx == null) {
            Properties props = new Properties();
            props.setProperty("TMGR.id", "TMGR.1");
            // props.setProperty("TMGR.recoveryUser", "recouser");
            // props.setProperty("TMGR.recoveryPassword", "recouser");
            props.setProperty("TLOG.driver", "DERBY.EMBEDDED");
            props.setProperty("TLOG.url", "C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb"); // Derby DB
            props.setProperty("TLOG.user", "app"); // app is default user.
            props.setProperty("TLOG.password", "app");
            userTx = new SimpleUserTransaction(props);
        }
        return userTx;
    }

    @Override
    protected void transactA(DataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("insert into buyer_private_identity(id, buyer_org, supplier_org, private_id) values(buyer_private_identity_seq.nextval, 109, 110, 'jta')"); // Can be any DML.
        stmt.close();

        conn.close();
    }

    @Override
    protected void transactB(DataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO dept VALUES (1, 'BSD', 'LONDON')"); // SQL: create table(id int, name varchar(50), location varchar(50)); Can be any DML.
        stmt.close();

        conn.close();
    }

    @Override
    protected DataSource getDataSourceA() {
        return new SimpleOracleXADataSource("TMGR.1", "jdbc:oracle:thin:@localhost:1521:XE", "tbeos", "tbeos"); // Existing user in oracle instance. You should have oracle instance in advance.
    }

    @Override
    protected DataSource getDataSourceB() {
        return new SimpleDerbyXADataSource("TMGR.1", "C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb", "app", "app"); // app is default user.
    }

    public void shutdown() {
        if (userTx != null) {
            userTx.shutdown();
        }
    }

}
```

依赖：

```Gradle
dependencies {
    compile group: 'javax.transaction', name: 'jta', version: '1.1'
    // SimpleJTA
    compile files('SimpleJTA/simplejta-1.07.jar')

    compile group: 'org.apache.derby', name: 'derby', version: '10.13.1.1'
    compile group: 'com.oracle', name: 'ojdbc6', version: '11.2.0.4.0-atlassian-hosted'
}
```

### JOTM - Java Open Transaction Manager

### Atomikos TransactionsEssentials
