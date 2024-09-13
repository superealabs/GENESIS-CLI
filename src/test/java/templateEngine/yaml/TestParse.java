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
        AppConfig config = FileUtils.fromYamlFile(AppConfig.class, "/Users/nomena/STAGE/GENESIS/src/test/java/templateEngine/yaml/config.yaml");

        System.out.println("Nom de l'application : " + config.getInfo().getNom());
        System.out.println("Version : " + config.getInfo().getVersion());
        System.out.println("Description :\n" + config.getInfo().getDescription());
    }

    @Test
    void frameworkYAML() throws IOException {
        Framework[] object = FileUtils.fromYamlFile(Framework[].class, "/Users/nomena/STAGE/GENESIS/data_genesis/yaml/framework-api.yaml");

        System.out.println(Arrays.toString(object));
    }
}
