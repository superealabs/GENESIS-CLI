import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.MVCGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.model.Entity;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class ClassTest {
    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void modelGeneration() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));

        Database database = databases[0];       // MySQL
        Language language = languages[0];       // Java
        Framework framework = frameworks[0];    // Spring MVC

        Credentials credentials = new Credentials("test_db", "root", "Nomena321@", "localhost", true, true);

        try (Connection connection = database.getConnexion(credentials)) {
            Entity[] entities = database.getEntities(connection, credentials, "employe");
            Entity entity = entities[0];
            entity.initialize(connection, credentials, database, language); ;

            MVCGenerator mvcGenerator = new MVCGenerator();
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
