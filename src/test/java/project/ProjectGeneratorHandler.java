package project;

import genesis.config.langage.ConfigurationMetadata;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;

import java.sql.Connection;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectGeneratorHandler {
    private final Credentials credentials;
    private final ProjectGenerator projectGenerator;

    public ProjectGeneratorHandler() {
        this.credentials = new Credentials();
        projectGenerator = new ProjectGenerator();
    }

    void generateProject() {
        System.out.println("\n** Welcome to the GENESIS-CLI ** \n\n Let's get started 🚀\n");

        try (Scanner scanner = new Scanner(System.in)) {
            int languageId = getLanguageSelection(scanner);
            Language language = ProjectGenerator.languages.get(languageId);

            int frameworkId = getFrameworkSelection(scanner, language);
            Framework framework = ProjectGenerator.frameworks.get(frameworkId);

            int databaseId;
            Database database = null;
            Connection connection = null;
            if (framework.getUseDB()) {
                databaseId = getDatabaseId(scanner);
                database = ProjectGenerator.databases.get(databaseId);
                connection = configureCredentials(scanner, database);
            }

            int projectId = getProjectId(scanner, framework);
            var project = ProjectGenerator.projects.get(projectId);

            String destinationFolder = FolderSelectorCombo.selectDestinationFolder(scanner);

            String projectName = getNonEmptyInput(scanner, "Enter the project name");

            String groupLink = "";
            if (framework.getWithGroupId())
                groupLink = getNonEmptyInput(scanner, "Enter the group link");

            String projectPort = getValidPort(scanner);
            String projectDescription = getNonEmptyInput(scanner, "Enter the project description");

            HashMap<String, String> frameworkConfiguration = configureFramework(scanner, framework);
            HashMap<String, String> languageConfiguration = configureLangage(scanner, language);

            String input = getNonEmptyInput(scanner, "Use a Eureka Server? (y/n)");
            boolean useEurekaServer = input.equalsIgnoreCase("y");

            if (useEurekaServer)
                frameworkConfiguration.putAll(configureFrameworkWithEureka(scanner, framework));

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
                    connection
            );
            System.out.println("\nProject generated successfully! 👨🏽‍💻\n\nSee you on the next project 👋🏼\n");

        } catch (Exception e) {
            System.out.println("\nAn error occurred during project generation:");
            e.printStackTrace();
        }
    }

    private Connection configureCredentials(Scanner scanner, Database database) {
        configureCommonCredentials(scanner, database);
        configureDatabaseSpecificCredentials(scanner, database);

        System.out.println("\nTesting database connection...");
        Connection connection = testDatabaseConnection(database);

        if (connection != null) {
            System.out.println("Connection successful 🎉\n");
        } else {
            connection = handleConnectionFailure(scanner, database);
        }
        return connection;
    }

    private void configureCommonCredentials(Scanner scanner, Database database) {
        credentials.setHost(getDefaultInput(scanner, "Enter the database host", "localhost"));
        credentials.setDatabaseName(getDefaultInput(scanner, "Enter the database name", "test_db"));
        credentials.setSchemaName(getDefaultInput(scanner, "Enter the schema name", "public"));
        credentials.setUser(getDefaultInput(scanner, "Enter the database user", "nomena"));
        credentials.setPwd(getDefaultInput(scanner, "Enter the database password", "root"));
        credentials.setPort(getDefaultInput(scanner, "Enter the database port", database.getPort()));
    }

    private void configureDatabaseSpecificCredentials(Scanner scanner, Database database) {
        switch (database.getName()) {
            case "MySQL":
                configureMySQLCredentials(scanner);
                break;
            case "Oracle":
                configureOracleCredentials(scanner, database);
                break;
            case "PostgreSQL":
                // No specific configuration needed
                break;
            case "SQL Server":
                configureSQLServerCredentials(scanner);
                break;
            default:
                System.out.println("Database type not recognized. Please check the configuration.");
                break;
        }
    }

    private void configureMySQLCredentials(Scanner scanner) {
        credentials.setTrustCertificate(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable trust certificate for MySQL (true/false)", "true")
        ));
        credentials.setUseSSL(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable SSL for MySQL (true/false)", "true")
        ));
        credentials.setAllowPublicKeyRetrieval(Boolean.parseBoolean(
                getDefaultInput(scanner, "Allow public key retrieval for MySQL (true/false)", "true")
        ));
    }

    private void configureOracleCredentials(Scanner scanner, Database database) {
        credentials.setServiceName(getDefaultInput(scanner, "Enter the service name for Oracle",
                database.getServiceName() != null ? database.getServiceName() : "ORCLCDB"));
        credentials.setTrustCertificate(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable trust certificate for Oracle (true/false)", "true")
        ));
        credentials.setUseSSL(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable SSL for Oracle (true/false)", "true")
        ));
        credentials.setDriverType(getDefaultInput(scanner, "Enter the driver type for Oracle",
                database.getDriverType() != null ? database.getDriverType() : "thin"));
    }

    private void configureSQLServerCredentials(Scanner scanner) {
        credentials.setTrustCertificate(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable trust certificate for SQL Server (true/false)", "true")
        ));
        credentials.setUseSSL(Boolean.parseBoolean(
                getDefaultInput(scanner, "Enable SSL for SQL Server (true/false)", "true")
        ));
    }

    private Connection handleConnectionFailure(Scanner scanner, Database database) {
        System.out.println("Connection failed. Would you like to modify the JDBC URL? (yes/no)");
        String choice = scanner.next().trim().toLowerCase();
        if ("yes".equals(choice)) {
            String customUrl = getNonEmptyInput(scanner, "Enter the custom JDBC URL:");
            Connection connection = testDatabaseConnection(database, customUrl);
            if (connection != null) {
                System.out.println("Connection successful with custom URL.");
            } else {
                System.out.println("Connection failed with custom URL.");
            }
            return connection;
        }
        return null; // If the user does not want to modify the URL
    }

    private Connection testDatabaseConnection(Database database) {
        try {
            Connection connection = database.getConnection(credentials);
            return connection.isValid(2) ? connection : null;
        } catch (Exception e) {
            System.out.println("Error testing connection: " + e.getMessage());
            return null;
        }
    }

    private Connection testDatabaseConnection(Database database, String customUrl) {
        try {
            Connection connection = database.getConnection(credentials, customUrl);
            return connection.isValid(2) ? connection : null;
        } catch (Exception e) {
            System.out.println("Error testing connection with custom URL: " + e.getMessage());
            return null;
        }
    }

    private String getDefaultInput(Scanner scanner, String prompt, String defaultValue) {
        System.out.print(prompt + " (default: " + defaultValue + "): ");
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            System.out.println("Using default: " + defaultValue + "\n");
            return defaultValue;
        } else {
            System.out.println("Using: " + input + "\n");
            return input;
        }
    }

    private String getNonEmptyInput(Scanner scanner, String prompt) {
        String input;
        do {
            System.out.print(prompt + ": ");
            input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Please enter a non-empty value.");
            }
        } while (input.isEmpty());

        System.out.println("Using: " + input + "\n");
        return input;
    }

    private String getValidPort(Scanner scanner) {
        while (true) {
            System.out.print("Enter the project port: ");
            String input = scanner.nextLine().trim();
            try {
                int port = Integer.parseInt(input);
                if (port > 0 && port <= 65535) {
                    System.out.println("Using: " + port + "\n");
                    return input;
                } else {
                    System.out.println("Error: Please enter a valid port number between 1 and 65535.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
            }
        }
    }

    private int getDatabaseId(Scanner scanner) {
        Map<Integer, String> databaseNames = ProjectGenerator.databases.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            if (entry.getValue() == null) {
                                throw new IllegalArgumentException("Entry with key " + entry.getKey() + " has a null value.");
                            }
                            return entry.getValue().getName();
                        }
                ));

        return getSelectionId(scanner, databaseNames, "database");
    }

    private int getLanguageSelection(Scanner scanner) {
        Map<Integer, String> languageNames = ProjectGenerator.languages.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()
                ));

        if (languageNames.isEmpty()) {
            System.out.println("No valid languages found.");
            return -1;
        }

        return getSelectionId(scanner, languageNames, "language");
    }

    private int getFrameworkSelection(Scanner scanner, Language language) {
        Map<Integer, Framework> frameworks = ProjectGenerator.frameworks;

        Map<Integer, String> validFrameworkNames = frameworks.entrySet().stream()
                .filter(entry -> entry.getValue().getLanguageId() == language.getId())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()
                ));


        if (validFrameworkNames.isEmpty()) {
            System.out.println("No valid frameworks found for the selected language.");
            return -1;
        }
        return getSelectionId(scanner, validFrameworkNames, "framework");
    }

    private int getProjectId(Scanner scanner, Framework framework) {
        Map<Integer, Project> projects = ProjectGenerator.projects;
        Map<Integer, String> languageNames = projects.entrySet().stream()
                .filter(entry -> entry.getValue().getFrameworkIds().contains(framework.getId()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()
                ));

        if (languageNames.isEmpty()) {
            System.out.println("No valid projects found for the selected framework.\n");
            return -1;
        }

        return getSelectionId(scanner, languageNames, "project");
    }

    private int getSelectionId(Scanner scanner, Map<Integer, String> options, String optionType) {
        List<Integer> keys = new ArrayList<>(options.keySet());
        Collections.sort(keys);

        while (true) {
            System.out.println("Options :");
            for (int i = 0; i < keys.size(); i++) {
                System.out.println((i + 1) + ") " + options.get(keys.get(i)));
            }

            System.out.print("Enter the " + optionType + " index: ");
            try {
                int index = Integer.parseInt(scanner.nextLine()) - 1;
                if (index >= 0 && index < keys.size()) {
                    int selectedId = keys.get(index);
                    System.out.println("Using: " + (index + 1) + "- " + options.get(selectedId) + "\n");
                    return selectedId;
                } else {
                    System.out.println("Error: Invalid index (" + options + ").Please select a valid option." + "\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
            }
        }
    }

    private HashMap<String, String> configureFramework(Scanner scanner, Framework framework) {
        return configureOptions(scanner, framework.getConfigurations());
    }

    private HashMap<String, String> configureFrameworkWithEureka(Scanner scanner, Framework framework) {
        HashMap<String, String> config = configureOptions(scanner, framework.getEurekaClientConfigurations());
        framework.setUseCloud(true);
        framework.setUseEurekaServer(true);
        return config;
    }

    private HashMap<String, String> configureLangage(Scanner scanner, Language language) {
        return configureOptions(scanner, language.getConfigurations());
    }

    private HashMap<String, String> configureOptions(Scanner scanner, List<ConfigurationMetadata> configurations) {
        HashMap<String, String> configMap = new HashMap<>();
        for (ConfigurationMetadata config : configurations) {
            String option = getUserInput(scanner, config);
            configMap.put(config.getVariableName(), option);
        }
        return configMap;
    }

    public String getUserInput(Scanner scanner, ConfigurationMetadata config) {
        System.out.println("Configuration: " + config.getName());

        List<String> options = config.getOptions();
        if (options == null || options.isEmpty()) {
            System.out.println("No options available for this configuration.");
            return config.getDefaultOption();
        }

        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ") " + options.get(i));
        }
        System.out.println((options.size() + 1) + ") Other");

        while (true) {
            try {
                System.out.print("Select an option ID (default: " + config.getDefaultOption() + "): ");
                String input = scanner.nextLine();

                if (input.isEmpty()) {
                    System.out.println("Using default option: " + config.getDefaultOption() + "\n");
                    return config.getDefaultOption();
                }

                int optionId = Integer.parseInt(input) - 1;

                if (optionId >= 0 && optionId < options.size()) {
                    System.out.println("Using option: " + optionId + " - " + options.get(optionId) + "\n");
                    return options.get(optionId);
                } else if (optionId == options.size()) {
                    System.out.print("Enter your custom option: ");
                    String customOption = scanner.nextLine();
                    System.out.println("Using custom option: " + customOption + "\n");
                    return customOption;
                } else {
                    System.out.println("Error: Invalid option. Please select a valid option.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
            }
        }
    }
}