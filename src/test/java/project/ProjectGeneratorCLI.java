package project;

import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import org.junit.jupiter.api.Test;
import handler.ProjectGeneratorHandler;

public class ProjectGeneratorCLI {
    public static void main(String[] args) {
        ProjectGeneratorHandler projectGeneratorHandler = new ProjectGeneratorHandler();
        projectGeneratorHandler.generateProject();
    }

    @Test
    void generateProject() {
        var credentials = new Credentials()
                .setHost("localhost")
                .setDatabaseName("post_db")
                .setUser("postgres")
                .setPwd("nikami")
                .setPort("5432")
                .setTrustCertificate(true)
                .setUseSSL(true)
                .setAllowPublicKeyRetrieval(true);

        int editorId = 0;
        int projectId = 0;
        int databaseId = 1;
        int languageId = 0;
        int frameworkId = 0;
        String projectName = "layout1";
        String groupLink = "labs";
        String projectPort = "8000";
        String logLevel = "INFO";
        String hibernateDdlAuto = "none";
        String frameworkVersion = "3.0.1";
        String projectDescription = "Test Description";
        String languageVersion = "17";

        var projectGenerator = new ProjectGenerator();

        projectGenerator.generateProject(databaseId, languageId, frameworkId, projectId, editorId, credentials, projectName, groupLink, projectPort, logLevel, hibernateDdlAuto, frameworkVersion, projectDescription, languageVersion);
        System.out.println("\nProject generated successfully! 👨🏽‍💻");


    }
}
