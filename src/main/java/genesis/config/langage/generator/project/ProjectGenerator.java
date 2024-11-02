package genesis.config.langage.generator.project;

import genesis.config.Constantes;
import genesis.config.langage.FilesEdit;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.framework.APIGenerator;
import genesis.config.langage.generator.framework.FrameworkMetadataProvider;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.model.TableMetadata;
import genesis.engine.TemplateEngine;
import utils.FileUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static genesis.config.langage.generator.framework.FrameworkMetadataProvider.getHashMapDaoGlobal;
import static genesis.config.langage.generator.project.ProjectMetadataProvider.getInitialHashMap;
import static genesis.config.langage.generator.project.ProjectMetadataProvider.getProjectFilesEditsHashMap;

public class ProjectGenerator {
    public static final Map<Integer, Project> projects;
    public static final Map<Integer, Database> databases;
    public static final Map<Integer, Language> languages;
    public static final Map<Integer, Framework> frameworks;
    public static final TemplateEngine engine;

    static {
        try {
            engine = new TemplateEngine();

            databases = Arrays.stream(FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON)))
                    .collect(Collectors.toMap(Database::getId, database -> database));

            languages = Arrays.stream(FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON)))
                    .collect(Collectors.toMap(Language::getId, language -> language));

            projects = Arrays.stream(FileUtils.fromYaml(Project[].class, FileUtils.getFileContent(Constantes.PROJECT_YAML)))
                    .collect(Collectors.toMap(Project::getId, project -> project));

            frameworks = Arrays.stream(FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML)))
                    .collect(Collectors.toMap(Framework::getId, framework -> framework));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ProjectGenerator() {
    }

    public static void renderAndCopyFiles(List<Project.ProjectFiles> projectFiles, HashMap<String, Object> initializeHashMap) throws IOException {
        for (Project.ProjectFiles projectFile : projectFiles) {
            String sourceFilePath = projectFile.getSourcePath() + projectFile.getFileName();
            String destinationFilePath = projectFile.getDestinationPath() + projectFile.getFileName();

            FileUtils.copyFile(sourceFilePath, engine.simpleRender(destinationFilePath, initializeHashMap));
        }
    }

    public static void renderAndCopyFolders(List<Project.ProjectFolders> projectFolders, HashMap<String, Object> initializeHashMap) throws IOException {
        for (Project.ProjectFolders projectFolder : projectFolders) {
            String sourceFolderPath = projectFolder.getSourcePath();
            String destinationFolderPath = projectFolder.getDestinationPath() + projectFolder.getFolderName();
            FileUtils.copyDirectory(sourceFolderPath, engine.simpleRender(destinationFolderPath, initializeHashMap));
        }
    }

    public static void renderFilesEdits(List<FilesEdit> filesEdits, HashMap<String, Object> initializeHashMap) throws Exception {
        for (FilesEdit projectFile : filesEdits) {

            String destinationFilePath = projectFile.getDestinationPath();
            String fileName = projectFile.getFileName();
            String content = projectFile.getContent();
            String extension = projectFile.getExtension();

            destinationFilePath = engine.simpleRender(destinationFilePath, initializeHashMap);
            content = engine.render(content, initializeHashMap);
            fileName = engine.render(fileName, initializeHashMap);

            // ALT
            content = engine.simpleRenderAlt(content, Map.of("spring-cloud.version", "${spring-cloud.version}"));

            FileUtils.createFile(destinationFilePath, fileName, extension, content);
        }
    }

    public void generateBackendComponents(GenesisGenerator genesisGenerator, Framework framework, Language language, TableMetadata tableMetadata, String destinationFolder, String projectName, String groupLink) throws Exception {
        if (framework.getModel().getToGenerate())
            genesisGenerator.generateModel(framework, language, tableMetadata, destinationFolder, projectName, groupLink);

        if (framework.getModelDao().getToGenerate())
            genesisGenerator.generateDao(framework, language, tableMetadata, destinationFolder, projectName, groupLink);

        if (framework.getService().getToGenerate())
            genesisGenerator.generateService(framework, language, tableMetadata, destinationFolder, projectName, groupLink);

        if (framework.getController().getToGenerate())
            genesisGenerator.generateController(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
    }

    public void generateProject(Integer databaseId,
                                int languageId,
                                int frameworkId,
                                int projectId,
                                Credentials credentials,
                                String destinationFolder,
                                String projectName,
                                String groupLink,
                                String projectPort,
                                String projectDescription,
                                HashMap<String, String> langageConfiguration,
                                HashMap<String, String> frameworkConfiguration,
                                Connection connection) throws Exception {

        Framework framework = frameworks.get(frameworkId);
        Language language = languages.get(languageId);
        Project project = projects.get(projectId);

        Database database = null;
        if (framework.getUseDB()) {
            database = databases.get(databaseId);
            try (Connection connex = (connection != null) ? connection : database.getConnection(credentials)) {
                List<TableMetadata> entities = database.getEntities(connex, credentials, language);
                GenesisGenerator genesisGenerator = new APIGenerator(ProjectGenerator.engine);

                for (TableMetadata tableMetadata : entities) {
                    generateBackendComponents(genesisGenerator, framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                }
                generateProjectFiles(entities, credentials, destinationFolder, projectName, groupLink, projectPort, projectDescription, langageConfiguration, frameworkConfiguration, database, language, framework, project);

            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
        }
        else {
            generateProjectFiles(null, credentials, destinationFolder, projectName, groupLink, projectPort, projectDescription, langageConfiguration, frameworkConfiguration, database, language, framework, project);
        }
    }

    private static void generateProjectFiles(List<TableMetadata> entities, Credentials credentials, String destinationFolder, String projectName, String groupLink, String projectPort, String projectDescription, HashMap<String, String> langageConfiguration, HashMap<String, String> frameworkConfiguration, Database database, Language language, Framework framework, Project project) throws Exception {
        HashMap<String, Object> initializeHashMap = getInitialHashMap(destinationFolder, projectName, groupLink);
        HashMap<String, Object> projectFilesEditsHashMap = getProjectFilesEditsHashMap(
                destinationFolder,
                projectName,
                groupLink,
                projectPort,
                database,
                credentials,
                language,
                projectDescription,
                langageConfiguration,
                framework,
                frameworkConfiguration);

        if (framework.getUseDB())
            projectFilesEditsHashMap.putAll(getHashMapDaoGlobal(framework, entities, projectName));

        renderAndCopyFiles(project.getProjectFiles(), initializeHashMap);
        renderAndCopyFolders(project.getProjectFolders(), initializeHashMap);
        renderFilesEdits(project.getProjectFilesEdits(), projectFilesEditsHashMap);
        renderFilesEdits(framework.getAdditionalFiles(), projectFilesEditsHashMap);
    }
}
