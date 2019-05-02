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
