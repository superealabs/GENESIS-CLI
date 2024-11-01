package genesis.config.langage.generator.framework;

import genesis.config.Constantes;
import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.engine.TemplateEngine;
import genesis.model.TableMetadata;
import utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;

import static genesis.config.langage.generator.framework.FrameworkMetadataProvider.*;

public class MVCGenerator implements GenesisGenerator {
    private static final TemplateEngine engine = new TemplateEngine();

    @Override
    public String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelHashMap(framework, language, tableMetadata);
        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);

        String fileSavePath = framework.getModel().getModelSavePath();
        fileSavePath = engine.simpleRender(fileSavePath, metadataFinally);

        FileUtils.createFile(fileSavePath, tableMetadata.getClassName(), framework.getModel().getModelExtension(), result);

        return result;
    }

    @Override
    public String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelDaoHashMap(framework, language, tableMetadata);
        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);
        metadataFinally.putAll(getPrimaryModelDaoHashMap(framework, tableMetadata));

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);

        String fileSavePath = framework.getModelDao().getModelDaoSavePath();
        String fileName = framework.getModelDao().getModelDaoName();
        fileSavePath = engine.simpleRender(fileSavePath, metadataFinally);
        fileName = engine.simpleRender(fileName, metadataFinally);

        FileUtils.createFile(fileSavePath, fileName, framework.getModelDao().getModelDaoExtension(), result);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String projectName, String groupLink) throws Exception {
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
    public String generateService(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getServiceHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getService().getServiceSavePath();
        String fileName = framework.getService().getServiceName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), framework.getService().getServiceExtension(), result);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateController(Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getControllerHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getController().getControllerSavePath();
        String fileName = framework.getController().getControllerName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), framework.getController().getControllerExtension(), result);

        return engine.render(result, metadataFinally);
    }

    public String generateListView(Framework framework, Language language, Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }

        String templateContent = loadListViewTemplate(editor);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);

        String fileName = framework.getView().getListViewName();
        String fileExtension = framework.getView().getViewExtension();
        String fileSavePath = framework.getView().getListViewSavePath();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), engine.simpleRender(fileExtension, metadataFinally), templateContent);

        return "";
    }

    @Override
    public String generateView(Framework framework, Language language, Editor editor, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }

        generateListView(framework, language, editor, tableMetadata, projectName, groupLink);

        return "";
    }

    @Override
    public void generateViewMainLayout(Framework framework, Language language, Editor editor,TableMetadata[] tableMetadatas, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLangageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLangageId() + "').");
        }

        String templateContent = loadViewTemplate(editor);

        //render base layout
        HashMap<String, Object> metadata = getViewMainLayoutHashMap(tableMetadatas, tableMetadata, language, editor);

        String result = engine.simpleRender(templateContent, metadata);

        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, projectName, groupLink);

        result =  engine.render(result, metadataFinally);

        metadataFinally.putAll(metadata);
        FileUtils.overwriteFileContentByName(engine.simpleRender(editor.getLayout().getDestinationPath(), metadataFinally), editor.getLayout().getName(), result);
    }

    private String loadTemplate(Framework framework) throws IOException {
        return FileUtils.getFileContent(Constantes.DATA_PATH + "/" + framework.getTemplate() + "." + Constantes.TEMPLATE_EXT);
    }

    private String loadViewTemplate(Editor editor) throws IOException {
        return FileUtils.getFileContent(Constantes.LAYOUT_DATA_PATH + "/" + editor.getTemplate() + "." + Constantes.TEMPLATE_EXT);
    }

    private String loadListViewTemplate(Editor editor) throws IOException {
        return FileUtils.getFileContent(Constantes.LAYOUT_DATA_PATH + "/" + editor.getListTemplate() + "." + Constantes.TEMPLATE_EXT);
    }
}
