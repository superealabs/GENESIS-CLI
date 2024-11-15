package modelGeneration;

import genesis.config.Constantes;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.model.TableMetadata;
import genesis.connexion.providers.PostgreSQLDatabase;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.IOException;

public class TestMetaData {

    final Credentials credentials;

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
    void postgresMetaData() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.getMetaData(credentials, database);
    }
}
