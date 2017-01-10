import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.UserTransaction;

import org.enhydra.jdbc.standard.StandardXADataSource;
import org.objectweb.jotm.Jotm;
import org.objectweb.transaction.jta.TMService;

public class JOTM extends JTADemo {

	private TMService jotm;

	public JOTM() throws NamingException {
		jotm = new Jotm(true, false);
	}

	public static void main(String[] args) throws Exception {
		new JOTM().demoJTA();
	}

	@Override
	protected UserTransaction getUserTransaction() throws Exception {
		return jotm.getUserTransaction();
	}

	@Override
	protected Connection getConnectionA() throws SQLException {
		XADataSource xads = new StandardXADataSource();
		((StandardXADataSource) xads).setDriverName("oracle.jdbc.driver.OracleDriver");
		((StandardXADataSource) xads).setUrl("jdbc:oracle:thin:@localhost:1521:XE");
		((StandardXADataSource) xads).setTransactionManager(jotm.getTransactionManager());
		XAConnection xaconn = xads.getXAConnection("tbeos", "tbeos");
		return xaconn.getConnection();
	}

	@Override
	protected Connection getConnectionB() throws SQLException {
		XADataSource xads = new StandardXADataSource();
		((StandardXADataSource) xads).setDriverName("oracle.jdbc.driver.OracleDriver");
		((StandardXADataSource) xads).setUrl("jdbc:oracle:thin:@localhost:1521:XE");
		((StandardXADataSource) xads).setTransactionManager(jotm.getTransactionManager());
		XAConnection xaconn = xads.getXAConnection("tbeos", "tbeos");
		return xaconn.getConnection();
	}

}
