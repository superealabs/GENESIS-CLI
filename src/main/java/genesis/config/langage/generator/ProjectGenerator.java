package genesis.config.langage.generator;

import utils.FileUtils;
import genesis.config.Constantes;
import genesis.connexion.Database;
import genesis.model.TableMetadata;
import genesis.connexion.Credentials;
import genesis.engine.TemplateEngine;
import genesis.config.langage.Project;
import genesis.config.langage.Language;
import genesis.config.langage.Framework;
import genesis.connexion.providers.PostgreSQLDatabase;

import java.util.HashMap;
import java.io.IOException;
import java.sql.Connection;

public class ProjectGenerator {
    TemplateEngine engine = new TemplateEngine();

    private static final Project[] projects;
    private static final Database[] databases;
    private static final Language[] languages;
    private static final Framework[] frameworks;

    static {
        try {
            databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
            languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
            projects = FileUtils.fromYaml(Project[].class, FileUtils.getFileContent(Constantes.PROJECT_YAML));
            frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ProjectGenerator() throws IOException {
    }

    private static HashMap<String, Object> getInitializeHashMap(String projectName) {
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("projectName", projectName);

        return metadata;
    }


    public void generateMavenProject(int databaseId, int languageId, int frameworkId, int projectId, Credentials credentials, String projectName){
        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[databaseId];
        Framework framework = frameworks[frameworkId];
        Language language = languages[languageId];
        Project project = projects[projectId];
        
        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            GenesisGenerator genesisGenerator = new MVCGenerator();

            for (TableMetadata tableMetadata : entities) {
                genesisGenerator.generateModel(framework, language, tableMetadata, "TestProject");
                genesisGenerator.generateDao(framework, language, tableMetadata, "TestProject");
                genesisGenerator.generateService(framework, language, tableMetadata, "TestProject");
                genesisGenerator.generateController(framework, language, tableMetadata, "TestProject");
            }

            // Template with basic hash map
            HashMap<String, Object> initializaHashMap = getInitializeHashMap(projectName);

            // Rendering local files
            for (Project.ProjectFiles projectFile : project.getProjectFiles()) {
                String sourceFilePath = projectFile.getSourcePath() + projectFile.getFileName();
                String destinationFilePath = projectFile.getDestinationPath() + projectFile.getFileName();
                FileUtils.copyFile(sourceFilePath, engine.simpleRender(destinationFilePath, initializaHashMap));
            }

            // Rendering local folders
            for (Project.ProjectFolders projectFolders : project.getProjectFolders()) {
                String sourceFolderPath = projectFolders.getSourcePath();
                String destinationFolderPath = projectFolders.getDestinationPath() + projectFolders.getFolderName();
                FileUtils.copyDirectory(sourceFolderPath, engine.simpleRender(destinationFolderPath, initializaHashMap));
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
