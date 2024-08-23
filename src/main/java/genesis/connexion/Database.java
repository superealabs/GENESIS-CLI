package genesis.connexion;

import genesis.model.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

@Setter
@Getter
@ToString
public abstract class Database {
    private int id;
    private String name;
    private String driver;
    private String port;
    private HashMap<String, String> types;
    private String getColumnsQuery;
    private String addEntitiesQuery;
    private String getTablesQuery;
    private String loginScript;

    public Connection getConnection(Credentials credentials) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        String url = getJdbcUrl(credentials);
        Connection connection;

        if ("oracle".equalsIgnoreCase(name)) {
            connection = DriverManager.getConnection(url, credentials.getUser(), credentials.getPwd());
        } else {
            connection = DriverManager.getConnection(url);
        }

        connection.setAutoCommit(false);
        return connection;
    }

    protected abstract String getJdbcUrl(Credentials credentials);

    public Entity[] getEntities(Connection connection, Credentials credentials, String entityName) throws SQLException {
        try (Connection connect = getOrCreateConnection(connection, credentials);
             PreparedStatement statement = prepareStatement(connect, credentials, entityName);
             ResultSet result = statement.executeQuery()) {

            List<Entity> entities = new ArrayList<>();
            while (result.next()) {
                entities.add(createEntityFromResult(result));
            }

            return entities.toArray(new Entity[0]);
        }
    }

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

    private Entity createEntityFromResult(ResultSet result) throws SQLException {
        Entity entity = new Entity();
        entity.setTableName(result.getString("table_name"));
        return entity;
    }
}
