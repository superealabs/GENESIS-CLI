package handler;

import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import lombok.Getter;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

@Getter
public class ProjectGenerationContext {
    private Database database;
    private Language language;
    private Framework framework;
    private Project project;
    private Credentials credentials;
    private String destinationFolder;
    private String projectName;
    private String groupLink;
    private String projectPort;
    private String projectDescription;
    private Map<String, Object> languageConfiguration;
    private Map<String, Object> frameworkConfiguration;
    private List<String> entityNames;
    private Connection connection;
    private List<String> generationOptions;
    private boolean generateProjectStructure;

    public ProjectGenerationContext setDatabase(Database database) {
        this.database = database;
        return this;
    }

    public ProjectGenerationContext setLanguage(Language language) {
        this.language = language;
        return this;
    }

    public ProjectGenerationContext setFramework(Framework framework) {
        this.framework = framework;
        return this;
    }

    public ProjectGenerationContext setProject(Project project) {
        this.project = project;
        return this;
    }

    public ProjectGenerationContext setCredentials(Credentials credentials) {
        this.credentials = credentials;
        return this;
    }

    public ProjectGenerationContext setDestinationFolder(String destinationFolder) {
        this.destinationFolder = destinationFolder;
        return this;
    }

    public ProjectGenerationContext setProjectName(String projectName) {
        this.projectName = projectName;
        return this;
    }

    public ProjectGenerationContext setGroupLink(String groupLink) {
        this.groupLink = groupLink;
        return this;
    }

    public ProjectGenerationContext setProjectPort(String projectPort) {
        this.projectPort = projectPort;
        return this;
    }

    public ProjectGenerationContext setProjectDescription(String projectDescription) {
        this.projectDescription = projectDescription;
        return this;
    }

    public ProjectGenerationContext setLanguageConfiguration(Map<String, Object> languageConfiguration) {
        this.languageConfiguration = languageConfiguration;
        return this;
    }

    public ProjectGenerationContext setFrameworkConfiguration(Map<String, Object> frameworkConfiguration) {
        this.frameworkConfiguration = frameworkConfiguration;
        return this;
    }

    public ProjectGenerationContext setEntityNames(List<String> entityNames) {
        this.entityNames = entityNames;
        return this;
    }

    public ProjectGenerationContext setConnection(Connection connection) {
        this.connection = connection;
        return this;
    }

    public ProjectGenerationContext setGenerationOptions(List<String> generationOptions) {
        this.generationOptions = generationOptions;
        return this;
    }

    public ProjectGenerationContext setGenerateProjectStructure(boolean generateProjectStructure) {
        this.generateProjectStructure = generateProjectStructure;
        return this;
    }
}