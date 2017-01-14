import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;
import javax.sql.XADataSource;

public class JustDataSource {

    private DataSource dataSource;
    private XADataSource xaDataSource;

    public JustDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JustDataSource(XADataSource xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public XADataSource getXADataSource() {
        return xaDataSource;
    }

    public Connection getConnection() throws SQLException {
        if (this.dataSource != null) {
            return this.dataSource.getConnection();
        } else if (this.xaDataSource != null) {
            return this.xaDataSource.getXAConnection().getConnection();
        } else {
            return null;
        }
    }

}
