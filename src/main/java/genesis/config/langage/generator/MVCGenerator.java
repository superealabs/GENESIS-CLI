package genesis.config.langage.generator;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.engine.TemplateEngine;
import genesis.model.ColumnMetadata;
import genesis.model.TableMetadata;
import org.jetbrains.annotations.NotNull;
import utils.FileUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static utils.FileUtils.toCamelCase;

public class MVCGenerator implements GenesisGenerator {

    @Override
    public String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadModelTemplate(framework);

        TemplateEngine engine = new TemplateEngine();

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getHashMapPrimaire(framework, language, tableMetadata);
        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName);
        return engine.render(result, metadataFinally);
    }


    private static HashMap<String, Object> getHashMapPrimaire(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Framework-related metadata
        metadata.put("package", framework.getModel().getModelPackage());
        metadata.put("imports", framework.getModel().getModelImports());
        metadata.put("classAnnotations", framework.getModel().getModelAnnotations());
        metadata.put("extends", framework.getModel().getModelExtends());
        metadata.put("fields", framework.getModel().getModelFieldContent());
        metadata.put("constructors", framework.getModel().getModelConstructors());
        metadata.put("getSets", framework.getModel().getModelGetterSetter());

        // Language-related metadata
        metadata.put("className", tableMetadata.getTableName());
        metadata.put("namespace", language.getSyntax().get("namespace"));
        metadata.put("namespaceStart", language.getSyntax().get("namespaceStart"));
        metadata.put("classKeyword", language.getSyntax().get("classKeyword"));
        metadata.put("bracketStart", language.getSyntax().get("bracketStart"));
        metadata.put("bracketEnd", language.getSyntax().get("bracketEnd"));
        metadata.put("namespaceEnd", language.getSyntax().get("namespaceEnd"));

        return metadata;
    }



    private static HashMap<String, Object> getHashMapIntermediaire(TableMetadata tableMetadata, String projectName) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("tableName", tableMetadata.getTableName());
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("projectName", projectName);

        List<Map<String, Object>> fields = new ArrayList<>();
        for (ColumnMetadata field : tableMetadata.getColumns()) {
            Map<String, Object> fieldMap = getStringObjectMap(field);
            fields.add(fieldMap);
        }
        metadata.put("fields", fields);

        return metadata;
    }

    private static @NotNull Map<String, Object> getStringObjectMap(ColumnMetadata field) {
        Map<String, Object> fieldMap = new HashMap<>();
        fieldMap.put("type", field.getType());
        fieldMap.put("name", field.getName());
        fieldMap.put("isPrimaryKey", field.isPrimary());
        fieldMap.put("isForeignKey", field.isForeign());
        fieldMap.put("withGetters", true);
        fieldMap.put("withSetters", true);
        fieldMap.put("columnType", field.getColumnType());
        fieldMap.put("columnName", field.getReferencedColumn());
        return fieldMap;
    }


    @Override
    public String generateController(Framework framework, Language language, TableMetadata tableMetadata, Database database, Credentials credentials, String projectName) throws IOException {
        return "";
    }

    @Override
    public String generateView(Framework framework, Language language, TableMetadata tableMetadata, Database database, Credentials credentials, String projectName) throws IOException {
        return "";
    }

    private String loadModelTemplate(Framework framework) throws IOException {
        return FileUtils.getFileContent(Constantes.DATA_PATH + "/" + framework.getModel().getModelTemplate() + "." + Constantes.MODEL_TEMPLATE_EXT);
    }
}
