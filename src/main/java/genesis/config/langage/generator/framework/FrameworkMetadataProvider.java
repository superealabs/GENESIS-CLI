package genesis.config.langage.generator.framework;

import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.engine.TemplateEngine;
import genesis.model.ColumnMetadata;
import genesis.model.TableMetadata;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FrameworkMetadataProvider {
    private static final TemplateEngine engine = new TemplateEngine();

    public static @NotNull Map<String, Object> getCredentialsHashMap(Database database) {
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

    public static HashMap<String, Object> getRelatedLanguageMetadata(Language language) {
        HashMap<String, Object> metadata = new HashMap<>();

        metadata.put("namespace", language.getSyntax().get("namespace"));
        metadata.put("bracketEnd", language.getSyntax().get("bracketEnd"));
        metadata.put("classKeyword", language.getSyntax().get("classKeyword"));
        metadata.put("bracketStart", language.getSyntax().get("bracketStart"));
        metadata.put("namespaceEnd", language.getSyntax().get("namespaceEnd"));
        metadata.put("namespaceStart", language.getSyntax().get("namespaceStart"));

        return metadata;
    }

    public static List<String> getClassNameHashMap(TableMetadata[] tableMetadata) {
        List<String> fields = new ArrayList<>();

        for (TableMetadata tableMetadatum : tableMetadata) {
                fields.add(tableMetadatum.getClassName());
            }

        return fields;
    }

    public static HashMap<String, Object> getForeignInformation(ColumnMetadata columnMetadatum, TableMetadata tableMetadata, Editor editor){
        HashMap<String, Object> foreignInformation = new HashMap<>();
        ColumnMetadata[] columnMetadata = tableMetadata.getColumns();
        Map<String, Object> fieldHashMap = getFieldHashMap(columnMetadatum);

        for (ColumnMetadata columnMetadata1 : columnMetadata) {
            foreignInformation.putAll(fieldHashMap);
            foreignInformation.put("fieldName", columnMetadata1.getName());
        }

        return foreignInformation;
    }

    public static List<String> getUpdateInputsList(TableMetadata tableMetadata,Editor editor) throws Exception {
        List<String> inputContents = new ArrayList<>();
        Map<String, Object> updates = editor.getUpdate().getInput();
        ColumnMetadata[] columnMetadata = tableMetadata.getColumns();

        for (ColumnMetadata columnMetadatum : columnMetadata) {
            if (!columnMetadatum.isPrimary()) {
                String updatesInput = engine.render(updates.get(columnMetadatum.getPrimaryType()).toString(), Map.of("fieldName", columnMetadatum.getName()));
                inputContents.add(updatesInput);
            }
//            else if (!columnMetadatum.isForeign()) {
//                String foreignUpdateInput = engine.render(updates.get(columnMetadatum.getReferencedTable()).toString(), Map.of("fieldName", columnMetadatum.getName()));
//                inputContents.add(foreignUpdateInput);
//            }
        }

        return inputContents;
    }

    public static List<String> getCreateInputsList(TableMetadata tableMetadata, Editor editor) throws Exception {
        List<String> inputContents = new ArrayList<>();
        Map<String, Object> creates = editor.getInsert().getInput();
        ColumnMetadata[] columnMetadata = tableMetadata.getColumns();

        for (ColumnMetadata columnMetadatum : columnMetadata) {
            if (!columnMetadatum.isPrimary()) {
                String createInput = engine.render(creates.get(columnMetadatum.getPrimaryType()).toString(), Map.of("fieldName", columnMetadatum.getName()));
                inputContents.add(createInput);
            }
        }

        return inputContents;
    }

    public static HashMap<String, Object> getPrimaryModelHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", tableMetadata.getClassName());
        metadata.put("package", framework.getModel().getModelPackage());
        metadata.put("imports", framework.getModel().getModelImports());
        metadata.put("fields", framework.getModel().getModelFieldContent());
        metadata.put("methods", framework.getModel().getModelGetterSetter());
        metadata.put("constructors", framework.getModel().getModelConstructors());
        metadata.put("classAnnotations", framework.getModel().getModelAnnotations());

        return metadata;
    }

    public static HashMap<String, Object> getPrimaryModelDaoHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

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

    public static HashMap<String, Object> getPrimaryServiceHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

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

    public static HashMap<String, Object> getPrimaryControllerHashMap(Framework framework, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

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

    public static HashMap<String, Object> getModelHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryModelMetadata = getPrimaryModelHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(primaryModelMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    public static HashMap<String, Object> getModelDaoHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryModelDaoMetadata = getPrimaryModelDaoHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);
        languageMetadata.put("classKeyword", "public interface");

        metadata.putAll(primaryModelDaoMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    public static HashMap<String, Object> getServiceHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryServiceMetadata = getPrimaryServiceHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(primaryServiceMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    public static HashMap<String, Object> getControllerHashMap(Framework framework, Language language, TableMetadata tableMetadata) {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryControllerMetadata = getPrimaryControllerHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(primaryControllerMetadata);
        metadata.putAll(languageMetadata);

        return metadata;
    }

    public static HashMap<String, Object> getViewMainLayoutHashMap(TableMetadata[] tableMetadatas, TableMetadata tableMetadata, Language language, Editor editor) throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();

        HashMap<String, Object> primaryViewMetadata = getDisplayHashMap(editor);
        List<String> classNames = getClassNameHashMap(tableMetadatas);
        HashMap<String, Object> languageMetadata = getRelatedLanguageMetadata(language);

        metadata.putAll(primaryViewMetadata);
        metadata.putAll(languageMetadata);
        metadata.put("fields", classNames);

        metadata.put("className", tableMetadata.getClassName());
        return metadata;
    }


    public static HashMap<String, Object> getHashMapIntermediaire(TableMetadata tableMetadata, String projectName, String groupLink) {
        HashMap<String, Object> metadata = new HashMap<>();

        metadata.put("projectName", projectName);
        metadata.put("groupLink", groupLink);
        metadata.put("pkColumnType", tableMetadata.getPrimaryColumn().getType());
        metadata.put("pkColumnName", tableMetadata.getPrimaryColumn().getName());
        metadata.put("tableName", tableMetadata.getTableName());
        metadata.put("className", tableMetadata.getClassName());
        metadata.put("entityName", tableMetadata.getClassName());
        metadata.put("classNameLink", tableMetadata.getClassName() + "s");

        List<Map<String, Object>> fields = new ArrayList<>();
        for (ColumnMetadata field : tableMetadata.getColumns()) {
            Map<String, Object> fieldMap = getFieldHashMap(field);

            fields.add(fieldMap);
        }
        metadata.put("fields", fields);

        var fieldsPK = new ArrayList<>();
        for (ColumnMetadata field : tableMetadata.getColumns()) {
            if (field.isPrimary())
                continue;
            Map<String, Object> fieldMap = getFieldHashMap(field);
            fieldsPK.add(fieldMap);
        }
        metadata.put("fieldsPK", fieldsPK);

        return metadata;
    }

    public static @NotNull Map<String, Object> getFieldHashMap(ColumnMetadata field) {
        Map<String, Object> fieldMap = new HashMap<>();

        fieldMap.put("withGetters", true);
        fieldMap.put("withSetters", true);
        fieldMap.put("type", field.getType());
        fieldMap.put("name", field.getName());
        fieldMap.put("isPrimaryKey", field.isPrimary());
        fieldMap.put("isForeignKey", field.isForeign());
        fieldMap.put("columnType", field.getColumnType());
        fieldMap.put("columnName", field.getReferencedColumn());
        fieldMap.put("columnTable", field.getReferencedTable());

        return fieldMap;
    }

    public static HashMap<String, Object> getDisplayHashMap(Editor editor) {
        HashMap<String, Object> metadata = new HashMap<>();

        /*-- Header --*/
        metadata.put("iconsLink", editor.getLayout().getHeader().getIconsLink());
        metadata.put("coresLink", editor.getLayout().getHeader().getCoresLink());
        metadata.put("themeLink", editor.getLayout().getHeader().getThemeLink());
        metadata.put("assetsLink", editor.getLayout().getHeader().getAssetsLink());
        metadata.put("vendorsLink", editor.getLayout().getHeader().getVendorsLink());
        metadata.put("helpersLink", editor.getLayout().getHeader().getHelpersLink());
        metadata.put("viewAttribute", editor.getLayout().getHeader().getViewAttribute());

        /*-- Content --*/
        metadata.put("callMenu", editor.getLayout().getContent().getCallMenu());
        metadata.put("callContent", editor.getLayout().getContent().getCallContent());

        /*-- Footer --*/
        metadata.put("coresFooterLink", editor.getLayout().getFooter().getCoresFooterLink());
        metadata.put("mainsFooterLink", editor.getLayout().getFooter().getMainsFooterLink());
        metadata.put("pagesFooterLink", editor.getLayout().getFooter().getPagesFooterLink());
        metadata.put("vendorsFooterLink", editor.getLayout().getFooter().getVendorsFooterLink());

        return metadata;
    }

    public static HashMap<String, Object> getMenuLayoutHashMap(Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();

        String listLink = engine.render(editor.getLayout().getMenu().getListLink(), getHashMapIntermediaire(tableMetadata, projectName, groupLink));
        String createLink = engine.render(editor.getLayout().getMenu().getCreateLink(), getHashMapIntermediaire(tableMetadata, projectName, groupLink));

        metadata.put("listLink", listLink);
        metadata.put("createLink", createLink);
        metadata.put("logo", editor.getLayout().getMenu().getLogo());
        metadata.put("aside", editor.getLayout().getMenu().getAside());

        return metadata;
    }

    public static HashMap<String, Object> getListViewHashMap(Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();

        String createLink = engine.render(editor.getLayout().getMenu().getCreateLink(), getHashMapIntermediaire(tableMetadata, projectName, groupLink));

        metadata.put("createLink", createLink);
        metadata.put("textFailed", editor.getLayout().getContent().getTextFailed());
        metadata.put("alertFailed", editor.getLayout().getContent().getAlertFailed());
        metadata.put("textSuccess", editor.getLayout().getContent().getTextSuccess());
        metadata.put("alertSuccess", editor.getLayout().getContent().getAlertSuccess());

        return metadata;
    }

    public static HashMap<String, Object> getAllCreateViewHashMap(Framework framework, Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();

        List<String> createInput = getCreateInputsList(tableMetadata, editor);
        HashMap<String, Object> fieldsCreateMap = getPrimaryModelDaoHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getHashMapIntermediaire(tableMetadata, projectName, groupLink);
        HashMap<String, Object> primaryListViewHashMap = getListViewHashMap(editor, tableMetadata, projectName, groupLink);

        metadata.putAll(fieldsCreateMap);
        metadata.putAll(languageMetadata);
        metadata.putAll(primaryListViewHashMap);
        metadata.put("inputContents", createInput);


        return metadata;
    }

    public static HashMap<String, Object> getAllListViewHashMap(Framework framework, Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        HashMap<String, Object> metadata = new HashMap<>();

        List<String> updatesInput = getUpdateInputsList(tableMetadata, editor);
        HashMap<String, Object> fieldsUpdateMap = getPrimaryModelDaoHashMap(framework, tableMetadata);
        HashMap<String, Object> languageMetadata = getHashMapIntermediaire(tableMetadata, projectName, groupLink);
        HashMap<String, Object> primaryListViewHashMap = getListViewHashMap(editor, tableMetadata, projectName, groupLink);

        metadata.putAll(fieldsUpdateMap);
        metadata.putAll(languageMetadata);
        metadata.putAll(primaryListViewHashMap);
        metadata.put("inputContents", updatesInput);

        return metadata;
    }

    private Map<String, Object> getHashMapDaoUnique(Framework framework, TableMetadata[] tableMetadata, String projectName) throws Exception {
        String packageDefault;
        packageDefault = framework.getModelDao().getModelDaoSavePath();

        Database database = tableMetadata[0].getDatabase();
        String connectionString = database.getConnectionString().get(framework.getLangageId());
        Map<String, Object> connectionStringMetadata = getCredentialsHashMap(database);
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
}
