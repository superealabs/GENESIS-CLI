package templateEngine;

import genesis.config.langage.Editor;
import genesis.engine.TemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateView {
    TemplateEngine engine = new TemplateEngine();
    Editor editor = new Editor();

    private static HashMap<String, Object> getHashMapIntermediaire() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("tableName", "person");
        metadata.put("className", "Person");
        metadata.put("projectName", "TestProject");


        List<Map<String, Object>> fields = List.of(
                Map.of("type", "Long", // Primary key
                        "name", "id",
                        "isPrimaryKey", true,
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "id"),

                Map.of("type", "String",
                        "name", "firstName",
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "first_name"),

                Map.of("type", "String",
                        "name", "lastName",
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "last_name"),

                Map.of("type", "int",
                        "name", "age",
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "age"),

                Map.of("type", "java.time.LocalDate",
                        "name", "dateNaissance",
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "date_naissance"),

                Map.of("type", "Adresse",
                        "name", "adresse",
                        "isForeignKey", true,
                        "withGetters", true,
                        "withSetters", true,
                        "columnName", "adresse_id")
        );

        metadata.put("fields", fields);

        // Toggle to add or remove the additional method
        metadata.put("hasAdditionalMethod", true);
        metadata.put("mess", "This is an additional method");

        return metadata;
    }

    @Test
    void templateEngineRenderModel() throws Exception {
        String template = """
                 <div class="log-lg-3 col-md-6 order-0">
                    {{#each inputContents}}
                        #{this}
                    {{/each}}
                 </div>
                """;

        HashMap<String, Object> metadata = getHashMapIntermediaire();

        String result = engine.render(template, metadata);
        System.out.println(result);

    }

}
