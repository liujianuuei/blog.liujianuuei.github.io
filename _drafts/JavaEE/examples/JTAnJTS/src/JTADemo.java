import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

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

    protected void transactA(JustDataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("insert into buyer_private_identity(id, buyer_org, supplier_org, private_id) values(buyer_private_identity_seq.nextval, 109, 110, 'jta3')"); // Can be any DML.
        stmt.close();

        conn.close();
    }

    protected void transactB(JustDataSource ds) throws SQLException {
        Connection conn = ds.getConnection();

        Statement stmt = conn.createStatement();
        stmt.executeUpdate("INSERT INTO dept VALUES (7, 'CMO', 'LONDON')"); // DDL: create table dept(id int, name varchar(50), location varchar(50)); Can be any DML.
        stmt.close();

        conn.close();
    }

    protected abstract UserTransaction getUserTransaction() throws Exception;

    protected abstract JustDataSource getDataSourceA() throws SQLException;

    protected abstract JustDataSource getDataSourceB() throws SQLException;

}
