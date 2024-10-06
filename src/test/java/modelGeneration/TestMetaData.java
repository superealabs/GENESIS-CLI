package modelGeneration;

import genesis.config.Constantes;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.model.TableMetadata;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;

public class TestMetaData {

    Credentials credentials;

    public TestMetaData(Credentials credentials) {
        this.credentials = credentials;
        credentials
                .setHost("localhost")
                .setDatabaseName("test_db")
                .setUser("nomena")
                .setPwd("root")
                .setTrustCertificate(true)
                .setUseSSL(true);
    }

    @Test
    void postgresMetaData() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.getMetaData(credentials, database);
    }
}
