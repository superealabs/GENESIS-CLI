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

public class MVCGenerator implements GenesisGenerator {
    TemplateEngine engine = new TemplateEngine();

    private Map<String, Object> getHashMapDaoUnique(Framework framework, TableMetadata[] tableMetadata, String projectName) throws Exception {
        String packageDefault;
        packageDefault = framework.getModelDao().getModelDaoSavePath();

        Database database = tableMetadata[0].getDatabase();
        String connectionString = database.getConnectionString().get(framework.getLangageId());
        Map<String, Object> connectionStringMetadata = getStringObjectMap(database);
        connectionString = engine.render(connectionString, connectionStringMetadata);

        Map<String, Object> metadata = new HashMap<>(Map.of(
                "projectName", projectName,
                "packageValue", packageDefault,
                "DBType", tableMetadata[0].getDatabase().getDaoName().get(framework.getLangageId()),
                "connectionString", connectionString)
        );

        List<String> fields = new ArrayList<>();
        for (TableMetadata tableMetadatum : tableMetadata) {
            fields.add(tableMetadatum.getClassName());
        }

        metadata.put("entities", fields);

        return metadata;
    }

    private static @NotNull Map<String, Object> getStringObjectMap(Database database) {
        Credentials credentials = database.getCredentials();

        return new HashMap<>(
                Map.of("host", credentials.getHost(),
                        "port", database.getPort(),
                        "database", credentials.getDatabaseName(),
                        "useSSL", credentials.isUseSSL(),
                        "username", credentials.getUser(),
                        "password", credentials.getPwd(),
                        "driverType", database.getDriverType(),
                        "serviceName", database.getServiceName()
                        )
        );
    }

    private static HashMap<String, Object> getRelatedLanguageMetadata(Language language) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Language-related metadata
        metadata.put("namespace", language.getSyntax().get("namespace"));
        metadata.put("bracketEnd", language.getSyntax().get("bracketEnd"));
        metadata.put("classKeyword", language.getSyntax().get("classKeyword"));
        metadata.put("bracketStart", language.getSyntax().get("bracketStart"));
        metadata.put("namespaceEnd", language.getSyntax().get("namespaceEnd"));
        metadata.put("namespaceStart", language.getSyntax().get("namespaceStart"));

        return metadata;
    }

    private static HashMap<String, Object> getPrimaryModelHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Framework-related metadata
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", tableMetadata.getClassName());
        metadata.put("package", framework.getModel().getModelPackage());
        metadata.put("imports", framework.getModel().getModelImports());
        metadata.put("fields", framework.getModel().getModelFieldContent());
        metadata.put("methods", framework.getModel().getModelGetterSetter());
        metadata.put("constructors", framework.getModel().getModelConstructors());
        metadata.put("classAnnotations", framework.getModel().getModelAnnotations());
        metadata.put("extends", "extends " + framework.getModelDao().getModelDaoExtends());

        return metadata;
    }

    private static HashMap<String, Object> getPrimaryModelDaoHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Framework-related metadata
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", framework.getModelDao().getModelDaoName());
        metadata.put("package", framework.getModelDao().getModelDaoPackage());
        metadata.put("imports", framework.getModelDao().getModelDaoImports());
        metadata.put("extends", framework.getModelDao().getModelDaoExtends());
        metadata.put("pkColumnType", tableMetadata.getPrimaryColumn().getType());
        metadata.put("fields", framework.getModelDao().getModelDaoFieldContent());
        metadata.put("methods", framework.getModelDao().getModelDaoMethodContent());
        metadata.put("constructors", framework.getModelDao().getModelDaoConstructors());
        metadata.put("classAnnotations", framework.getModelDao().getModelDaoAnnotations());

        return metadata;
    }

    private static HashMap<String, Object> getPrimaryServiceHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Framework-related metadata
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", framework.getService().getServiceName());
        metadata.put("package", framework.getService().getServicePackage());
        metadata.put("imports", framework.getService().getServiceImports());
        metadata.put("extends", framework.getService().getServiceExtends());
        metadata.put("fields", framework.getService().getServiceFieldContent());
        metadata.put("methods", framework.getService().getServiceMethodContent());
        metadata.put("constructors", framework.getService().getServiceConstructors());
        metadata.put("classAnnotations", framework.getService().getServiceAnnotations());

        return metadata;
    }

    private static HashMap<String, Object> getPrimaryControllerHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        // Framework-related metadata
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", framework.getController().getControllerName());
        metadata.put("package", framework.getController().getControllerPackage());
        metadata.put("imports", framework.getController().getControllerImports());
        metadata.put("extends", framework.getController().getControllerExtends());
        metadata.put("fields", framework.getController().getControllerFieldContent());
        metadata.put("methods", framework.getController().getControllerMethodContent());
        metadata.put("constructors", framework.getController().getControllerConstructors());
        metadata.put("classAnnotations", framework.getController().getControllerAnnotations());

        return metadata;
    }

    private static HashMap<String, Object> getModelHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> PrimaryModelMetadata = getPrimaryModelHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(PrimaryModelMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    private static HashMap<String, Object> getModelDaoHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> PrimaryModelDaoMetadata = getPrimaryModelDaoHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);
        languageMetadata.put("classKeyword", "public interface");

        metadata.putAll(PrimaryModelDaoMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    private static HashMap<String, Object> getServiceHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> PrimaryServiceMetadata = getPrimaryServiceHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(PrimaryServiceMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    private static HashMap<String, Object> getControllerHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryControllerMetadata = getPrimaryControllerHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(primaryControllerMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    private static HashMap<String, Object> getHashMapIntermediaire(TableMetadata tableMetadata, String projectName) {
        HashMap<String, Object> metadata = new HashMap<>();

        metadata.put("projectName", projectName);
        metadata.put("tableName", tableMetadata.getTableName());
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", tableMetadata.getClassName());
        metadata.put("classNameLink", tableMetadata.getClassName() + "s");

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

        fieldMap.put("withGetters", true);
        fieldMap.put("withSetters", true);
        fieldMap.put("type", field.getType());
        fieldMap.put("name", field.getName());
        fieldMap.put("isPrimaryKey", field.isPrimary());
        fieldMap.put("isForeignKey", field.isForeign());
        fieldMap.put("columnType", field.getColumnType());
        fieldMap.put("columnName", field.getReferencedColumn());

        return fieldMap;
    }

    @Override
    public String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getModel().getModelSavePath();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), tableMetadata.getClassName(), framework.getModel().getModelExtension(), result);

        return result;
    }

    @Override
    public String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelDaoHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getModelDao().getModelDaoSavePath();
        String fileName = framework.getModelDao().getModelDaoName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), framework.getModelDao().getModelDaoExtension(), result);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
//        String templateContent = framework.getModel().getModelDao().getContent();
//
//        Map<String, Object> metadata = getHashMapDaoUnique(framework, tableMetadata, projectName);
//        return engine.render(templateContent, metadata);
        return "";
    }

    @Override
    public String generateService(Framework framework, Language language, TableMetadata tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getServiceHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getService().getServiceSavePath();
        String fileName = framework.getService().getServiceName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), framework.getService().getServiceExtension(), result);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateController(Framework framework, Language language, TableMetadata tableMetadata, String projectName) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getControllerHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getController().getControllerSavePath();
        String fileName = framework.getController().getControllerName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), framework.getController().getControllerExtension(), result);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateView(Framework framework, Language language, TableMetadata tableMetadata, Database database, Credentials credentials, String projectName) throws IOException {
        return "";
    }

    private String loadTemplate(Framework framework) throws IOException {
        return FileUtils.getFileContent(Constantes.DATA_PATH + "/" + framework.getTemplate() + "." + Constantes.MODEL_TEMPLATE_EXT);
    }
}
