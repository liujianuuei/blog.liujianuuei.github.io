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
