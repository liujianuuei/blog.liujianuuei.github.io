

import java.sql.SQLException;

import javax.sql.DataSource;
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

    protected abstract UserTransaction getUserTransaction() throws Exception;

    protected abstract void transactA(DataSource ds) throws SQLException;

    protected abstract void transactB(DataSource ds) throws SQLException;

    protected abstract DataSource getDataSourceA();

    protected abstract DataSource getDataSourceB();

}
