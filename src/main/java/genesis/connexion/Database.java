package genesis.connexion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.model.TableMetadata;
import genesis.connexion.providers.MySQLDatabase;
import genesis.connexion.providers.OracleDatabase;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.connexion.providers.SQLServerDatabase;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import utils.FileUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public abstract class Database {
    private int id;
    private String driverName;
    private String driverVersion;
    private String name;
    private String driverType;
    private String serviceName;
    private Map<Integer, String> connectionString;
    private Map<Integer, String> daoName;
    private String driver;
    private String port;
    private Map<String, String> types;
    private List<String> excludeSchemas;
    private Credentials credentials;
    private Map<String, Object> databaseMetadata;
    private Map<String, Framework.Dependency> dependencies;

    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        setCredentials(credentials);
        Class.forName(getDriver());
        String url = getJdbcUrl(credentials);
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }

    public Connection getConnection(Credentials credentials, String url) throws ClassNotFoundException, SQLException {
        setCredentials(credentials);
        Class.forName(getDriver());
        Connection connection = DriverManager.getConnection(url);
        connection.setAutoCommit(false);
        return connection;
    }

    protected abstract String getJdbcUrl(Credentials credentials);

    public TableMetadata getEntity(Connection connection, Credentials credentials, String entityName, Language language) throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setTableName(entityName);
        tableMetadata.initialize(connection, credentials, this, language);
        return tableMetadata;
    }

    public List<TableMetadata> getEntities(Connection connection, Credentials credentials, Language language) throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        return tableMetadata.initializeTables(null, connection, credentials, this, language);
    }

    public List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet tables = metaData.getTables(null, credentials.getSchemaName(), "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }


    public Map<String, Object> getDatabaseMetadataHashMap(Credentials credentials) {
        Map<String, Object> databaseMetadata = new HashMap<>();

        databaseMetadata.put("host", credentials.getHost());
        databaseMetadata.put("port", credentials.getPort());
        databaseMetadata.put("database", credentials.getSchemaName());
        databaseMetadata.put("username", credentials.getUser());
        databaseMetadata.put("password", credentials.getPwd());
        databaseMetadata.put("useSSL", String.valueOf(credentials.isUseSSL()));
        databaseMetadata.put("allowPublicKeyRetrieval", String.valueOf(credentials.isAllowPublicKeyRetrieval()));
        databaseMetadata.put("driverType", driverType);
        databaseMetadata.put("serviceName", serviceName);

        return databaseMetadata;
    }
/*
    private Connection getOrCreateConnection(Connection connection, Credentials credentials) throws SQLException {
        if (connection != null) {
            return connection;
        }
        try {
            return getConnection(credentials);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Failed to create database connection", e);
        }
    }

    private PreparedStatement prepareStatement(Connection connection, Credentials credentials, String entityName) throws SQLException {
        String query = buildQuery(credentials, entityName);
        return connection.prepareStatement(query);
    }

    private String buildQuery(Credentials credentials, String entityName) {
        String query = getTablesQuery.replace("[databaseName]", credentials.getSchemaName());
        if (!entityName.equals("*")) {
            query += String.format(addEntitiesQuery, entityName);
        }
        return query;
    }

    private TableMetadata createEntityFromResult(ResultSet result) throws SQLException {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setTableName(result.getString("table_name"));
        return tableMetadata;
    }
*/
}
