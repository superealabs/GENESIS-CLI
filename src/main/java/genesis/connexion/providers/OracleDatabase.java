package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleDatabase extends Database {
    @Override
    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        Class.forName(getDriver());
        String url = getJdbcUrl(credentials);
        Connection connection = DriverManager.getConnection(url, credentials.getUser(), credentials.getPwd());
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    protected String getJdbcUrl(Credentials credentials) {
        return String.format("jdbc:oracle:thin:@%s:%s:%s",
                credentials.getHost(),
                getPort(),
                credentials.getDatabaseName());
    }
}
