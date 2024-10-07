package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgreSQLDatabase extends Database {

    @Override
    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        setCredentials(credentials);
        Class.forName(getDriver());
        String url = getJdbcUrl(credentials);
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }

    @Override
    protected String getJdbcUrl(Credentials credentials) {
        String port;
        if (credentials.getPort()!=null)
            port = credentials.getPort();
        else port = getPort();
        return String.format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                credentials.getHost(),
                port,
                credentials.getDatabaseName(),
                credentials.getUser(),
                credentials.getPwd());
    }

    @Override
    public List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }


}

