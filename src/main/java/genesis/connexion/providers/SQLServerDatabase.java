package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLServerDatabase extends Database {

    @Override
    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        Class.forName(getDriver());
        String url = getJdbcUrl(credentials);
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    protected String getJdbcUrl(Credentials credentials) {
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s",
                credentials.getHost(),
                getPort(),
                credentials.getDatabaseName(),
                credentials.getUser(),
                credentials.getPwd());
    }
}
