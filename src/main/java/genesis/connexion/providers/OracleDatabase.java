package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDatabase extends Database {
    @Override
    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        setCredentials(credentials);
        Class.forName(getDriver());
        String url = getJdbcUrl(credentials);
        Connection connection = DriverManager.getConnection(url, credentials.getUser(), credentials.getPwd());
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    public Connection getConnection(Credentials credentials, String url) throws ClassNotFoundException, SQLException {
        setCredentials(credentials);
        Class.forName(getDriver());
        Connection connection = DriverManager.getConnection(url, credentials.getUser(), credentials.getPwd());
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    protected String getJdbcUrl(Credentials credentials) {
        String port;
        if (credentials.getPort() != null)
            port = credentials.getPort();
        else port = getPort();
        return String.format("jdbc:oracle:%s:@//%s:%s/%s",
                getDriverType(),
                credentials.getHost(),
                port,
                credentials.getServiceName());
    }
}
