package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.GenesisGenerator;
import genesis.config.langage.generator.MVCGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.MySQLDatabase;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.model.Entity;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQLSQLTest {

    Credentials credentials = new Credentials("test_db", "nomena", "root", "localhost", true, true);

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void PostgreSQLxJavaSpringMVC() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL
        Language language = languages[0];                                   // Java
        Framework framework = frameworks[0];                                // Spring MVC

        try (Connection connection = database.getConnection(credentials)) {
            Entity[] entities = database.getEntities(connection, credentials, "employe");
            Entity entity = entities[0];
            entity.initialize(connection, credentials, database, language); ;

            GenesisGenerator mvcGenerator = new MVCGenerator();
            String model = mvcGenerator.generateModel(framework, language, entity, "Test");

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n"+model);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void MySQLxNET() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL
        Language language = languages[1];                                   // C#
        Framework framework = frameworks[1];                                // .NET

        try (Connection connection = database.getConnection(credentials)) {
            Entity[] entities = database.getEntities(connection, credentials, "employe");
            Entity entity = entities[0];
            entity.initialize(connection, credentials, database, language); ;

            GenesisGenerator mvcGenerator = new MVCGenerator();
            String model = mvcGenerator.generateModel(framework, language, entity, "Test");

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n"+model);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

}
