package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

public class SQLServerDatabase extends Database {

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
