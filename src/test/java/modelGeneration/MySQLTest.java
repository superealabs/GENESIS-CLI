package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.framework.APIGenerator;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.MySQLDatabase;
import genesis.connexion.model.TableMetadata;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.IOException;
import java.sql.Connection;

public class MySQLTest {

    Credentials credentials;

    public MySQLTest() {
        this.credentials = new Credentials()
                .setHost("localhost")
                .setPort("3306")
                .setDatabaseName("test_db")
                .setSchemaName("test_db")
                .setUser("root")
                .setPwd("Nomena321@")
                .setTrustCertificate(true)
                .setUseSSL(true)
                .setAllowPublicKeyRetrieval(true);
    }

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void MySQLxJavaSpringMVC() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        MySQLDatabase database = (MySQLDatabase) databases[0];  // MySQL
        Language language = languages[0];                       // Java
        Framework framework = frameworks[0];                    // Spring MVC


        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            TableMetadata tableMetadata = entities[1]; //Employe

            GenesisGenerator mvcGenerator = new APIGenerator();
            String projectName = "TestProject", groupLink = "com";
            String destinationFolder = "/Users/nomena/STAGE/GENESIS";


            String model = mvcGenerator.generateModel(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
            String dao = mvcGenerator.generateDao(framework, language, tableMetadata, destinationFolder, projectName, groupLink);

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
            System.out.println("\n====== GENERATED ======\n" + dao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void MySQLxNET() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        MySQLDatabase database = (MySQLDatabase) databases[0];  // MySQL
        Language language = languages[1];                       // C#
        Framework framework = frameworks[1];                    // .NET


        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            TableMetadata tableMetadata = entities[1]; //Employe

            GenesisGenerator mvcGenerator = new APIGenerator();
            String projectName = "TestProject", groupLink = "com";
            String destinationFolder = "/Users/nomena/STAGE/GENESIS";


            String model = mvcGenerator.generateModel(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
            String dao = mvcGenerator.generateDao(framework, language, tableMetadata, destinationFolder, projectName, groupLink);

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
            System.out.println("\n====== GENERATED ======\n" + dao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
