package templateEngine;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import genesis.engine.TemplateEngine;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateModelRepo {
    final TemplateEngine engine = new TemplateEngine();
    final String templatePrimary = FileUtils.getFileContent("data_genesis/Template.templ");

    public TemplateModelRepo() throws FileNotFoundException {
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


    private static HashMap<String, Object> getHashMapIntermediaireEachEach() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("tableName", "person");
        metadata.put("className", "Person");
        metadata.put("projectName", "TestProject");


        List<HashMap<String, List<Map<String, Object>>>> fields =
                List.of(new HashMap<>() {{
                    put("Person",
                            List.of(
                                    new HashMap<>() {{
                                        put("type", "Long");
                                        put("name", "id");
                                        put("isPrimaryKey", true);
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "id");
                                    }},
                                    new HashMap<>() {{
                                        put("type", "String");
                                        put("name", "firstName");
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "first_name");
                                    }},
                                    new HashMap<>() {{
                                        put("type", "String");
                                        put("name", "lastName");
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "last_name");
                                    }},
                                    new HashMap<>() {{
                                        put("type", "Integer");
                                        put("name", "age");
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "age");
                                    }},
                                    new HashMap<>() {{
                                        put("type", "java.time.LocalDate");
                                        put("name", "dateNaissance");
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "date_naissance");
                                    }},
                                    new HashMap<>() {{
                                        put("type", "com.example.Adresse");
                                        put("name", "adresse");
                                        put("isForeignKey", true);
                                        put("withGetters", true);
                                        put("withSetters", true);
                                        put("columnName", "adresse_id");
                                    }}
                            )
                    );
                }});

        metadata.put("entities", fields);

        // Toggle to add or remove the additional method
        metadata.put("hasAdditionalMethod", true);
        metadata.put("mess", "This is an additional method");

        return metadata;
    }


    /*---CONTROLLER---*/
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
                     @RequestMapping("/${classNameLink}")""");
        metadata.put("classKeyword", "public class");
        metadata.put("pathVariableKeyword", "@PathVariable");
        metadata.put("modelAttributeKeyword", "@RequestBody");
        metadata.put("extends", "");
        metadata.put("bracketStart", "{");

        metadata.put("bracketEnd", "}");
        metadata.put("namespaceEnd", "");

        return metadata;
    }

    /*---SERVICE---*/
    private static HashMap<String, Object> getHashMapService() {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("projectName", "animalerie");
        metadata.put("className", "animal");
        metadata.put("serviceName", "${majStart(className)}Service");

        metadata.put("namespace", "package");
        metadata.put("package", "com.${lowerCase(projectName)}.services");
        metadata.put("namespaceStart", "");
        metadata.put("serviceAnnotations", "@Service");
        metadata.put("classKeyword", "public class");
        metadata.put("extends", "");
        metadata.put("bracketStart", "{");

        metadata.put("bracketEnd", "}");
        metadata.put("namespaceEnd", "");

        return metadata;
    }

    private static HashMap<String, Object> getHashMapPOM() {
        HashMap<String, Object> metadata = new HashMap<>();

        // Variables simples
        metadata.put("springBootVersion", "3.3.4");
        metadata.put("groupLink", "itu.labs");
        metadata.put("projectName", "test");
        metadata.put("projectDescription", "Demo project for Spring Boot");
        metadata.put("languageVersion", "17");

        // Liste des dépendances
        List<HashMap<String, String>> dependencies = new ArrayList<>();

        // Dépendances spécifiques ajoutées
        HashMap<String, String> dep1 = new HashMap<>();
        dep1.put("groupId", "org.springframework.boot");
        dep1.put("artifactId", "spring-boot-starter-data-jpa");
        dependencies.add(dep1);

        HashMap<String, String> dep2 = new HashMap<>();
        dep2.put("groupId", "org.springframework.boot");
        dep2.put("artifactId", "spring-boot-starter-thymeleaf");
        dependencies.add(dep2);

        HashMap<String, String> dep3 = new HashMap<>();
        dep3.put("groupId", "org.springframework.boot");
        dep3.put("artifactId", "spring-boot-starter-web");
        dependencies.add(dep3);

        HashMap<String, String> dep4 = new HashMap<>();
        dep4.put("groupId", "com.microsoft.sqlserver");
        dep4.put("artifactId", "mssql-jdbc");
        dep4.put("version", "9.4.0.jre17");
        dependencies.add(dep4);

        HashMap<String, String> dep5 = new HashMap<>();
        dep5.put("groupId", "com.mysql");
        dep5.put("artifactId", "mysql-connector-j");
        dep5.put("version", "8.0.30");
        dependencies.add(dep5);

        HashMap<String, String> dep6 = new HashMap<>();
        dep6.put("groupId", "com.oracle.database.jdbc");
        dep6.put("artifactId", "ojdbc11");
        dep6.put("version", "21.3.0.0");
        dependencies.add(dep6);

        HashMap<String, String> dep7 = new HashMap<>();
        dep7.put("groupId", "org.postgresql");
        dep7.put("artifactId", "postgresql");
        dep7.put("version", "42.3.4");
        dependencies.add(dep7);

        // Dépendance avec scope (test)
        HashMap<String, String> dep8 = new HashMap<>();
        dep8.put("groupId", "org.springframework.boot");
        dep8.put("artifactId", "spring-boot-starter-test");
        dep8.put("scope", "test");
        dependencies.add(dep8);

        // Ajout de la liste des dépendances au metadata
        metadata.put("dependencies", dependencies);

        return metadata;
    }

    @Test
    void renderTemplateIntermediaire() {
        HashMap<String, Object> metadata = getHashMapPrimaire();

        String result = engine.simpleRender(templatePrimary, metadata);
        System.out.println(result);
    }

    @Test
    void templateEngineRenderModelWithFreeMarker() throws Exception {
        // Créer et configurer une instance FreeMarker
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);


        String templateContent = """
                 <#assign fields=metadata.fields>
                        \s
                 package com.${metadata.projectName}.models;
                        \s
                 import jakarta.persistence.*;
                        \s
                 @Entity
                 @Table(name="${metadata.tableName}")
                 public class Person  {
                 <#list fields as field>
                     private ${field.type} ${field.name};
                 </#list>
                        \s
                     public ${metadata.className}(<#list fields as field>${field.type} ${field.name}<#if field_has_next>, </#if></#list>) {
                         <#list fields as field>
                         this.${field.name} = ${field.name};
                         </#list>
                     }
                            \s
                     <#list fields as field>
                     <#if field.withGetters>
                     public ${field.type} get${field.name?cap_first}() {
                         return ${field.name};
                     }
                     </#if>
                
                     <#if field.withSetters>
                     public void set${field.name?cap_first}(${field.type} ${field.name}) {
                         this.${field.name} = ${field.name};
                     }
                     </#if>
                     </#list>
                 }
                \s""";

        // Utilisation de StringTemplateLoader pour charger des templates à partir de chaînes
        StringTemplateLoader stringLoader = new StringTemplateLoader();
        stringLoader.putTemplate("myTemplate", templateContent);
        cfg.setTemplateLoader(stringLoader);

        // Charger le template
        Template template = cfg.getTemplate("myTemplate");

        // Données du modèle
        Map<String, Object> metadata = getHashMapIntermediaire();

        // Rendu du template
        StringWriter writer = new StringWriter();
        template.process(Map.of("metadata", metadata), writer);

        String result = writer.toString();
        System.out.println(result);
    }


    @Test
    void templateEngineRenderModel() throws Exception {
        String template = """
                 package com.${lowerCase(projectName)}.models;
                
                 import jakarta.persistence.*;
                
                 @Entity
                 @Table(name="${tableName}")
                 public class ${majStart(className)} {
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


    @Test
    void testVelocityTemplate() {
        // Initialisation du moteur Velocity
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        // Template Velocity
        String template = """
            package com.${projectName}.models;

            import jakarta.persistence.*;

            @Entity
            @Table(name="${tableName}")
            public class ${className} {
                #foreach ($field in $fields)
                #if ($field.isPrimaryKey)
                @Id
                @GeneratedValue(strategy=GenerationType.IDENTITY)
                @Column(name="$field.columnName")
                #elseif ($field.isForeignKey)
                @ManyToOne
                @JoinColumn(name="$field.columnName")
                #else
                @Column(name="$field.columnName")
                #end
                private $field.type $field.name;
            
                #end

                // Constructor
                public ${className}(#foreach ($field in $fields)$field.type $field.name#if (!$foreach.last), #end#end) {
                    #foreach ($field in $fields)
                    this.$field.name = $field.name;
                    #end
                }

                // Getters and Setters
            #foreach ($field in $fields)
                #if ($field.withGetters)
                public $field.type get${$field.name}() {
                    return $field.name;
                }
                #end
                #if ($field.withSetters)
                public void set${$field.name}($field.type $field.name) {
                    this.$field.name = $field.name;
                }
                #end
            #end
            }
            """;

        // Données (via votre méthode)
        HashMap<String, Object> metadata = getHashMapIntermediaire();

        // Contexte Velocity
        VelocityContext context = new VelocityContext(metadata);

        // Génération du résultat
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "VelocityTest", template);

        // Résultat
        System.out.println(writer);
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


    /*

     */
    @Test
    void testDbContext() throws Exception {
        String template = """
                namespace ${majStart(projectName)}.Data;
                
                using Microsoft.EntityFrameworkCore;
                using Models;
                
                public class ${majStart(projectName)}Context : DbContext
                {
                    public ${majStart(projectName)}Context(DbContextOptions<${majStart(projectName)}Context> options) : base(options)
                    {
                    }
                    {{#each entities}}
                    public DbSet<${this}> ${this}s { get; set; }
                    {{/each}}
                
                    protected override void OnModelCreating(ModelBuilder modelBuilder)
                    {
                        {{#each entities}}
                        modelBuilder.Entity<${this}>(entity =>
                        {
                            {{#each entities.fields}}
                            {{#if this.isPrimaryKey}}
                            entity.HasKey(e => e.${majStart(this.name)});
                            {{else if this.isForeignKey}}
                            entity.HasOne(e => e.${majStart(this.name)})
                                .WithMany()
                                .HasForeignKey(e => e.${majStart(this.columnName)})
                                .OnDelete(DeleteBehavior.NoAction);
                            {{else}}
                            entity.Property(e => e.${majStart(this.name)});
                            {{/if}}
                            {{/each}}
                        });
                        {{/each}}
                    }
                }
                """;

        HashMap<String, Object> metadata = getHashMapIntermediaireEachEach();

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

    @Test
    void templateEngineRenderController() throws Exception {
        String template = """
                      ${namespace} ${package}${namespaceStart}
                               \s
                      import org.springframework.ui.Model;
                      import com.${lowerCase(projectName)}.models.${majStart(className)};
                      import org.springframework.stereotype.Controller;
                      import org.springframework.web.bind.annotation.*;
                      import com.${lowerCase(projectName)}.repositories.${majStart(className)}Repository;
                      import org.springframework.web.servlet.view.RedirectView;
                      import org.springframework.beans.factory.annotation.Autowired;
                               \s
                      ${controllerAnnotations}
                      ${classKeyword} ${controllerName} ${extends}${bracketStart}
                               \s
                          @Autowired
                          private ${majStart(className)}Service ${lowerCase(className)}Service;
                               \s
                          @GetMapping
                          public String getAll${majStart(classNameLink)}(Model model) ${bracketStart}
                              List<${majStart(className)}> ${lowerCase(classNameLink)} = ${lowerCase(className)}Service.getAll${majStart(className)}();
                              model.addAttribute("${lowerCase(classNameLink)}", ${lowerCase(classNameLink)});
                              return "${lowerCase(classNameLink)}/list-${lowerCase(className)}";
                          ${bracketEnd}
                               \s
                          @GetMapping("/{id}")
                          public String get${majStart(className)}ById(${pathVariableKeyword} Long id, Model model) ${bracketStart}
                      {{tab}}     ${majStart(className)} ${lowerCase(className)} = ${lowerCase(className)}Service.get${majStart(className)}ById(id);
                      {{tab}}     model.addAttribute("${lowerCase(className)}", ${lowerCase(className)});
                      {{tab}}     return "${lowerCase(classNameLink)}/view-list-${lowerCase(className)}";
                      {{tab}}${bracketEnd}
                               \s
                      {{tab}}@PostMapping
                      {{tab}}public String create${majStart(className)}(${modelAttributeKeyword} ${majStart(className)} ${lowerCase(className)}, Model model) ${bracketStart}
                      {{tab}}     ${majStart(className)} new${majStart(className)} = ${lowerCase(className)}Service.create${majStart(className)}(${lowerCase(className)});
                      {{tab}}     model.addAttribute("new${majStart(className)}", new${majStart(className)});
                      {{tab}}     return "${lowerCase(classNameLink)}/create-list-${lowerCase(className)}";
                      {{tab}}${bracketEnd}
                            \s
                      {{tab}}@PutMapping("/{id}")
                      {{tab}}public String update${majStart(className)}(${pathVariableKeyword} Long id, ${modelAttributeKeyword} ${majStart(className)} ${lowerCase(className)}, Model model) ${bracketStart}
                      {{tab}}     ${majStart(className)} update${majStart(className)} = ${lowerCase(className)}Service.update${majStart(className)}(id, ${lowerCase(className)});
                      {{tab}}     model.addAttribute("update${majStart(className)}", update${majStart(className)});
                      {{tab}}     return "${lowerCase(classNameLink)}/update-list-${lowerCase(className)}";
                      {{tab}}${bracketEnd}
                               \s
                      {{tab}}@DeleteMapping("/{id}")
                      {{tab}}public String delete${majStart(className)}ById(${pathVariableKeyword} Long id, Model model) ${bracketStart}
                      {{tab}}     ${lowerCase(className)}Service.delete${majStart(className)}(id);
                      {{tab}}     model.addAttribute("message", "${majStart(className)} deleted successfully");
                      {{tab}}     return "redirect:/${lowerCase(classNameLink)}";
                      {{tab}}${bracketEnd}
                               \s
                      ${bracketEnd}
                      ${namespaceEnd}
                \s""";

        HashMap<String, Object> metadata = getHashMapController();

        String result = engine.render(template, metadata);
        System.out.println(result);

    }

    @Test
    void templateEngineRenderService() throws Exception {
        String template = """
                 ${namespace} ${package}${namespaceStart}
                               \s
                 import com.${lowerCase(projectName)}.models.${majStart(className)};
                 import org.springframework.stereotype.Service;
                 import com.${lowerCase(projectName)}.repositories.${majStart(className)}Repository;
                 import org.springframework.beans.factory.annotation.Autowired;
                                \s
                 ${serviceAnnotations}
                 ${classKeyword} ${serviceName} ${extends}${bracketStart}
                                \s
                 {{tab}}@Autowired
                 {{tab}}private ${majStart(className)}Repository ${lowerCase(className)}Repository;
                                \s
                 {{tab}}public List<${majStart(className)}> getAll${majStart(className)}() ${bracketStart}
                 {{tab}}    return ${lowerCase(className)}Repository.findAll();
                 {{tab}}${bracketEnd}
                                \s
                 {{tab}}public ${majStart(className)} get${majStart(className)}ById(Long id) ${bracketStart}
                 {{tab}}    return ${lowerCase(className)}Repository.findById(id);
                 {{tab}}${bracketEnd}
                                \s
                 {{tab}}public ${majStart(className)} create${majStart(className)}(${majStart(className)} ${lowerCase(className)}) ${bracketStart}
                 {{tab}}    return ${lowerCase(className)}Repository.save(${lowerCase(className)});
                 {{tab}}${bracketEnd}
                                \s
                 {{tab}}public ${majStart(className)} update${majStart(className)}(Long id, ${majStart(className)} ${lowerCase(className)}) ${bracketStart}
                 {{tab}}    ${majStart(className)} existing${majStart(className)} = ${lowerCase(className)}Repository.findById(id);
                 {{tab}}    existing${majStart(className)} = ${lowerCase(className)};
                 {{tab}}    existing${majStart(className)}.setId${majStart(className)}(id);
                 {{tab}}    return ${lowerCase(className)}Repository.save(existing${lowerCase(className)});
                 {{tab}}${bracketEnd}
                                \s
                 {{tab}}public void delete${majStart(className)}(Long id) ${bracketStart}
                 {{tab}}    ${lowerCase(className)}Repository.deleteById(id);
                 {{tab}}${bracketEnd}
                                \s
                 ${bracketEnd}
                 ${namespaceEnd}
                \s""";

        HashMap<String, Object> metadata = getHashMapService();

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

    @Test
    void POMXML() throws Exception {
        String template = """
                <?xml version="1.0" encoding="UTF-8"?>
                <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
                {{tab}}<modelVersion>4.0.0</modelVersion>
                {{tab}}<parent>
                {{tab}}{{tab}}<groupId>org.springframework.boot</groupId>
                {{tab}}{{tab}}<artifactId>spring-boot-starter-parent</artifactId>
                {{tab}}{{tab}}<version>${springBootVersion}</version>
                {{tab}}{{tab}}<relativePath/>
                {{tab}}</parent>
                {{tab}}<groupId>${groupLink}</groupId>
                {{tab}}<artifactId>${projectName}</artifactId>
                {{tab}}<version>0.0.1-SNAPSHOT</version>
                {{tab}}<name>${projectName}</name>
                {{tab}}<description>${projectDescription}</description>
                {{tab}}<url/>
                {{tab}}<licenses>
                {{tab}}{{tab}}<license/>
                {{tab}}</licenses>
                {{tab}}<developers>
                {{tab}}{{tab}}<developer/>
                {{tab}}</developers>
                {{tab}}<scm>
                {{tab}}{{tab}}<connection/>
                {{tab}}{{tab}}<developerConnection/>
                {{tab}}{{tab}}<tag/>
                {{tab}}{{tab}}<url/>
                {{tab}}</scm>
                {{tab}}<properties>
                {{tab}}{{tab}}<java.version>${languageVersion}</java.version>
                {{tab}}</properties>
                {{tab}}<dependencies>
                {{tab}}{{tab}}{{#each dependencies}}
                {{tab}}{{tab}}<dependency>
                {{tab}}{{tab}}{{tab}}<groupId>${this.groupId}</groupId>
                {{tab}}{{tab}}{{tab}}<artifactId>${this.artifactId}</artifactId>
                {{tab}}{{tab}}{{tab}}<version>${this.version}</version>
                {{tab}}{{tab}}</dependency>{{#if !@last}}{{newline}}{{tab}}{{tab}}{{/if}}{{/each}}
                {{tab}}</dependencies>
                </project>
                
                <# ${groupLink} /#>
                """;

        HashMap<String, Object> metadata = getHashMapPOM();

        String result = engine.render(template, metadata);
        System.out.println(result);
    }


}
