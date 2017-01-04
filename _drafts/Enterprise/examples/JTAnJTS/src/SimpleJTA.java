
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
        stmt.executeUpdate("insert into buyer_private_identity(id, buyer_org, supplier_org, private_id) values(buyer_private_identity_seq.nextval, 109, 110, 'jta3')"); // Can be any DML.
        stmt.close();

        conn.close();
    }

    @Override
    protected void transactB(DataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO dept VALUES (3, 'BSD', 'LONDON')"); // SQL: create table(id int, name varchar(50), location varchar(50)); Can be any DML.
        stmt.close();

        conn.close();
    }

    @Override
    protected DataSource getDataSourceA() {
        return new SimpleOracleXADataSource("TMGR.1", "jdbc:oracle:thin:@localhost:1521:XE", "tbeos", "tbeos"); // Existing user in oracle instance.
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
