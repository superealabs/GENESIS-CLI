package modelGeneration;

import utils.FileUtils;
import genesis.config.Constantes;
import org.junit.jupiter.api.Test;
import genesis.connexion.Database;
import genesis.model.TableMetadata;
import genesis.connexion.Credentials;
import genesis.connexion.providers.PostgreSQLDatabase;

import java.io.FileNotFoundException;

public class TestMetaData {

    Credentials credentials = new Credentials("test_db", "nomena", "root", "localhost", true, true);

    @Test
    void postgresMetaData() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.getMetaData(credentials, database);
    }
}
