
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
