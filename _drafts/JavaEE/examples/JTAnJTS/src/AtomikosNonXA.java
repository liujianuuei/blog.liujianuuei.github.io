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
