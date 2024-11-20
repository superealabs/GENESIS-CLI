package genesis.config.langage.generator.project;

import genesis.config.Constantes;
import genesis.config.langage.FilesEdit;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.framework.APIGenerator;
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
import static handler.ProjectGeneratorHandler.*;
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
            String destinationFilePathSimple = projectFile.getDestinationPath() + projectFile.getFileName();
            String destinationFilePath = engine.simpleRender(destinationFilePathSimple, initializeHashMap);

            System.out.println("Rendering and copying file:");
            System.out.println("Source: " + sourceFilePath);
            System.out.println("Rendered destination: " + destinationFilePath);
            System.out.println();
            FileUtils.copyFile(sourceFilePath, destinationFilePath, "");
        }
    }

    public static void renderAndCopyFolders(List<Project.ProjectFolders> projectFolders, HashMap<String, Object> initializeHashMap) throws IOException {
        for (Project.ProjectFolders projectFolder : projectFolders) {
            String sourceFolderPath = projectFolder.getSourcePath();
            String destinationFolderPath = engine.simpleRender(projectFolder.getDestinationPath() + projectFolder.getFolderName(), initializeHashMap);

            System.out.println("Rendering and copying folder:");
            System.out.println("Source folder: " + sourceFolderPath);
            System.out.println("Rendered destination folder: " + destinationFolderPath);

            FileUtils.copyDirectory(sourceFolderPath, destinationFolderPath);
        }
    }

    public static void renderFilesEdits(List<FilesEdit> filesEdits, HashMap<String, Object> initializeHashMap) throws Exception {
        for (FilesEdit projectFile : filesEdits) {
            String destinationFilePath = engine.simpleRender(projectFile.getDestinationPath(), initializeHashMap);
            String fileName = engine.render(projectFile.getFileName(), initializeHashMap);
            String content = engine.render(projectFile.getContent(), initializeHashMap);
            String extension = projectFile.getExtension();

            // ALT rendering for specific placeholders
            content = engine.simpleRenderAlt(content, Map.of("spring-cloud.version", "${spring-cloud.version}"));
            content = engine.simpleRenderAlt(content, Map.of("spring.application.name", "${spring.application.name}"));
            content = engine.simpleRenderAlt(content, Map.of("server.port", "${server.port}"));
            content = engine.simpleRenderAlt(content, Map.of("spring.datasource.url", "${spring.datasource.url}"));
            content = engine.simpleRenderAlt(content, Map.of("spring.datasource.url", "${spring.datasource.url}"));
            content = engine.simpleRenderAlt(content, Map.of("HOSTNAME", "${HOSTNAME}"));
            content = engine.simpleRenderAlt(content, Map.of("server.port", "${server.port}"));
            content = engine.simpleRenderAlt(content, Map.of("spring.cloud.client.ip-address", "${spring.cloud.client.ip-address}"));
            content = engine.simpleRenderAlt(content, Map.of("security.user.username:admin", "${security.user.username:admin}"));
            content = engine.simpleRenderAlt(content, Map.of("security.user.password:admin", "${security.user.password:admin}"));
            content = engine.simpleRenderAlt(content, Map.of("security.user.role:admin", "${security.user.role:USER}"));

            System.out.println("\nEditing file:");
            System.out.println("Rendered destination path: " + destinationFilePath);
            System.out.println("Rendered file name: " + fileName);
            System.out.println("Extension: " + extension);

            FileUtils.createFile(destinationFilePath, fileName, extension, content);
            System.out.println("File edited and created successfully: " + fileName + "\n");
        }
    }

    private static void generateProjectFiles(List<TableMetadata> entities, Credentials credentials, String destinationFolder, String projectName, String groupLink, String projectPort, String projectDescription, HashMap<String, Object> langageConfiguration, HashMap<String, Object> frameworkConfiguration, Database database, Language language, Framework framework, Project project) throws Exception {
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

    public void generateBackendComponents(GenesisGenerator genesisGenerator,
                                          Framework framework,
                                          Language language,
                                          TableMetadata tableMetadata,
                                          String destinationFolder,
                                          String projectName,
                                          String groupLink,
                                          List<String> generationOptions,
                                          boolean generateComponentOnly) throws Exception {
        String renderedDestinationFolder = engine.simpleRender(destinationFolder, Map.of("projectName", projectName));
        System.out.println("Generating backend components for project: " + projectName + " at rendered destination: " + renderedDestinationFolder);
        System.out.println("The entity: "+tableMetadata.getTableName()+"\n");

        // Ensure the destination directory exists
        FileUtils.createDirectory(renderedDestinationFolder);

        if (generationOptions.contains(COMPONENT_MODEL) && framework.getModel().getToGenerate()) {
            System.out.println("Generating " + COMPONENT_MODEL + " component...");
            genesisGenerator.generateModel(framework, language, tableMetadata, renderedDestinationFolder, projectName, groupLink, generateComponentOnly);
        }

        if (generationOptions.contains(COMPONENT_DAO) && framework.getModelDao().getToGenerate()) {
            System.out.println("Generating " + COMPONENT_DAO + " component..."+tableMetadata.getClassName());
            genesisGenerator.generateDao(framework, language, tableMetadata, renderedDestinationFolder, projectName, groupLink, generateComponentOnly);
        }

        if (generationOptions.contains(COMPONENT_SERVICE) && framework.getService().getToGenerate()) {
            System.out.println("Generating " + COMPONENT_SERVICE + " component...");
            genesisGenerator.generateService(framework, language, tableMetadata, renderedDestinationFolder, projectName, groupLink, generateComponentOnly);
        }

        if (generationOptions.contains(COMPONENT_CONTROLLER) && framework.getController().getToGenerate()) {
            System.out.println("Generating " + COMPONENT_CONTROLLER + " component...");
            genesisGenerator.generateController(framework, language, tableMetadata, renderedDestinationFolder, projectName, groupLink, generateComponentOnly);
        }

        System.out.println("Backend component generation completed for project: " + projectName);
    }



    public void generateProject(Database database,
                                Language language,
                                Framework framework,
                                Project project,
                                Credentials credentials,
                                String destinationFolder,
                                String projectName,
                                String groupLink,
                                String projectPort,
                                String projectDescription,
                                HashMap<String, Object> languageConfiguration,
                                HashMap<String, Object> frameworkConfiguration,
                                List<String> entityNames,
                                Connection connection,
                                List<String> generationOptions,
                                boolean generateProjectStructure) throws Exception {

        if (generateProjectStructure) {
            // Existing logic for generating the full project
            generateFullProject(
                    database,
                    language,
                    framework,
                    project,
                    credentials,
                    destinationFolder,
                    projectName,
                    groupLink,
                    projectPort,
                    projectDescription,
                    languageConfiguration,
                    frameworkConfiguration,
                    entityNames,
                    connection,
                    generationOptions
            );
        } else {
            // Generate only the components
            generateComponentsOnly(
                    database,
                    language,
                    framework,
                    credentials,
                    destinationFolder,
                    projectName,
                    groupLink,
                    entityNames,
                    connection,
                    generationOptions
            );
        }
    }

    private void generateFullProject(Database database,
                                     Language language,
                                     Framework framework,
                                     Project project,
                                     Credentials credentials,
                                     String destinationFolder,
                                     String projectName,
                                     String groupLink,
                                     String projectPort,
                                     String projectDescription,
                                     HashMap<String, Object> languageConfiguration,
                                     HashMap<String, Object> frameworkConfiguration,
                                     List<String> entityNames,
                                     Connection connection,
                                     List<String> generationOptions) throws Exception {
        if (framework.getUseDB()) {
            try (Connection connex = (connection != null) ? connection : database.getConnection(credentials)) {
                List<TableMetadata> entities = database.getEntitiesByNames(entityNames, connex, credentials, language);
                GenesisGenerator genesisGenerator = new APIGenerator(ProjectGenerator.engine);

                for (TableMetadata tableMetadata : entities) {
                    generateBackendComponents(
                            genesisGenerator,
                            framework,
                            language,
                            tableMetadata,
                            destinationFolder,
                            projectName,
                            groupLink,
                            generationOptions,
                            false
                    );
                }

                generateProjectFiles(entities, credentials, destinationFolder, projectName, groupLink, projectPort,
                        projectDescription, languageConfiguration, frameworkConfiguration, database, language, framework, project);

            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
        } else {
            generateProjectFiles(null, credentials, destinationFolder, projectName, groupLink, projectPort,
                    projectDescription, languageConfiguration, frameworkConfiguration, database, language, framework, project);
        }
    }

    private void generateComponentsOnly(Database database,
                                        Language language,
                                        Framework framework,
                                        Credentials credentials,
                                        String destinationFolder,
                                        String projectName,
                                        String groupLink,
                                        List<String> entityNames,
                                        Connection connection,
                                        List<String> generationOptions) {
        if (framework.getUseDB()) {
            try (Connection connex = (connection != null) ? connection : database.getConnection(credentials)) {
                List<TableMetadata> entities = database.getEntitiesByNames(entityNames, connex, credentials, language);
                GenesisGenerator genesisGenerator = new APIGenerator(ProjectGenerator.engine);

                for (TableMetadata tableMetadata : entities) {
                    generateBackendComponents(
                            genesisGenerator,
                            framework,
                            language,
                            tableMetadata,
                            destinationFolder,
                            projectName,
                            groupLink,
                            generationOptions,
                            true
                    );
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
        }
    }


    public void generateProject(Database database,
                                Language language,
                                Framework framework,
                                Project project,
                                Credentials credentials,
                                String destinationFolder,
                                String projectName,
                                String groupLink,
                                String projectPort,
                                String projectDescription,
                                HashMap<String, Object> langageConfiguration,
                                HashMap<String, Object> frameworkConfiguration,
                                Connection connection) throws Exception {

        if (framework.getUseDB()) {
            try (Connection connex = (connection != null) ? connection : database.getConnection(credentials)) {
                List<TableMetadata> entities = database.getEntities(connex, credentials, language);
                GenesisGenerator genesisGenerator = new APIGenerator(ProjectGenerator.engine);

                for (TableMetadata tableMetadata : entities) {
                    generateBackendComponents(
                            genesisGenerator,
                            framework,
                            language,
                            tableMetadata,
                            destinationFolder,
                            projectName,
                            groupLink,
                            Arrays.asList(frameworkConfiguration.get("generationOptions").toString().split(",")),
                            false
                    );
                }

                generateProjectFiles(entities, credentials, destinationFolder, projectName, groupLink, projectPort, projectDescription, langageConfiguration, frameworkConfiguration, database, language, framework, project);

            } catch (Exception e) {
                throw new RuntimeException(e.getLocalizedMessage());
            }
        } else {
            generateProjectFiles(null, credentials, destinationFolder, projectName, groupLink, projectPort, projectDescription, langageConfiguration, frameworkConfiguration, database, language, framework, project);
        }
    }
}
