package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
    protected String getJdbcUrl(Credentials credentials) {
        String port;
        if (credentials.getPort() != null)
            port = credentials.getPort();
        else port = getPort();
        return String.format("jdbc:oracle:thin:@//%s:%d/%s",
                credentials.getHost(),
                Integer.parseInt(port),
                credentials.getDatabaseName());
    }

    @Override
    public List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                String tableSchema = tables.getString("TABLE_SCHEM");

                boolean isSystemTable = false;
                for (String schema : super.getExcludeSchemas()) {
                    if (schema.equalsIgnoreCase(tableSchema)) {
                        isSystemTable = true;
                        break;
                    }
                }

                if (!isSystemTable) {
                    tableNames.add(tableName);
                }
            }
        }

        return tableNames;
    }

}
