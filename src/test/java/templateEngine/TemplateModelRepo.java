package templateEngine;

import genesis.config.Constantes;
import genesis.engine.TemplateEngine;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateModelRepo {
    TemplateEngine engine = new TemplateEngine();
    String templatePrimary = FileUtils.getFileContent("data_genesis/ModelTemplate.templ");

    public TemplateModelRepo() throws FileNotFoundException {
    }

    @Test
    void renderTemplateIntermediaire() {
        HashMap<String, Object> metadata = getHashMapPrimaire();

        String result = engine.simpleRender(templatePrimary, metadata);
        System.out.println(result);
    }

    private static HashMap<String, Object> getHashMapPrimaire() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("className", "person");

        metadata.put("namespace", "package");
        metadata.put("package", "com.${lowerCase(projectName)}.models");
        metadata.put("namespaceStart", "");
        metadata.put("imports", "import jakarta.persistence.*;");
        metadata.put("classAnnotations", """
                @Entity
                @Table(name="${tableName}")""");
        metadata.put("classKeyword", "public class");
        metadata.put("extends", "");
        metadata.put("bracketStart", "{");


        metadata.put("fields",
                """
                            {{#each fields}}
                            {{#if this.isPrimaryKey}}
                            @Id
                            @GeneratedValue(strategy=GenerationType.IDENTITY)
                            @Column(name="${this.columnName}"){{elseIf this.isForeignKey}}
                            @ManyToOne
                            @JoinColumn(name="${this.columnName}"){{else}}
                            @Column(name="${this.columnName}"){{/if}}
                            private ${this.type} ${this.name};{{#if !@last}}{{newline}}{{/if}}
                            {{/each}}
                        """);
        metadata.put("constructors",
                """
                            public ${majStart(className)}({{#each fields}}${this.type} ${this.name}{{#if !@last}}, {{/if}}{{/each}}) {
                                {{#each fields}}
                                this.${this.name} = ${this.name};{{#if !@last}}
                                {{/if}}{{/each}}
                            }
                        """);
        metadata.put("getSets",
                """
                            {{#each fields}}
                            {{#if this.withGetters}}
                            public ${this.type} get${majStart(this.name)}() {
                                return ${this.name};
                            }{{/if}}
                            {{#if this.withSetters}}
                            public void set${majStart(this.name)}(${this.type} ${this.name}) {
                                this.${this.name} = ${this.name};
                            }{{#if !@last}}{{newline}}{{/if}}
                            {{/if}}{{/each}}
                        """);


        metadata.put("bracketEnd", "}");
        metadata.put("namespaceEnd", "");

        return metadata;
    }


    @Test
    void templateEngineRenderModel() throws Exception {
        String template = """
                package com.${lowerCase(projectName)}.models;

                import jakarta.persistence.*;
                
                @Entity
                @Table(name="${tableName}")
                public class Person  {
                    {{#each fields}}
                    {{#if this.isPrimaryKey}}
                    @Id
                    @GeneratedValue(strategy=GenerationType.IDENTITY)
                    @Column(name="${this.columnName}"){{elseIf this.isForeignKey}}
                    @ManyToOne
                    @JoinColumn(name="${this.columnName}"){{else}}
                    @Column(name="${this.columnName}"){{/if}}
                    private ${this.type} ${this.name};{{#if !@last}}{{newline}}{{/if}}
                    {{/each}}
                    public ${majStart(className)}({{#each fields}}${this.type} ${this.name}{{#if !@last}}, {{/if}}{{/each}}) {
                        {{#each fields}}
                        this.${this.name} = ${this.name};{{#if !@last}}
                        {{/if}}{{/each}}
                    }
                
                    {{#each fields}}
                    {{#if this.withGetters}}
                    public ${this.type} get${majStart(this.name)}() {
                        return ${this.name};
                    }{{/if}}
                    {{#if this.withSetters}}
                    public void set${majStart(this.name)}(${this.type} ${this.name}) {
                        this.${this.name} = ${this.name};
                    }{{#if !@last}}{{newline}}{{/if}}
                    {{/if}}{{/each}}
                }
                """;

        HashMap<String, Object> metadata = getHashMapIntermediaire();

        String result = engine.render(template, metadata);
        System.out.println(result);

    }

    private static HashMap<String, Object> getHashMapIntermediaire() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("tableName", "person");
        metadata.put("className", "person");
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
    void testElseIfSimplified() throws Exception {
        String template = """
                {{#each fields}}
                {{#if this.isPrimaryKey}}
                @Id
                @GeneratedValue(strategy=GenerationType.IDENTITY)
                @Column(name="${this.columnName}"){{elseIf this.isForeignKey}}
                @ManyToOne
                @JoinColumn(name="${this.columnName}"){{else}}
                @Column(name="${this.columnName}"){{/if}}
                private ${this.type} ${this.name};
                {{/each}}
                """;

        HashMap<String, Object> metadata = getHashMapIntermediaire();

        String result = engine.render(template, metadata);
        System.out.println(result);
    }


    @Test
    void renderModelFromPrimary() throws Exception {
        HashMap<String, Object> metadataPrimary = getHashMapPrimaire();
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire();

        String result = engine.simpleRender(templatePrimary, metadataPrimary);
        System.out.println("\n=== 1e RENDER ====");
        System.out.println(result);

        String resultFinal = engine.render(result, metadataFinally);
        System.out.println("\n\n=== 2nd RENDER ====");
        System.out.println(resultFinal);
    }

    @Test
    void elseIf() throws Exception {
        TemplateEngine engine = new TemplateEngine();

        String template = """
                {{#if isAdult}}
                    You are an adult.
                {{elseIf isTeenager}}
                    You are a teenager.
                {{elseIf isTeenagerUp}}
                    You are a teenager Up.
                {{else}}
                    You are a child.
                {{/if}}
                """;

        HashMap<String, Object> variables = new HashMap<>();
        variables.put("isAdult", false);
        variables.put("isTeenager", true);
        variables.put("isTeenagerUp", false);

        String result = engine.render(template, variables);
        System.out.println(result);
    }

    @Test
    void dbContext() throws Exception {
        TemplateEngine engine = new TemplateEngine();

        String template = """
        using Microsoft.EntityFrameworkCore;
        using System.ComponentModel.DataAnnotations;
        using ${projectName}.Models;
        
        namespace ${packageValue};
        
        public class ${projectName}Context : DbContext
        {
            {{#each entities}}
            public DbSet<${this}> ${this}s { get; set; }
            {{/each}}
            protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
            {
                optionsBuilder.Use${DBType}(@"${connectionString}");
            }
        }
        """;

        // Utilisation de Map.of pour créer la map des variables
        Map<String, Object> variables = Map.of(
                "projectName", "MyApp",
                "packageValue", "MyApp.Data",
                "DBType", "SqlServer",  // Peut être "SqlServer", "MySQL", "PostgreSQL", etc.
                "connectionString", "Server=myServerAddress;Database=myDataBase;User Id=myUsername;Password=myPassword;",
                "entities", List.of("User", "Order")
        );

        String result = engine.render(template, variables);
        System.out.println(result);
    }

    @Test
    void loopTest() throws Exception {
        TemplateEngine engine = new TemplateEngine();
        String template = "Liste des éléments :\n{{#each items}}\n- ${this}\n{{/each}}";

        Map<String, Object> variables = new HashMap<>();
        List<String> items = List.of("Pomme", "Banane", "Orange");
        variables.put("items", items);

        String output = engine.render(template, variables);
        System.out.println(output);
    }

    // Etapes pour render le templatePrimary du model :
    /*

    Modif Moteur :
        - fonctions : majStart(str), upperCase(str), lowerCase(str) OK
        - évaluation conditionnelle si null considérer false si non-null et uniquement la variable considérer comme true OK
        - dans boucle each si null ou indéfini, ne rien faire OK
        - if - else if - else : getter
        - ajout de "or" et "and" dans les conditions

    1) 1er Render templatePrimary intermédiaire :
    ex :
    ${namespace} ${package}${namespaceStart}
    ${imports}
    ${classAnnotations}
    ${classKeyword} ${className} ${extends} ${bracketStart}
        ${fields}
        ${constructors}
        ${getSets}
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

    private static HashMap<String, Object> getHashMapController() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("projectName", "animalerie");
        metadata.put("className", "animal");
        metadata.put("classNameLink", "animals");
        metadata.put("controllerName", "${majStart(className)}Controller");

        metadata.put("namespace", "package");
        metadata.put("package", "com.${lowerCase(projectName)}.controllers");
        metadata.put("namespaceStart", "");
        metadata.put("controllerAnnotations", """
                @Controller
                {{tab}} @RequestMapping("/${classNameLink}")""");
        metadata.put("classKeyword", "public class");
        metadata.put("methodKeyword", "public");
        metadata.put("returnKeyword", "return");
        metadata.put("valueReturnKeyword", "String");
        metadata.put("pathVariableKeyword", "@PathVariable");
        metadata.put("modelAttributeKeyword", "@ModelAttribute");
        metadata.put("extends", "");
        metadata.put("bracketStart", "{");


//        metadata.put("fields",
//                """
//                            {{#each fields}}
//                            {{#if this.isPrimaryKey}}
//                            @Id
//                            @GeneratedValue(strategy=GenerationType.IDENTITY)
//                            @Column(name="${this.columnName}"){{elseIf this.isForeignKey}}
//                            @ManyToOne
//                            @JoinColumn(name="${this.columnName}"){{else}}
//                            @Column(name="${this.columnName}"){{/if}}
//                            private ${this.type} ${this.name};{{#if !@last}}{{newline}}{{/if}}
//                            {{/each}}
//                        """);
//        metadata.put("constructors",
//                """
//                            public ${majStart(className)}({{#each fields}}${this.type} ${this.name}{{#if !@last}}, {{/if}}{{/each}}) {
//                                {{#each fields}}
//                                this.${this.name} = ${this.name};{{#if !@last}}
//                                {{/if}}{{/each}}
//                            }
//                        """);
//        metadata.put("getSets",
//                """
//                            {{#each fields}}
//                            {{#if this.withGetters}}
//                            public ${this.type} get${majStart(this.name)}() {
//                                return ${this.name};
//                            }{{/if}}
//                            {{#if this.withSetters}}
//                            public void set${majStart(this.name)}(${this.type} ${this.name}) {
//                                this.${this.name} = ${this.name};
//                            }{{#if !@last}}{{newline}}{{/if}}
//                            {{/if}}{{/each}}
//                        """);


        metadata.put("bracketEnd", "}");
        metadata.put("namespaceEnd", "");

        return metadata;
    }

    @Test
    void templateEngineRenderController() throws Exception {
        String template = """
                    ${namespace} ${package}${namespaceStart}
               
                    import org.springframework.ui.Model;
                    import com.${lowerCase(projectName)}.entities.${majStart(className)};
                    import org.springframework.stereotype.Controller;
                    import org.springframework.web.bind.annotation.*;
                    import com.${lowerCase(projectName)}.repositories.${majStart(className)}Repository;
                    import org.springframework.web.servlet.view.RedirectView;
                    import org.springframework.beans.factory.annotation.Autowired;
               
                    ${controllerAnnotations}
                    ${classKeyword} ${controllerName} ${extends}${bracketStart}
               
                    {{tab}}@Autowired
                    {{tab}}private ${majStart(className)}Service ${lowerCase(className)}Service;
               
                    {{tab}}@GetMapping
                    {{tab}}${methodKeyword} ${valueReturnKeyword} getAll${majStart(classNameLink)}(Model model) ${bracketStart}
                    {{tab}}    List<${majStart(className)}> ${lowerCase(classNameLink)} = ${lowerCase(className)}Service.getAll${majStart(classNameLink)}();
                    {{tab}}    model.addAttribute("${lowerCase(classNameLink)}", ${lowerCase(classNameLink)});
                    {{tab}}    ${returnKeyword} "${lowerCase(classNameLink)}/list-${lowerCase(className)}";
                    {{tab}}${bracketEnd}
               
                    {{tab}}@GetMapping("/{id}")
                    {{tab}}${methodKeyword} ${valueReturnKeyword} get${majStart(className)}ById(${pathVariableKeyword} Long id, Model model) ${bracketStart}
                    {{tab}}     ${majStart(className)} ${lowerCase(className)} = ${lowerCase(className)}Service.get${majStart(className)}ById(id);
                    {{tab}}     model.addAttribute("${lowerCase(className)}", ${lowerCase(className)});
                    {{tab}}     ${returnKeyword} "${lowerCase(classNameLink)}/view-list-${lowerCase(className)}";
                    {{tab}}${bracketEnd}
               
                    {{tab}}@PostMapping
                    {{tab}}${methodKeyword} ${valueReturnKeyword} create${majStart(className)}(${modelAttributeKeyword} ${majStart(className)}DTO ${lowerCase(className)}DTO, Model model) ${bracketStart}
                    {{tab}}     ${majStart(className)} new${majStart(className)} = ${lowerCase(className)}Service.create${majStart(className)}(${lowerCase(className)}DTO.to${majStart(className)}());
                    {{tab}}     model.addAttribute("new${majStart(className)}", new${majStart(className)});
                    {{tab}}     ${returnKeyword} "${lowerCase(classNameLink)}/create-list-${lowerCase(className)}";
                    {{tab}}${bracketEnd}
            
                    {{tab}}@PutMapping("/{id}")
                    {{tab}}${methodKeyword} ${valueReturnKeyword} update${majStart(className)}(${pathVariableKeyword} Long id, ${modelAttributeKeyword} ${majStart(className)}DTO ${lowerCase(className)}DTO, Model model) ${bracketStart}
                    {{tab}}     ${majStart(className)} update${majStart(className)} = ${lowerCase(className)}Service.update${majStart(className)}(id, ${lowerCase(className)}DTO.to${majStart(className)}());
                    {{tab}}     model.addAttribute("update${majStart(className)}", update${majStart(className)});
                    {{tab}}     ${returnKeyword} "${lowerCase(classNameLink)}/update-list-${lowerCase(className)}";
                    {{tab}}${bracketEnd}
               
                    {{tab}}@DeleteMapping("/{id}")
                    {{tab}}${methodKeyword} ${valueReturnKeyword} delete${majStart(className)}ById(${pathVariableKeyword} Long id, Model model) ${bracketStart}
                    {{tab}}     ${lowerCase(className)}Service.delete${majStart(className)}(id);
                    {{tab}}     model.addAttribute("message", "${majStart(className)} deleted successfully");
                    {{tab}}     ${returnKeyword} "redirect:/${lowerCase(classNameLink)}";
                    {{tab}}${bracketEnd}
               
                    ${bracketEnd}
                    ${namespaceEnd}
               """;

        HashMap<String, Object> metadata = getHashMapController();

        String result = engine.render(template, metadata);
        System.out.println(result);

    }

    @Test
    void templateEngineRenderViewModel1() throws Exception {

        String template = "";

        HashMap<String, Object> metadata = getHashMapController();

        String result = engine.render(template, metadata);
        System.out.println(result);
    }
}
