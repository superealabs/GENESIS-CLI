package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

public class OracleDatabase extends Database {
    @Override
    protected String getJdbcUrl(Credentials credentials) {
        return String.format("jdbc:oracle:thin:@%s:%s:%s",
                credentials.getHost(),
                getPort(),
                credentials.getDatabaseName());
    }
}
