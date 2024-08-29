package templateEngine;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StringTemplateTest {
    @Test
    void strTemplate() {
        /*String name = "Alice";
        String greeting = STR."""
                            Hello, \{name}!
                            Comment ca va ?
                            En forme ???
                                        OK
        """;
        // Résultat : "Hello, Alice!"
        System.out.println(greeting);

        int x = 10;
        int y = 20;
        String result = STR."\{x} + \{y} = \{x + y}";
        System.out.println(result);
         */
    }

    @Test
    void name() {
        String template = """
                    [namespace] [package][namespaceStart]
                    [imports]
                    [classAnnotations]
                    public class [classNameMaj] [extends] {
                        [fields]
                        [constructors]
                    }
                    [namespaceEnd]
                """;

        template = template.replace("[namespace]", "package")
                .replace("[namespaceStart]", "")
                .replace("[namespaceEnd]", "")
                .replace("[package]", "test.com")
                .replace("[imports]", "import test;")
                .replace("[classAnnotations]", "@Table")
                .replace("[extends]", "extends Table")
                .replace("[projectNameMin]", "project")
                .replace("[projectNameMaj]", "Project")
                .replace("[tableName]", "Project")
                .replace("[classNameMaj]", "Project");

        System.out.println(template);
    }

    @Test
    void templateEngineRender() throws Exception {

        String template = """
public class ${className} {
    // Fields
    {{#each fields}}
    private ${this.type} ${this.name};
    {{/each}}

    // Constructor
    public Person({{#each fields}}${this.type} ${this.name}{{#if !@last}}, {{/if}}{{/each}}) {
        {{#each fields}}
        this.${this.name} = ${this.name};{{#if !@last}}
        {{/if}}{{/each}}
    }

    // Getters and Setters
    {{#each fields}}
    public ${this.type} get${this.name}() {
        return ${this.name};
    }
    public void set${this.name}(${this.type} ${this.name}) {
        this.${this.name} = ${this.name};
    }
    {{/each}}
    {{#if hasAdditionalMethod }}
    // Additional method
    public void additionalMethod() {
        System.out.println("This is an additional method.");
    }
    {{/if}}
}
""";


        TemplateEngine engine = new TemplateEngine();

            HashMap<String, Object> metadata = new HashMap<>();
            metadata.put("className", "Person");

            List<Map<String, String>> fields = List.of(
                    Map.of("type", "String",
                            "name", "firstName"),
                    Map.of("type", "String",
                            "name", "lastName"),
                    Map.of("type", "int",
                            "name", "age")
            );

            metadata.put("fields", fields);

            // Toggle to add or remove the additional method
            metadata.put("hasAdditionalMethod", false);

            String result = engine.render(template, metadata);
            System.out.println(result);

    }
}
