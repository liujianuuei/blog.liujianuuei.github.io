import java.util.Properties;

import javax.sql.DataSource;
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
    protected DataSource getDataSourceA() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("oracledb");
        ds.setXaDataSourceClassName("oracle.jdbc.xa.client.OracleXADataSource");
        Properties props = new Properties();
        props.setProperty("user", "tbeos");
        props.setProperty("password", "tbeos");
        props.setProperty("URL", "jdbc:oracle:thin:@localhost:1521:XE");
        ds.setXaProperties(props);
        ds.setPoolSize(5);
        return ds;
    }

    @Override
    protected DataSource getDataSourceB() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setXaDataSourceClassName("org.apache.derby.jdbc.EmbeddedXADataSource");
        Properties props = new Properties();
        props.put("databaseName", "derbydb");
        props.put("createDatabase", "create");
        ds.setXaProperties(props);
        ds.setUniqueResourceName("derbydb");
        ds.setPoolSize(10);
        return ds;
    }

}
