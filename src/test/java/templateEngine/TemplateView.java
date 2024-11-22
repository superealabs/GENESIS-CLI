package templateEngine;

import genesis.config.langage.Editor;
import genesis.engine.TemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    void testNestedLoops() throws Exception {
        // Template avec boucle imbriquée
        String template = """
                {{#each items}}
                Item: #{this.name}
                {{#if @last}}
                SubItems:
                {{#each this.subItems}}
                - #{this}
                {{#each huuh}}
                    #{this}
                {{/each}}
                {{/each}}
                {{/if}}{{/each}}
                """;

        // Données : List<Map<String, List<Object>>>
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Item-" + i);
            item.put("subItems", List.of("Sub-" + i + "-1", "Sub-" + i + "-2"));
            items.add(item);
        }

        List<String> huuh = List.of("Bonjour", "Aurevoir");
        Map<String, Object> variables = new HashMap<>();
        variables.put("items", items);
        variables.put("huuh", huuh);

        // Exécution du moteur
        TemplateEngine engine = new TemplateEngine();
        String result = engine.render(template, variables);

        // Résultat attendu
        String expected = """
                Item: Item-1
                SubItems:
                - Sub-1-1
                - Sub-1-2
                Item: Item-2
                SubItems:
                - Sub-2-1
                - Sub-2-2
                Item: Item-3
                SubItems:
                - Sub-3-1
                - Sub-3-2
                """;

        // Vérification
        assertEquals(expected.strip(), result.strip());
    }
}
