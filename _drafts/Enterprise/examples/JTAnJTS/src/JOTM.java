

import java.sql.SQLException;

import javax.sql.DataSource;
import javax.transaction.UserTransaction;

public class JOTM extends JTADemo {
	
	public static void main(String[] args) throws Exception {
		new JOTM().demoJTA();
	}

	@Override
	protected UserTransaction getUserTransaction() throws Exception {
		return null;
	}

	@Override
	protected void transactA(DataSource ds) throws SQLException {

	}

	@Override
	protected void transactB(DataSource ds) throws SQLException {

	}

	@Override
	protected DataSource getDataSourceA() {
		return null;
	}

	@Override
	protected DataSource getDataSourceB() {
		return null;
	}

}
