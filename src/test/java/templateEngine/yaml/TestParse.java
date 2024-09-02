package templateEngine.yaml;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class TestParse {
    @Test
    void parse() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        AppConfig config = objectMapper.readValue(new File("/Users/nomena/STAGE/GENESIS/src/test/java/templateEngine/yaml/config.yaml"), AppConfig.class);

        System.out.println("Nom de l'application : " + config.getInfo().getNom());
        System.out.println("Version : " + config.getInfo().getVersion());
        System.out.println("Description :\n" + config.getInfo().getDescription());
    }
}
