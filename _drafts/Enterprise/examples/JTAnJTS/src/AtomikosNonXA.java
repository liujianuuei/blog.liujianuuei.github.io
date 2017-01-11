import javax.sql.DataSource;
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
    protected DataSource getDataSourceA() {
        AtomikosNonXADataSourceBean ds = new AtomikosNonXADataSourceBean();
        ds.setUrl("jdbc:oracle:thin:@localhost:1521:XE");
        ds.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        ds.setUniqueResourceName("oracledb");
        ds.setPoolSize(1);
        return ds;
    }

    @Override
    protected DataSource getDataSourceB() {
        AtomikosNonXADataSourceBean ds = new AtomikosNonXADataSourceBean();
        ds.setUrl("jdbc:derby:db;create=true");
        ds.setDriverClassName("org.apache.derby.jdbc.EmbeddedDriver");
        ds.setUniqueResourceName("derbydb");
        ds.setPoolSize(1);
        return ds;
    }

}
