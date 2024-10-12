package genesis.config.langage.generator.project;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.config.langage.generator.framework.MVCGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.engine.TemplateEngine;
import genesis.model.TableMetadata;
import utils.FileUtils;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import static genesis.config.langage.generator.project.ProjectMetadataProvider.getInitialHashMap;
import static genesis.config.langage.generator.project.ProjectMetadataProvider.getProjectFilesEditsHashMap;

public class ProjectGenerator {
    public static final Project[] projects;
    public static final Database[] databases;
    public static final Language[] languages;
    public static final Framework[] frameworks;
    public static final TemplateEngine engine;

    static {
        try {
            engine = new TemplateEngine();
            databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
            languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
            projects = FileUtils.fromYaml(Project[].class, FileUtils.getFileContent(Constantes.PROJECT_YAML));
            frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public ProjectGenerator() {
    }

    private void generateMVCComponents(GenesisGenerator genesisGenerator, Framework framework, Language language, TableMetadata tableMetadata, String projectName, String groupLink) throws Exception {
        genesisGenerator.generateModel(framework, language, tableMetadata, projectName, groupLink);
        genesisGenerator.generateDao(framework, language, tableMetadata, projectName, groupLink);
        genesisGenerator.generateService(framework, language, tableMetadata, projectName, groupLink);
        genesisGenerator.generateController(framework, language, tableMetadata, projectName, groupLink);
    }

    private void renderAndCopyFiles(Project project, HashMap<String, Object> initializeHashMap) throws IOException {
        for (Project.ProjectFiles projectFile : project.getProjectFiles()) {
            String sourceFilePath = projectFile.getSourcePath() + projectFile.getFileName();
            String destinationFilePath = projectFile.getDestinationPath() + projectFile.getFileName();
            FileUtils.copyFile(sourceFilePath, engine.simpleRender(destinationFilePath, initializeHashMap));
        }
    }

    private void renderAndCopyFolders(Project project, HashMap<String, Object> initializeHashMap) throws IOException {
        for (Project.ProjectFolders projectFolders : project.getProjectFolders()) {
            String sourceFolderPath = projectFolders.getSourcePath();
            String destinationFolderPath = projectFolders.getDestinationPath() + projectFolders.getFolderName();
            FileUtils.copyDirectory(sourceFolderPath, engine.simpleRender(destinationFolderPath, initializeHashMap));
        }
    }


    private void renderProjectFilesEdits(Project project, HashMap<String, Object> initializeHashMap) throws Exception {
        for (Project.ProjectFilesEdit projectFile : project.getProjectFilesEdits()) {

            String destinationFilePath = projectFile.getDestinationPath();
            String fileName = projectFile.getFileName();
            String content = projectFile.getContent();
            String extension = projectFile.getExtension();

            destinationFilePath = engine.simpleRender(destinationFilePath, initializeHashMap);
            content = engine.render(content, initializeHashMap);
            fileName = engine.render(fileName, initializeHashMap);

            FileUtils.createFile(destinationFilePath, fileName, extension, content);
        }
    }

    public void generateProject(int databaseId,
                                int languageId,
                                int frameworkId,
                                int projectId,
                                Credentials credentials,
                                String projectName,
                                String groupLink,
                                String projectPort,
                                String logLevel,
                                String hibernateDdlAuto,
                                String springBootVersion,
                                String projectDescription,
                                String languageVersion) {

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[databaseId];
        Framework framework = frameworks[frameworkId];
        Language language = languages[languageId];
        Project project = projects[projectId];

        try (Connection connection = database.getConnection(credentials)) {
            // Récupération des entités depuis la base de données
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            GenesisGenerator genesisGenerator = new MVCGenerator();

            // Génération des composants MVC pour chaque table
            for (TableMetadata tableMetadata : entities) {
                generateMVCComponents(genesisGenerator, framework, language, tableMetadata, projectName, groupLink);
            }

            // Initialisation des HashMap pour les templates
            HashMap<String, Object> initializeHashMap = getInitialHashMap(projectName, groupLink);
            HashMap<String, Object> projectFilesEditsHashMap = getProjectFilesEditsHashMap(projectName,
                    groupLink,
                    projectPort,
                    logLevel,
                    database,
                    credentials,
                    language,
                    hibernateDdlAuto,
                    springBootVersion,
                    projectDescription,
                    languageVersion,
                    framework);

            // Rendu et copie des fichiers du projet
            renderAndCopyFiles(project, initializeHashMap);

            // Rendu et copie des dossiers du projet
            renderAndCopyFolders(project, initializeHashMap);

            // Rendu et copie des dossiers du projet (edits)
            renderProjectFilesEdits(project, projectFilesEditsHashMap);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
