package project;

import genesis.config.Constantes;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

public class ProjectGeneratorCLI {
    public static void main(String[] args) {
        ProjectGeneratorHandler projectGeneratorHandler = new ProjectGeneratorHandler();
        projectGeneratorHandler.generateProject();
    }

    /* TODO : Génération .NET EF Core
        frameworks.yaml :
            - Model : juste content OK
            - ModelDao :
                - classe Repo extends Repo ... : OK
                - unique False OK
            - Service :
                - Interface : OK
                - additional file : impl OK
            - Controller : juste content OK
        projects.yaml :
            - projectFilesEdits :
                - Project.sln : OK
                - Project.csproj : OK
                - Program.cs : OK
                - launchSettings.json : OK
                - appSettings.json : OK
                - HttpFile : OK
                - DAO :
                    - DbContext : OK
                    - Repository : OK
                    - IRepository : OK
                - IService : OK
                - projectFiles :
                    - appSettings.Developpement.json OK
                - projectFolders : none
        - Génération de la solution :
            - renderAndCopyAdditionalFiles :
                - ModelDao OK
                - Service ok
    */

    @Test
    void generateProjectSpring() {
        var credentials = new Credentials()
                .setHost("localhost")
                .setPort("5432")
                .setSchemaName("public")
                .setDatabaseName("test_db")
                .setUser("nomena")
                .setPwd("root")
                .setTrustCertificate(true)
                .setUseSSL(true)
                .setAllowPublicKeyRetrieval(true);

        try {

            int databaseId = Constantes.PostgreSQL_ID;
            int languageId = Constantes.Java_ID;
            int frameworkId = Constantes.Spring_REST_API_ID;
            int projectId = Constantes.Maven_ID;

            var database = ProjectGenerator.databases.get(databaseId);
            var language = ProjectGenerator.languages.get(languageId);
            var framework = ProjectGenerator.frameworks.get(frameworkId);
            var project = ProjectGenerator.projects.get(projectId);

/*
            String projectName = "ProjetIrai";
            String groupLink = "com.flm.irai";
            String projectPort = "8080";
            String logLevel = "INFO";
            String hibernateDdlAuto = "none";
            String projectDescription = "Projet irai - Backend - Spring boot";
            String frameworkVersion = "3.3.5";
            String languageVersion = "21";
            String destinationFolder = "/Users/nomena/STAGE/GENESIS/generated/Fana";
*/
            String projectName = "TestRest";
            String groupLink = "com.labs";
            String projectPort = "8000";
            String logLevel = "INFO";
            String hibernateDdlAuto = "none";
            String projectDescription = "Test Project";
            String frameworkVersion = "3.3.5";
            String languageVersion = "21";
            String destinationFolder = "/Users/nomena/STAGE/GENESIS/generated/service";

            ProjectGenerator projectGenerator = new ProjectGenerator();

            HashMap<String, String> frameworkConfiguration = new HashMap<>();
            frameworkConfiguration.put("hibernateDdlAuto", hibernateDdlAuto);
            frameworkConfiguration.put("loggingLevel", logLevel);
            frameworkConfiguration.put("frameworkVersion", frameworkVersion);

            //===== USE EUREKA SERVER =======//
            framework.setUseCloud(true);
            framework.setUseEurekaServer(true);
            frameworkConfiguration.put("eurekaServerHttpProtocol", "http");
            frameworkConfiguration.put("eurekaServerHost", "localhost");
            frameworkConfiguration.put("eurekaServerPort", "8761");
            frameworkConfiguration.put("projectNonSecurePort", projectPort);
            //==============================//

            HashMap<String, String> languageConfiguration = new HashMap<>();
            languageConfiguration.put("languageVersion", languageVersion);

            projectGenerator.generateProject(
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
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void generateProjectNET() {
        var credentials = new Credentials()
                .setHost("localhost")
                .setPort("5432")
                .setSchemaName("public")
                .setDatabaseName("flm_dev")
                .setUser("nomena")
                .setPwd("root");

        try {
            int databaseId = Constantes.PostgreSQL_ID;
            int languageId = Constantes.CSharp_ID;
            int frameworkId = Constantes.NET_ID;
            int projectId = Constantes.ASP_ID;

            var database = ProjectGenerator.databases.get(databaseId);
            var language = ProjectGenerator.languages.get(languageId);
            var framework = ProjectGenerator.frameworks.get(frameworkId);
            var project = ProjectGenerator.projects.get(projectId);

            String projectName = "TestRestNetFlm";
            String groupLink = "";
            String projectPort = "9001";
            String logLevel = "Information";
            String projectDescription = "An ASP.NET BEGIN Project";
            String frameworkVersion = "8.0";
            String languageVersion = "";
            String destinationFolder = "/Users/nomena/STAGE/GENESIS/generated";

            ProjectGenerator projectGenerator = new ProjectGenerator();

            HashMap<String, String> frameworkConfiguration = new HashMap<>();
            frameworkConfiguration.put("loggingLevel", logLevel);
            frameworkConfiguration.put("frameworkVersion", frameworkVersion);

            HashMap<String, String> languageConfiguration = new HashMap<>();
            frameworkConfiguration.put("languageVersion", languageVersion);

            projectGenerator.generateProject(
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
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void generateProjectSpringEurekaServer() {
        try {

            int languageId = Constantes.Java_ID;
            int frameworkId = Constantes.Spring_Eureka_Server_ID;
            int projectId = Constantes.Maven_ID;

            var language = ProjectGenerator.languages.get(languageId);
            var framework = ProjectGenerator.frameworks.get(frameworkId);
            var project = ProjectGenerator.projects.get(projectId);

            String projectName = "TestEurekaServer";
            String groupLink = "labs.test";
            String projectPort = "8761";
            String logLevel = "INFO";
            String projectDescription = "Eureka Server Project For Testing Genesis API Generator";
            String frameworkVersion = "3.3.5";
            String languageVersion = "21";

            String destinationFolder = "/Users/nomena/STAGE/GENESIS/generated/discovery";

            ProjectGenerator projectGenerator = new ProjectGenerator();

            HashMap<String, String> frameworkConfiguration = new HashMap<>();
            frameworkConfiguration.put("loggingLevel", logLevel);
            frameworkConfiguration.put("frameworkVersion", frameworkVersion);

            HashMap<String, String> languageConfiguration = new HashMap<>();
            languageConfiguration.put("languageVersion", languageVersion);

            projectGenerator.generateProject(
                    null,
                    language,
                    framework,
                    project,
                    null,
                    destinationFolder,
                    projectName,
                    groupLink,
                    projectPort,
                    projectDescription,
                    languageConfiguration,
                    frameworkConfiguration,
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
