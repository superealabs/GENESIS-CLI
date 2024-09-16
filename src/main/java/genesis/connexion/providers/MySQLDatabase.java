package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLDatabase extends Database {

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
        return String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&useSSL=%s&allowPublicKeyRetrieval=%s",
                credentials.getHost(),
                getPort(),
                credentials.getDatabaseName(),
                credentials.getUser(),
                credentials.getPwd(),
                credentials.isUseSSL(),
                credentials.isAllowPublicKeyRetrieval());
    }

    @Override
    public List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();

        try (Statement statement = connection.createStatement();
             ResultSet tables = statement.executeQuery("SHOW TABLES")) {
            while (tables.next()) {
                String tableName = tables.getString(1);
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }


}
