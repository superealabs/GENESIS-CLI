package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLServerDatabase extends Database {

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
        return String.format("jdbc:sqlserver://%s:%s;databaseName=%s;user=%s;password=%s;encrypt=%s;trustServerCertificate=%s;",
                credentials.getHost(),
                getPort(),
                credentials.getDatabaseName(),
                credentials.getUser(),
                credentials.getPwd(),
                credentials.isUseSSL(),
                credentials.isTrustCertificate());
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
