package genesis.connexion;

import genesis.config.Credentials;
import genesis.model.Entity;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

@Setter
@Getter
public class Database {
    private int id;
    private String name;
    private String driver;
    private String port;
    private HashMap<String, String> types;
    private String getcolumnsQuery;
    private String gettablesQuery;
    private String loginScript;

    public Connection getConnexion(Credentials credentials) throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        String url = "jdbc:%s://%s:%s/%s?user=%s&password=%s&useSSL=%s&allowPublicKeyRetrieval=%s";
        url = String.format(url, getName(), credentials.getHost(), getPort(), credentials.getDatabaseName(), credentials.getUser(), credentials.getPwd(), credentials.isUseSSL(), credentials.isAllowPublicKeyRetrieval());
        Connection connex = DriverManager.getConnection(url);
        connex.setAutoCommit(false);
        return connex;
    }

    public Entity[] getEntities(Connection connex, Credentials credentials, String entityName) throws ClassNotFoundException, SQLException {
        boolean opened = false;
        Connection connect = connex;
        if (connect == null) {
            connect = getConnexion(credentials);
            opened = true;
        }
        String query = getGettablesQuery().replace("[databaseName]", credentials.getDatabaseName());
        if (!entityName.equals("*")) {
            query += String.format(" and pg_tables.tablename='%s'", entityName);
        }
        try (PreparedStatement statement = connect.prepareStatement(query)) {
            Vector<Entity> liste = new Vector<>();
            Entity entity;
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    entity = new Entity();
                    entity.setTableName(result.getString("table_name"));
                    liste.add(entity);
                }
            }
            Entity[] entities = new Entity[liste.size()];
            for (int i = 0; i < entities.length; i++) {
                entities[i] = liste.get(i);
            }
            return entities;
        } finally {
            if (opened) {
                connect.close();
            }
        }
    }
}
