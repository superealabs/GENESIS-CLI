package templateEngine;

import genesis.config.langage.Language;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateModelRepo {
    @Test
    void templateEngineRenderModel() throws Exception {
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
                    public ${this.type} get${majStart(this.name)}() {
                        return ${this.name};
                    }
                    public void set${majStart(this.name)}(${this.type} ${this.name}) {
                        this.${this.name} = ${this.name};
                    }
                    {{/each}}{{#if hasAdditionalMethod }}
                    // Additional method
                    public void additionalMethod() {
                        System.out.println("${lowerCase(mess)}");
                        System.out.println("${upperCase(mess)}");
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
        metadata.put("hasAdditionalMethod", true);
        metadata.put("mess", "This is an additional method");


        String result = engine.render(template, metadata);
        System.out.println(result);

    }

    // Etapes pour render le template du model :
    /*

    Modif Moteur :
        - fonctions : majStart(str), upperCase(str), lowerCase(str) OK
        - évaluation conditionnelle si null considérer false si non-null et uniquement la variable considérer comme true
        ex : if
        - dans boucle each si null ou indéfini, ne rien faire

    1) 1er Render template intermédiaire :
    ex :
    ${namespace} ${package}${namespaceStart}
    ${imports}
    ${classAnnotations}
    ${classKeyword} ${className} ${extends} ${bracketStart}
        ${fields}
        ${constructors}
    ${bracketEnd}
    ${namespaceEnd}

    =>

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
        {{/each}}{{#if hasAdditionalMethod }}
        // Additional method
        public void additionalMethod() {
            System.out.println("This is an additional method.");
        }
    {{/if}}
    }

        -
     */


}
