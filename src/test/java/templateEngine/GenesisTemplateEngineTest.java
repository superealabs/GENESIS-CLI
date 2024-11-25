package templateEngine;

import genesis.engine.GenesisTemplateEngine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GenesisTemplateEngineTest {
    @Test
    void testNestedLoops() throws Exception {
        // Template avec boucle imbriquée
        String template = """
                {{#each items}}
                Item: ${this.name}
                SubItems:
                {{#each this.subItems}}
                - ${this}
                {{/each}}{{/each}}
                """;

        // Données : List<Map<String, List<Object>>>
        List<Map<String, Object>> items = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", "Item-" + i);
            item.put("subItems", List.of("Sub-" + i + "-1", "Sub-" + i + "-2"));
            items.add(item);
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("items", items);

        // Exécution du moteur
        GenesisTemplateEngine engine = new GenesisTemplateEngine();
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
