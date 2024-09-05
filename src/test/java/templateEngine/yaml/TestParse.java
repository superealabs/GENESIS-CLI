package templateEngine.yaml;

import genesis.config.ApplicationType;
import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.connexion.Database;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.IOException;
import java.util.Arrays;

public class TestParse {
    @Test
    void parseYAML() throws IOException {
        AppConfig config = FileUtils.fromYaml(AppConfig.class, "/Users/nomena/STAGE/GENESIS/src/test/java/templateEngine/yaml/config.yaml");

        System.out.println("Nom de l'application : " + config.getInfo().getNom());
        System.out.println("Version : " + config.getInfo().getVersion());
        System.out.println("Description :\n" + config.getInfo().getDescription());
    }

    @Test
    void applicationYAML() throws IOException {
        ApplicationType[] object = FileUtils.fromYaml(ApplicationType[].class, "/Users/nomena/STAGE/GENESIS/data_genesis/yaml/application.yaml");

        System.out.println(Arrays.toString(object));
    }

    // OK
    @Test
    void applicationJSON() throws IOException {
        ApplicationType[] object = FileUtils.fromJson(ApplicationType[].class, FileUtils.getFileContent("/Users/nomena/STAGE/GENESIS/data_genesis/application.json"));

        System.out.println(Arrays.toString(object));
    }

    @Test
    void databaseYAML() throws IOException {
        Database[] object = FileUtils.fromYaml(Database[].class, "/Users/nomena/STAGE/GENESIS/data_genesis/yaml/database.yaml");

        System.out.println(Arrays.toString(object));
    }

    // OK
    @Test
    void databaseJSON() throws IOException {
        Database[] object = FileUtils.fromJson(Database[].class, FileUtils.getFileContent("/Users/nomena/STAGE/GENESIS/data_genesis/databases.json"));

        System.out.println(Arrays.toString(object));
    }

    // OK
    @Test
    void frameworkYAML() throws IOException {
        Framework[] object = FileUtils.fromYaml(Framework[].class, "/Users/nomena/STAGE/GENESIS/data_genesis/yaml/framework-api.yaml");

        System.out.println(Arrays.toString(object));
    }

    @Test
    void frameworkJSON() throws IOException {
        Framework[] object = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_API_JSON));

        System.out.println(Arrays.toString(object));
    }
}
