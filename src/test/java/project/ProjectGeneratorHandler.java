package project;

import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.util.Scanner;

public class ProjectGeneratorHandler {

    Credentials credentials;
    ProjectGenerator projectGenerator;

    public ProjectGeneratorHandler() {
        this.credentials = new Credentials();
        projectGenerator = new ProjectGenerator();
    }

    void generateProject() {
        System.out.println("\n** Welcome to the GENESIS-CLI ** \n\n Let's get started 🚀\n");

        try (Scanner scanner = new Scanner(System.in)) {
            configureCredentials(scanner);
            int editorId = getEditorId(scanner);
            int projectId = getProjectId(scanner);
            String logLevel = getLogLevel(scanner);
            int databaseId = getDatabaseId(scanner);
            int languageId = getLanguageId(scanner);
            String groupLink = getGroupLink(scanner);
            int frameworkId = getFrameworkId(scanner);
            String projectName = getProjectName(scanner);
            String projectPort = getProjectPort(scanner);
            String languageVersion = getLanguageVersion(scanner);
            String hibernateDdlAuto = getHibernateDdlAuto(scanner);
            String frameworkVersion = getFrameworkVersion(scanner);
            String projectDescription = getProjectDescription(scanner);

            projectGenerator.generateProject(databaseId, languageId, frameworkId, projectId, editorId, credentials, projectName, groupLink, projectPort, logLevel, hibernateDdlAuto, frameworkVersion, projectDescription, languageVersion);
            System.out.println("\nProject generated successfully! 👨🏽‍💻");

        } catch (Exception e) {
            System.out.println("\nAn error occurred during project generation:");
            e.printStackTrace();
        }
    }

    private void configureCredentials(Scanner scanner) {
        credentials.setHost(askForInput(scanner, "Enter the database host", "localhost"));
        credentials.setDatabaseName(askForInput(scanner, "Enter the database name", "test_db"));
        credentials.setUser(askForInput(scanner, "Enter the database user", "postgres"));
        credentials.setPwd(askForInput(scanner, "Enter the database password", "root"));
        credentials.setPort(askForInput(scanner, "Enter the database port", "5432"));
        System.out.println();
    }

    private String askForInput(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(prompt + " (default: " + defaultValue + "): ");
        String input = scanner.nextLine();
        String value = input.isEmpty() ? defaultValue : input;
        System.out.println("Using: " + value + "\n");
        return value;
    }

    private int getDatabaseId(Scanner scanner) {
        Database[] databases = ProjectGenerator.databases;
        System.out.println("Options:");
        for (int i = 0; i < databases.length; i++) {
            System.out.println(i + " : " + databases[i].getName());
        }
        System.out.print("Enter the database ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Using: " + id + "\n");
        return id;
    }

    private int getLanguageId(Scanner scanner) {
        Language[] languages = ProjectGenerator.languages;
        System.out.println("Options:");
        for (int i = 0; i < languages.length; i++) {
            System.out.println(i + " : " + languages[i].getName());
        }
        System.out.print("Enter the language ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Using: " + id + "\n");
        return id;
    }

    private int getFrameworkId(Scanner scanner) {
        Framework[] frameworks = ProjectGenerator.frameworks;
        System.out.println("Options:");
        for (int i = 0; i < frameworks.length; i++) {
            System.out.println(i + " : " + frameworks[i].getName());
        }
        System.out.print("Enter the framework ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Using: " + id + "\n");
        return id;
    }

    private int getProjectId(Scanner scanner) {
        Project[] projects = ProjectGenerator.projects;
        System.out.println("Options:");
        for (int i = 0; i < projects.length; i++) {
            System.out.println(i + " : " + projects[i].getName());
        }
        System.out.print("Enter the project ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Using: " + id + "\n");
        return id;
    }

    private int getEditorId(Scanner scanner) {
        Editor[] editors = ProjectGenerator.editors;
        System.out.println("Options:");
        for (int i = 0; i < editors.length; i++) {
            System.out.println(i + " : " + editors[i].getName());
        }
        System.out.print("Enter the project ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        System.out.println("Using: " + id + "\n");
        return id;
    }

    private String getProjectName(Scanner scanner) {
        System.out.print("Enter the project name: ");
        String name = scanner.nextLine();
        System.out.println("Using: " + name + "\n");
        return name;
    }

    private String getGroupLink(Scanner scanner) {
        System.out.print("Enter the group link: ");
        String link = scanner.nextLine();
        System.out.println("Using: " + link + "\n");
        return link;
    }

    private String getProjectPort(Scanner scanner) {
        System.out.print("Enter the project port: ");
        String port = scanner.nextLine();
        System.out.println("Using: " + port + "\n");
        return port;
    }

    private String getLogLevel(Scanner scanner) {
        String[] logLevels = {"INFO", "DEBUG", "ERROR", "WARN", "TRACE", "OFF"};
        System.out.println("Options:");
        for (int i = 0; i < logLevels.length; i++) {
            System.out.println(i + " : " + logLevels[i]);
        }
        System.out.print("Enter the log level ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        String logLevel = logLevels[id];
        System.out.println("Using: " + logLevel + "\n");
        return logLevel;
    }

    private String getHibernateDdlAuto(Scanner scanner) {
        String[] ddlOptions = {"create", "create-drop", "none", "validate"};
        System.out.println("Options:");
        for (int i = 0; i < ddlOptions.length; i++) {
            System.out.println(i + " : " + ddlOptions[i]);
        }
        System.out.print("Enter Hibernate DDL auto configuration ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        String ddlAuto = ddlOptions[id];
        System.out.println("Using: " + ddlAuto + "\n");
        return ddlAuto;
    }

    private String getFrameworkVersion(Scanner scanner) {
        String[] frameworkVersions = {"3.3.4", "3.2.10"};
        System.out.println("Options:");
        for (int i = 0; i < frameworkVersions.length; i++) {
            System.out.println(i + " : " + frameworkVersions[i]);
        }
        System.out.print("Enter the framework version ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        String version = frameworkVersions[id];
        System.out.println("Using: " + version + "\n");
        return version;
    }

    private String getProjectDescription(Scanner scanner) {
        System.out.print("Enter the project description: ");
        String description = scanner.nextLine();
        System.out.println("Using: " + description + "\n");
        return description;
    }

    private String getLanguageVersion(Scanner scanner) {
        String[] languageVersions = {"17", "21", "23"};
        System.out.println("Options:");
        for (int i = 0; i < languageVersions.length; i++) {
            System.out.println(i + " : " + languageVersions[i]);
        }
        System.out.print("Enter the language version ID: ");
        int id = Integer.parseInt(scanner.nextLine());
        String version = languageVersions[id];
        System.out.println("Using: " + version + "\n");
        return version;
    }
}
