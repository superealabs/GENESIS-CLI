package genesis.connexion.providers;

import genesis.connexion.Credentials;
import genesis.connexion.Database;

public class MySQLDatabase extends Database {

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
}
