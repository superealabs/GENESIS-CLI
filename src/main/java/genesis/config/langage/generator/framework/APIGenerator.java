package genesis.config.langage.generator.framework;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.engine.TemplateEngine;
import genesis.connexion.model.TableMetadata;
import utils.FileUtils;

import java.io.IOException;
import java.util.HashMap;

import static genesis.config.langage.generator.framework.FrameworkMetadataProvider.*;

public class APIGenerator implements GenesisGenerator {
    private final TemplateEngine engine;

    public APIGenerator(TemplateEngine engine) {
        this.engine = engine;
    }

    public APIGenerator() {
        this.engine = new TemplateEngine();
    }

    @Override
    public String generateModel(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLanguageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLanguageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelHashMap(framework, language, tableMetadata);
        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, destinationFolder, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);

        String fileSavePath = framework.getModel().getModelSavePath();
        fileSavePath = engine.simpleRender(fileSavePath, metadataFinally);

        FileUtils.createFile(fileSavePath, tableMetadata.getClassName(), language.getExtension(), result);

        return result;
    }


    @Override
    public String generateDao(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLanguageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLanguageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getModelDaoHashMap(framework, language, tableMetadata);
        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, destinationFolder, projectName, groupLink);
        metadataFinally.putAll(getPrimaryModelDaoHashMap(framework, tableMetadata));

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);

        String fileSavePath = framework.getModelDao().getModelDaoSavePath();
        String fileName = framework.getModelDao().getModelDaoName();
        fileSavePath = engine.simpleRender(fileSavePath, metadataFinally);
        fileName = engine.simpleRender(fileName, metadataFinally);

        FileUtils.createFile(fileSavePath, fileName, language.getExtension(), result);

        // renderAndCopyAdditionalFiles
        ProjectGenerator.renderFilesEdits(framework.getModelDao().getModelDaoAdditionalFiles(), metadataFinally);

        return result;
    }

    @Override
    public String generateDao(Framework framework, Language language, TableMetadata[] tableMetadata, String destinationFolder, String projectName, String groupLink) {
        if (language.getId() != framework.getLanguageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLanguageId() + "').");
        }
//        String templateContent = framework.getModel().getModelDao().getContent();
//
//        Map<String, Object> metadata = getHashMapDaoUnique(framework, tableMetadata, projectName);
//        return engine.render(templateContent, metadata);
        return "";
    }

    @Override
    public String generateService(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLanguageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLanguageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getServiceHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, destinationFolder, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getService().getServiceSavePath();
        String fileName = framework.getService().getServiceName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), language.getExtension(), result);

        // renderAndCopyAdditionalFiles
        ProjectGenerator.renderFilesEdits(framework.getService().getServiceAdditionalFiles(), metadataFinally);

        return engine.render(result, metadataFinally);
    }

    @Override
    public String generateController(Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception {
        if (language.getId() != framework.getLanguageId()) {
            throw new RuntimeException("Incompatibility detected: the language '" + language.getName() + "' (provided ID: " + language.getId() + ") is not compatible with the framework '" + framework.getName() + "' (required language ID: '" + framework.getLanguageId() + "').");
        }
        String templateContent = loadTemplate(framework);

        // Render le template intermédiaire
        HashMap<String, Object> metadataPrimary = getControllerHashMap(framework, language, tableMetadata);

        String result = engine.simpleRender(templateContent, metadataPrimary);

        // Render le template final
        HashMap<String, Object> metadataFinally = getHashMapIntermediaire(tableMetadata, destinationFolder, projectName, groupLink);

        // Creation of the corresponding file
        result = engine.render(result, metadataFinally);
        String fileSavePath = framework.getController().getControllerSavePath();
        String fileName = framework.getController().getControllerName();
        FileUtils.createFile(engine.simpleRender(fileSavePath, metadataFinally), engine.simpleRender(fileName, metadataFinally), language.getExtension(), result);

        // renderAndCopyAdditionalFiles
        ProjectGenerator.renderFilesEdits(framework.getController().getControllerAdditionalFiles(), metadataFinally);

        return engine.render(result, metadataFinally);
    }

    private String loadTemplate(Framework framework) throws IOException {
        return FileUtils.getFileContent(Constantes.DATA_PATH + "/" + framework.getTemplate() + "." + Constantes.MODEL_TEMPLATE_EXT);
    }
}
