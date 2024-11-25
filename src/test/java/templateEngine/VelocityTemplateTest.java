package templateEngine;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VelocityTemplateTest {

    @Test
    void testVelocityTemplate() {
        // Initialisation du moteur Velocity
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();

        // Template Velocity corrigé
        String template = """
                package com.${stringUtils.lowerCase(projectName)}.models;
                
                import jakarta.persistence.*;
                
                @Entity
                @Table(name="${tableName}")
                public class ${stringUtils.majStart(className)} {
                    #foreach ($field in $fields)
                    #if ($field.isPrimaryKey)
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
                    public ${stringUtils.majStart(className)}(
                    #foreach ($field in $fields)$field.type $field.name#if (!$foreach.last), #end#end) {
                        #foreach ($field in $fields)
                        this.$field.name = $field.name;
                        #end
                    }
                
                    // Getters and Setters
                    #foreach ($field in $fields)
                    #if ($field.withGetters)
                    public $field.type get${stringUtils.majStart($field.name)}() {
                        return $field.name;
                    }
                    #end
                    #if ($field.withSetters)
                    public void set${stringUtils.majStart($field.name)}($field.type $field.name) {
                        this.$field.name = $field.name;
                    }
                    #end
                    #end
                }
                """;


        // Données
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("projectName", "TestProject");
        metadata.put("className", "Person");
        metadata.put("tableName", "person");
        metadata.put("fields", getFieldList());

        // Contexte Velocity
        VelocityContext context = new VelocityContext(metadata);
        context.put("stringUtils", new StringUtils()); // Ajouter stringUtils au contexte

        // Génération du résultat
        StringWriter writer = new StringWriter();
        velocityEngine.evaluate(context, writer, "VelocityTest", template);

        // Résultat
        System.out.println(writer);
    }

    // Méthode pour obtenir la liste des champs
    private List<Field> getFieldList() {
        return Arrays.asList(
                new Field("id", "Long", "id", true, false, true, true),
                new Field("firstName", "String", "first_name", false, false, true, true),
                new Field("lastName", "String", "last_name", false, false, true, true),
                new Field("age", "int", "age", false, false, true, true),
                new Field("dateNaissance", "java.time.LocalDate", "date_naissance", false, false, true, true),
                new Field("adresse", "Adresse", "adresse_id", false, true, true, true)
        );
    }

    // Classe pour les utilitaires de chaîne
    static class StringUtils {
        public String lowerCase(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }
            return input.toLowerCase();
        }

        public String majStart(String input) {
            if (input == null || input.isEmpty()) {
                return input;
            }
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        }
    }

    // Classe pour représenter les champs
    static class Field {
        private String name;
        private String type;
        private String columnName;
        private boolean isPrimaryKey;
        private boolean isForeignKey;
        private boolean withGetters;
        private boolean withSetters;

        // Constructor
        public Field(String name, String type, String columnName, boolean isPrimaryKey, boolean isForeignKey, boolean withGetters, boolean withSetters) {
            this.name = name;
            this.type = type;
            this.columnName = columnName;
            this.isPrimaryKey = isPrimaryKey;
            this.isForeignKey = isForeignKey;
            this.withGetters = withGetters;
            this.withSetters = withSetters;
        }

        // Getters
        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public String getColumnName() {
            return columnName;
        }

        public boolean isPrimaryKey() {
            return isPrimaryKey;
        }

        public boolean isForeignKey() {
            return isForeignKey;
        }

        public boolean isWithGetters() {
            return withGetters;
        }

        public boolean isWithSetters() {
            return withSetters;
        }
    }
}

