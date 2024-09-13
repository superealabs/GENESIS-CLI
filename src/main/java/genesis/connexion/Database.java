package genesis.connexion;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import genesis.config.langage.Language;
import genesis.connexion.providers.MySQLDatabase;
import genesis.connexion.providers.OracleDatabase;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.connexion.providers.SQLServerDatabase;
import genesis.model.TableMetadata;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Setter
@Getter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "name"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = MySQLDatabase.class, name = "mysql"),
        @JsonSubTypes.Type(value = PostgreSQLDatabase.class, name = "postgresql"),
        @JsonSubTypes.Type(value = OracleDatabase.class, name = "oracle"),
        @JsonSubTypes.Type(value = SQLServerDatabase.class, name = "sqlServer")
})
public abstract class Database {
    private int id;
    private String name;
    private String driver;
    private String port;
    private HashMap<String, String> types;
    private List<String> excludeSchemas;

    public abstract Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException;

    protected abstract String getJdbcUrl(Credentials credentials);

    public TableMetadata getEntity(Connection connection, Credentials credentials, String entityName, Language language) throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setTableName(entityName);
        tableMetadata.initialize(connection, credentials,  this, language);
        return tableMetadata;
    }

    public List<TableMetadata> getEntities(Connection connection, Credentials credentials, Language language) throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        return tableMetadata.initializeTables(null, connection, credentials, this, language);
    }

    public abstract List<String> getAllTableNames(Connection connection) throws SQLException;

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
        String query = getTablesQuery.replace("[databaseName]", credentials.getDatabaseName());
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
