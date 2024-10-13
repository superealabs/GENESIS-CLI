package project;

import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import org.junit.jupiter.api.Test;

public class ProjectGeneratorCLI {
    public static void main(String[] args) {
        ProjectGeneratorHandler projectGeneratorHandler = new ProjectGeneratorHandler();
        projectGeneratorHandler.generateProject();
    }

    @Test
    void generateProject() {
        var credentials = new Credentials()
                .setHost("localhost")
                .setDatabaseName("test_db")
                //.setUser("postgres")
                //.setPwd("nikami")
                .setUser("nomena")
                .setPwd("root")
                .setPort("5432")
                .setTrustCertificate(true)
                .setUseSSL(true)
                .setAllowPublicKeyRetrieval(true);
        try {
            // Déclaration et initialisation des variables
            int databaseId = 1;
            int languageId = 0;
            int frameworkId = 0;
            int projectId = 0;
            String projectName = "TestREST";
            String groupLink = "rest";
            String projectPort = "8000";
            String logLevel = "INFO";
            String hibernateDdlAuto = "none";
            String frameworkVersion = "3.0.1";
            String projectDescription = "A Spring Boot BEGIN Project";
            String languageVersion = "17";

            // Création de l'instance du générateur de projet
            ProjectGenerator projectGenerator = new ProjectGenerator();

            // Appel de la méthode generateProject avec les arguments
            projectGenerator.generateProject(
                    databaseId,
                    languageId,
                    frameworkId,
                    projectId,
                    credentials,
                    projectName,
                    groupLink,
                    projectPort,
                    logLevel,
                    hibernateDdlAuto,
                    frameworkVersion,
                    projectDescription,
                    languageVersion
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
