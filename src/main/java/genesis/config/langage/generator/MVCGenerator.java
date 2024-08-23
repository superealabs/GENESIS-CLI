package genesis.config.langage.generator;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.model.Entity;
import genesis.model.EntityColumn;
import genesis.model.EntityField;
import utils.FileUtils;

import java.io.IOException;

public class MVCGenerator implements GenesisGenerator {
    private static final String INDENT = "    "; // 4 espaces pour l'indentation

    @Override
    public String generateModel(Framework framework, Language language, Entity entity, String projectName) throws IOException {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }

        String templateContent = loadModelTemplate(framework);
        StringBuilder content = new StringBuilder(templateContent);

        // Remplacer les placeholders
        replaceModelPlaceholders(content, framework, language, entity, projectName);

        // Remplacer [fields]
        int fieldsIndex = content.indexOf("[fields]");
        if (fieldsIndex != -1) {
            content.delete(fieldsIndex, fieldsIndex + "[fields]".length());
            insertFields(content, fieldsIndex, framework, entity);
        }

        // Remplacer [constructors]
        int constructorsIndex = content.indexOf("[constructors]");
        if (constructorsIndex != -1) {
            content.delete(constructorsIndex, constructorsIndex + "[constructors]".length());
            insertConstructors(content, constructorsIndex, framework);
        }

        return formatContent(content.toString());
    }

    @Override
    public String generateController(Framework framework, Language language, Entity entity, Database database, Credentials credentials, String projectName) throws IOException {
        return "";
    }

    @Override
    public String generateView(Framework framework, Language language, Entity entity, Database database, Credentials credentials, String projectName) throws IOException {
        return "";
    }

    private String loadModelTemplate(Framework framework) throws IOException {
        return FileUtils.getFileContent(Constantes.DATA_PATH + "/" + framework.getModel().getModelTemplate() + "." + Constantes.MODEL_TEMPLATE_EXT);
    }

    private void replaceModelPlaceholders(StringBuilder content, Framework framework, Language language, Entity entity, String projectName) {
        replace(content, "[namespace]", language.getSyntax().get("namespace"));
        replace(content, "[namespaceStart]", language.getSyntax().get("namespaceStart"));
        replace(content, "[namespaceEnd]", language.getSyntax().get("namespaceEnd"));
        replace(content, "[package]", framework.getModel().getModelPackage());
        replace(content, "[imports]", generateImports(framework.getModel().getModelImports()));
        replace(content, "[classAnnotations]", generateClassAnnotations(framework.getModel().getModelAnnotations()));
        replace(content, "[extends]", framework.getModel().getModelExtends());
        replace(content, "[projectNameMin]", FileUtils.minStart(projectName));
        replace(content, "[projectNameMaj]", FileUtils.majStart(projectName));

        replace(content, "[classNameMaj]", FileUtils.majStart(entity.getClassName()));
        replace(content, "[tableName]", entity.getTableName());
    }

    private void generateForeignFieldAnnotations(StringBuilder content, Framework framework, EntityField field, EntityColumn column, String indent) {
        for (String annotation : framework.getModel().getModelForeignFieldAnnotations()) {
            String formattedAnnotation = annotation
                    .replace("[referencedFieldNameMin]", FileUtils.minStart(field.getReferencedField()))
                    .replace("[columnName]", column.getName());
            content.append(indent).append(formattedAnnotation).append("\n");
        }
    }

    private void insertFields(StringBuilder content, int index, Framework framework, Entity entity) {
        StringBuilder fields = new StringBuilder();
        EntityField[] entityFields = entity.getFields();
        EntityColumn[] entityColumns = entity.getColumns();

        for (int i = 0; i < entityFields.length; i++) {
            fields.append("\n");
            generateFieldContent(fields, framework, entityFields[i], entityColumns[i]);
            if (i < entityFields.length - 1) {
                fields.append("\n");
            }
        }

        content.insert(index, fields);
    }

    private void generateFieldContent(StringBuilder content, Framework framework, EntityField field, EntityColumn column) {
        String indent = INDENT;
        String fieldCase = framework.getModel().getModelFieldCase();
        String fieldName = field.getName();

        if (field.isPrimary()) {
            for (String annotation : framework.getModel().getModelPrimaryFieldAnnotations()) {
                content.append(indent).append(annotation).append("\n");
            }
        } else if (field.isForeign()) {
            generateForeignFieldAnnotations(content, framework, field, column, indent);
        }

        if (!field.isForeign()) {
            for (String annotation : framework.getModel().getModelFieldAnnotations()) {
                content.append(indent).append(annotation.replace("[columnName]", fieldName)).append("\n");
            }
        }
        fieldName = getFieldName(fieldName, fieldCase);
        String fieldDeclaration = framework.getModel().getModelFieldContent()
                .replace("[fieldType]", field.getType())
                .replace("[modelFieldCase]", fieldCase)
                .replace("[fieldNameMin]", FileUtils.minStart(fieldName))
                .replace("[fieldNameMaj]", FileUtils.majStart(fieldName))
                .replace("[columnName]", fieldName);

        content.append(indent).append(fieldDeclaration);
    }

    public String getFieldName(String fieldName, String fieldCase) {
        if ("Min".equalsIgnoreCase(fieldCase)) {
            fieldName = FileUtils.minStart(fieldName);
        } else if ("Maj".equalsIgnoreCase(fieldCase)) {
            fieldName = FileUtils.majStart(fieldName);
        }
        return fieldName;
    }

    private void insertConstructors(StringBuilder content, int index, Framework framework) {
        StringBuilder constructors = new StringBuilder();
        for (String constructor : framework.getModel().getModelConstructors()) {
            constructors.append(constructor).append("\n");
        }
        content.insert(index, constructors);
    }

    private String generateImports(String[] importsList) {
        String imports = String.join("\n", importsList);
        return "\n"+imports;
    }

    private String generateClassAnnotations(String[] annotationsList) {
        String annotations = String.join("\n", annotationsList);
        return "\n"+annotations;    }

    private void replace(StringBuilder content, String placeholder, String value) {
        int index = content.indexOf(placeholder);
        if (index != -1) {
            content.replace(index, index + placeholder.length(), value);
        }
    }

    private String formatContent(String content) {
        return content.trim();
    }

}
