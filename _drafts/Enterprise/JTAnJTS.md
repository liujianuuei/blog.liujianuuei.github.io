# JTA and JTS

## 为什么需要事务

事务保证用户操作的 ACID（即原子、一致、隔离、持久）属性。

## 什么是分布式事务

对于只操作单一资源的应用，可以通过本地资源接口（比如[数据库的事务](SQL.md#事务)）实现事务管理；对于跨资源的应用，则必须使用分布式事务（Distributed Transaction）服务以保证用户操作的事务性，即所有资源的操作位于一个全局事务之内。分布式事务的核心是**多个异构本地资源的协调和控制**。分布式事务也叫全局事务。

## JTA

JTA 为 Java Enterprise 提供分布式事务服务，它是一组接口的定义，包括全局事务管理器（Transaction Manager）和一个或多个支持 X/Open XA 协议的本地资源管理器 ( Resource Manager )。本地资源管理器可以被看做任意类型的持久化数据存储服务，由各提供商（数据库，JMS 等）依据规范提供资管理功能；全局事务管理器负责协调多个异构资源。也就是说参与分布式事务的各本地资源本身必须支持 X/Open XA 协议。

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

第一步，就是获取 `UserTransaction`，无论通过什么方式，JNDI 还是直接创建，只要得到 `UserTransaction` 就行；第二步，开始分布式事务；第三步，对涉及到的所有分布式事务资源进行相应修改；第四步，就是整体提交（背后原理就是所谓的**两阶段提交（2PC）**）；最后，如果发生异常等破坏事务性约束的错误，则整体回滚。

一般来说，这个编程模型可以套用到任何 JTA 的提供商的实现上面。下面，我们就 JTA 的各个实现，逐个来应用此模型。

## JTA Provider

在此之前，我们先对上述编程模型做部分加工：

```Java
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    protected void transactA(JustDataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("insert into buyer_private_identity(id, buyer_org, supplier_org, private_id) values(buyer_private_identity_seq.nextval, 109, 110, 'jta3')"); // Can be any DML.
        stmt.close();

        conn.close();
    }

    protected void transactB(JustDataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO dept VALUES (7, 'CMO', 'LONDON')"); // DDL: create table dept(id int, name varchar(50), location varchar(50)); Can be any DML.
        stmt.close();

        conn.close();
    }

    protected abstract UserTransaction getUserTransaction() throws Exception;

    protected abstract JustDataSource getDataSourceA() throws SQLException;

    protected abstract JustDataSource getDataSourceB() throws SQLException;

}
```
可以看到，代码核心部分我们并没有变动。接下来的例子，都是基于该抽象类完成。所有例子都需要 `javax.transaction` 包，也就是 JTA 的支持。示例中用到的数据库实例，需提前构建好，并创建相应的表结构。另外，我们用 Gradle 来管理第三方依赖。

完整的工程可以在[这里](examples/JTAnJTS)找到。

### [SimpleJTA - A Simple Java Transaction Manager](http://simplejta.sourceforge.net/)

```Java
import java.util.Properties;

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
        try {
            simpleJTA.demoJTA();
        } finally {
            simpleJTA.shutdown();
        }
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
    protected JustDataSource getDataSourceA() {
        return new JustDataSource(new SimpleOracleXADataSource("TMGR.1", "jdbc:oracle:thin:@localhost:1521:XE", "tbeos", "tbeos"));
    }

    @Override
    protected JustDataSource getDataSourceB() {
        return new JustDataSource(new SimpleDerbyXADataSource("TMGR.1", "C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb", "app", "app")); // Derby DB, app/aap is default user/password.
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

### [JOTM - Java Open Transaction Manager](http://jotm.ow2.org/)

```Java
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;

import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

public class JOTM extends JTADemo {

    private TMService serv;

    public JOTM() throws NamingException {
        serv = new Jotm(true, false);
    }

    public static void main(String[] args) throws Exception {
        JOTM jotm = new JOTM();
        try {
            jotm.demoJTA();
        } finally {
            jotm.shutdown();
        }
    }

    @Override
    protected UserTransaction getUserTransaction() throws Exception {
        return serv.getUserTransaction();
    }

    @Override
    protected JustDataSource getDataSourceA() throws SQLException {
        XADataSource xads = new StandardXADataSource();
        ((StandardXADataSource) xads).setDriverName("oracle.jdbc.driver.OracleDriver");
        ((StandardXADataSource) xads).setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        ((StandardXADataSource) xads).setUser("tbeos");
        ((StandardXADataSource) xads).setPassword("tbeos");
        ((StandardXADataSource) xads).setTransactionManager(serv.getTransactionManager());
        return new JustDataSource(xads);
    }

    @Override
    protected JustDataSource getDataSourceB() throws SQLException {
        XADataSource xads = new StandardXADataSource();
        ((StandardXADataSource) xads).setDriverName("org.apache.derby.jdbc.EmbeddedDriver");
        ((StandardXADataSource) xads).setUrl("C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb"); // Derby DB
        ((StandardXADataSource) xads).setUser("app"); // default user
        ((StandardXADataSource) xads).setPassword("app"); // default password
        ((StandardXADataSource) xads).setTransactionManager(serv.getTransactionManager());
        return new JustDataSource(xads);
    }

    public void shutdown() {
        if (serv != null) {
            serv.stop();
        }
    }

}
```

依赖：

```Gradle
dependencies {
    compile group: 'javax.transaction', name: 'jta', version: '1.1'
    // JOTM
    compile fileTree(dir:'JOTM/ow2-jotm-dist-2.1.9/lib',include:['*.jar'])

    compile group: 'org.apache.derby', name: 'derby', version: '10.13.1.1'
    compile group: 'com.oracle', name: 'ojdbc6', version: '11.2.0.4.0-atlassian-hosted'
}
```

### [Atomikos TransactionsEssentials](https://www.atomikos.com/Main/TransactionsEssentials)

Atomikos TransactionsEssentials 支持 XA 和 非 XA 两种模型。

非 XA：

```Java
import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

/*
 * read more: https://www.atomikos.com/Documentation/NonXaDataSource
 */
public class AtomikosNonXA extends JTADemo {

    public static void main(String[] args) throws Exception {
        new AtomikosNonXA().demoJTA();
    }

    @Override
    protected UserTransaction getUserTransaction() throws Exception {
        return new UserTransactionImp();
    }

    @Override
    protected JustDataSource getDataSourceA() {
        AtomikosNonXADataSourceBean ds = new AtomikosNonXADataSourceBean();
        ds.setUniqueResourceName("oracledb");
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        ds.setUser("tbeos");
        ds.setPassword("tbeos");
        ds.setPoolSize(1);
        return new JustDataSource(ds);
    }

    @Override
    protected JustDataSource getDataSourceB() {
        AtomikosNonXADataSourceBean ds = new AtomikosNonXADataSourceBean();
        ds.setUniqueResourceName("derbydb");
        ds.setUrl("jdbc:derby:C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb;"); // Derby DB
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        ds.setUser("app"); // default user
        ds.setPassword("app"); // default password
        ds.setPoolSize(1);
        return new JustDataSource(ds);
    }

}
```

XA：

```Java
import java.util.Properties;

import javax.transaction.UserTransaction;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.jdbc.AtomikosDataSourceBean;

/*
 * read more: https://www.atomikos.com/Documentation/ConfiguringJdbc#XA_and_non_45XA_transactions
 */
public class Atomikos extends JTADemo {

    public static void main(String[] args) throws Exception {
        new Atomikos().demoJTA();
    }

    @Override
    protected UserTransaction getUserTransaction() throws Exception {
        return new UserTransactionImp();
    }

    @Override
    protected JustDataSource getDataSourceA() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("oracledb");
        dataSource.setXaDataSourceClassName("oracle.jdbc.xa.client.OracleXADataSource");
        Properties props = new Properties();
        props.setProperty("URL", "jdbc:oracle:thin:@localhost:1521:XE");
        props.setProperty("user", "tbeos");
        props.setProperty("password", "tbeos");
        dataSource.setXaProperties(props);
        dataSource.setPoolSize(1);
        return new JustDataSource(dataSource);
    }

    @Override
    protected JustDataSource getDataSourceB() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("derbydb");
        dataSource.setXaDataSourceClassName("org.apache.derby.jdbc.EmbeddedXADataSource");
        Properties props = new Properties();
        props.put("databaseName", "C:/SAP/Programs/db-derby-10.13.1.1-bin/db/simplejtadb"); // Derby DB
        //props.put("createDatabase", "create");
        props.setProperty("user", "app"); // default user
        props.setProperty("password", "app"); // default password
        dataSource.setXaProperties(props);
        dataSource.setPoolSize(1);
        return new JustDataSource(dataSource);
    }

}
```

依赖：

```Gradle
dependencies {
    compile group: 'javax.transaction', name: 'jta', version: '1.1'
    // Atomikos
    compile group: 'com.atomikos', name: 'transactions-jdbc', version: '4.0.4'
    compile group: 'com.atomikos', name: 'transactions-jms', version: '4.0.4'

    compile group: 'org.apache.derby', name: 'derby', version: '10.13.1.1'
    compile group: 'com.oracle', name: 'ojdbc6', version: '11.2.0.4.0-atlassian-hosted'
}
```

## JTS

JTA 是开发人员用于使用全局事务的接口。与之对应，Java 事务服务（JTS），是分布式事务提供商用于实现事务的接口。

## 最后

分布式事务的编写涉及很多方面，除了逻辑还有容错等，上述例子只是最简单的对分布式事务的示意。

就这样。
