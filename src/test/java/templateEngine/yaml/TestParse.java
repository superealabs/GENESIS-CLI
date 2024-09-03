package templateEngine.yaml;

import genesis.config.ApplicationType;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.IOException;

public class TestParse {
    @Test
    void parse() throws IOException {
        AppConfig config = FileUtils.fromYaml(AppConfig.class, "/Users/nomena/STAGE/GENESIS/src/test/java/templateEngine/yaml/config.yaml");

        System.out.println("Nom de l'application : " + config.getInfo().getNom());
        System.out.println("Version : " + config.getInfo().getVersion());
        System.out.println("Description :\n" + config.getInfo().getDescription());
    }

    @Test
    void application() throws IOException {
        ApplicationType[] applicationType = FileUtils.fromYaml(ApplicationType[].class, "/Users/nomena/STAGE/GENESIS/data_genesis/yaml/application.yml");

        System.out.println(applicationType.toString());
    }
}
