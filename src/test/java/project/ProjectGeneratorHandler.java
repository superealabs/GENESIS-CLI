package project;

import genesis.config.langage.ConfigurationMetadata;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.Project;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.SQLRunner;
import utils.FileUtils;

import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
            // INITIALISATION
            String projectName = getNonEmptyInput(scanner, "Enter the project name");
            int languageId = getLanguageSelection(scanner);
            Language language = ProjectGenerator.languages.get(languageId);

            String baseFramework = getBaseFrameworkSelection(scanner, language);

            int projectId = getProjectId(scanner, baseFramework);
            var project = ProjectGenerator.projects.get(projectId);
            String destinationFolder = FolderSelectorCombo.selectDestinationFolder(scanner);


            // TYPE DE PROJET
            int frameworkId = getFrameworkSelection(scanner, language, baseFramework);
            Framework framework = ProjectGenerator.frameworks.get(frameworkId);


            // CONFIGURATION DE LA BASE DE DONNÉES
            int databaseId;
            Database database = null;
            Connection connection = null;
            List<String> entityNames = new ArrayList<>();
            if (framework.getUseDB()) {
                databaseId = getDatabaseId(scanner);
                database = ProjectGenerator.databases.get(databaseId);
                connection = configureCredentials(scanner, database);

                handleScriptExecution(scanner, database, connection);

                // Récupérer les noms des entités et gérer la sélection
                List<String> allTableNames = fetchEntityNames(database, connection);
                entityNames = handleEntitySelection(scanner, allTableNames);
            }


            // CONFIGURATION PERSONNALISÉES
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
                    entityNames,
                    connection
            );
            System.out.println("\nProject generated successfully! 👨🏽‍💻\n\nSee you on the next project 👋🏼\n");

        } catch (Exception e) {
            System.out.println("\nAn error occurred during project generation:");
            e.printStackTrace();
        }
    }


    private String generateSQL(String description) {
        // Placeholder implementation
        String sql = "-- SQL script generated based on the description: " + description + "\n";
        sql += """
                CREATE TABLE example_table (
                    id INT PRIMARY KEY,
                    name VARCHAR(255)
                );""";
        return sql;
    }



    private void handleScriptExecution(Scanner scanner, Database database, Connection connection) {
        String input = getNonEmptyInput(scanner, "Would you like to execute a script in the database? (y/n)");
        if (input.equalsIgnoreCase("y")) {
            input = getNonEmptyInput(scanner, "Would you like to generate the script? (y/n)");

            if (input.equalsIgnoreCase("y")) {
                handleGeneratedScript(scanner, connection);
            } else {
                handleExistingScript(scanner, connection);
            }
        } else {
            System.out.println("Skipping script execution.");
        }
    }


    private void handleGeneratedScript(Scanner scanner, Connection connection) {
        String description = getNonEmptyInput(scanner, "Enter a description of the tables or modifications to create");
        String scriptContent = GroqApiClient.generateSQL(description);

        // Demander le chemin pour enregistrer le fichier SQL
        System.out.println("Enter the path where the SQL file should be saved");
        String sqlFilePath = FolderSelectorCombo.selectDestinationFolder(scanner);

        // Créer le fichier SQL
        if (!createSQLFile(sqlFilePath, scriptContent)) {
            return; // Si le fichier n'est pas créé, on quitte
        }

        String input = getNonEmptyInput(scanner, "Confirm execution of the generated script? (y/n)");
        if (!input.equalsIgnoreCase("y")) {
            System.out.println("Skipping script execution.");
            return;
        }

        executeSQLScript(connection, scriptContent);
    }


    private void handleExistingScript(Scanner scanner, Connection connection) {
        String scriptPath = getNonEmptyInput(scanner, "Enter the path of the SQL script to execute");

        String scriptContent;
        try {
            scriptContent = FileUtils.getFileContent(scriptPath);
        } catch (FileNotFoundException e) {
            System.out.println("SQL script file not found: " + e.getMessage());
            return;
        }

        String input = getNonEmptyInput(scanner, "Confirm execution of the script? (y/n)");
        if (!input.equalsIgnoreCase("y")) {
            System.out.println("Skipping script execution.");
            return;
        }

        executeSQLScript(connection, scriptContent);
    }


    private boolean createSQLFile(String sqlFilePath, String scriptContent) {
        try {
            String filename = "generated_script_"+ LocalDateTime.now();
            FileUtils.createSimpleFile(sqlFilePath, filename, "sql", scriptContent);
            System.out.println("SQL file created at: " + sqlFilePath + "/"+filename+".sql");
            return true;
        } catch (IOException e) {
            System.out.println("Error creating SQL file: " + e.getMessage());
            return false;
        }
    }


    private void executeSQLScript(Connection connection, String scriptContent) {
        try {
            SQLRunner.execute(connection, scriptContent);
            System.out.println("Script executed successfully.");
        } catch (Exception e) {
            System.out.println("Error executing script: " + e.getMessage());
        }
    }





    private List<String> fetchEntityNames(Database database, Connection connection) {
        List<String> allTableNames = new ArrayList<>();
        try {
            allTableNames = database.getAllTableNames(connection);
            if (allTableNames.isEmpty()) {
                System.out.println("No tables found in the database.");
            } else {
                System.out.println("\nAvailable entities in the database:");
                for (int i = 0; i < allTableNames.size(); i++) {
                    System.out.println((i + 1) + ") " + allTableNames.get(i));
                }
                System.out.println("*  Select all entities");
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while fetching table names: " + e.getMessage());
        }
        return allTableNames;
    }

    /*
    Avant de demander le choix des entités, on demande à l'utilisateur s'il veut exécuter un script dans la base de données,
    si OUI :
           on demande s'il veut générer le script:
                     si oui, on demande une description textuelle des tables ou modifications à créer,
                     (on appelle une fonction generateSQL(String description))
                    si non, on skip
                    on demande le path du fichier SQL
                    et on va créer un fichier SQL à partir de generateSQL (utilise les méthodes dans FileUtils)
                    confirmer (y/n)
           sinon
                on demande juste le path du script à exécuter (pour l'exécution de script utilise SQL Runner)
    sinon on skip et on demande les entités à choisir
     */

    private List<String> handleEntitySelection(Scanner scanner, List<String> allTableNames) {
        if (allTableNames.isEmpty()) {
            return new ArrayList<>();
        }

        while (true) {
            System.out.println("\nEnter the numbers of the entities you want to use, separated by commas (e.g., 1,3,5) or enter * to select all:");
            String input = scanner.nextLine().trim();

            if (input.equals("*")) {
                System.out.println("All entities selected.");
                return new ArrayList<>();
            }

            List<String> selectedEntities = validateEntitySelection(input, allTableNames);

            if (!selectedEntities.isEmpty()) {
                System.out.println("Selected entities: " + String.join(", ", selectedEntities)+"\n\n");
                return selectedEntities;
            }

            System.out.println("\nInvalid selection. Please try again.");
        }
    }


    private List<String> validateEntitySelection(String input, List<String> allTableNames) {
        List<String> selectedEntities = new ArrayList<>();
        String[] selectedIndexes = input.split(",");

        boolean hasInvalidSelection = false;

        for (String index : selectedIndexes) {
            try {
                int idx = Integer.parseInt(index.trim()) - 1;
                if (idx >= 0 && idx < allTableNames.size()) {
                    selectedEntities.add(allTableNames.get(idx));
                } else {
                    System.out.println("Invalid selection: " + index);
                    hasInvalidSelection = true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input: " + index);
                hasInvalidSelection = true;
            }
        }

        return hasInvalidSelection ? new ArrayList<>() : selectedEntities;
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
        credentials.setPort(getDefaultInput(scanner, "Enter the database port", database.getPort()));
        credentials.setDatabaseName(getDefaultInput(scanner, "Enter the database name", "test"));
        credentials.setSchemaName(getDefaultInput(scanner, "Enter the schema name", "public"));
        credentials.setUser(getDefaultInput(scanner, "Enter the database user", "root"));
        credentials.setPwd(getPasswordInput(scanner, "Enter the database password", ""));
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
        String choice = getNonEmptyInput(scanner, "Connection failed. Would you like to modify the JDBC URL? (y/n)");
        if ("y".equals(choice)) {
            String previousJdbcUrl = database.getJdbcUrl(credentials);
            System.out.println("(Previous JDBC URL: \"" + previousJdbcUrl + "\")");

            String customUrl = getNonEmptyInput(scanner, "Enter the custom Connexion  String URL");
            Connection connection = testDatabaseConnection(database, customUrl);
            if (connection != null) {
                System.out.println("Connection successful with custom URL 🎉\n");
            } else {
                System.out.println("Connection failed with custom URL.");
            }
            return connection;
        }
        return null;
    }

    private Connection testDatabaseConnection(Database database) {
        try {
            Connection connection = database.getConnection(credentials);
            return connection.isValid(2) ? connection : null;
        } catch (Exception e) {
            String formattedMessage = String.join("\n", e.getMessage().split("\\."));
            System.out.println("Error testing connection: " + formattedMessage);
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

    private String getPasswordInput(Scanner scanner, String prompt, String defaultValue) {
        Console console = System.console();
        String password;

        if (console != null) {
            char[] passwordChars = console.readPassword(prompt + ": ");
            password = new String(passwordChars).trim();
            if (password.isEmpty()) {
                System.out.println("Using default password.\n");
                password = defaultValue;
            } else {
                System.out.println("Password entered.\n");
            }
        } else {
            System.out.print(prompt + " (default: " + (defaultValue.isEmpty() ? "<empty>" : "<hidden>") + "): ");
            password = scanner.nextLine().trim();
            if (password.isEmpty()) {
                System.out.println("Using default password.\n");
                password = defaultValue;
            } else {
                System.out.println("Password entered.\n");
            }
        }

        return password;
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

        return getSelectionId(scanner, databaseNames, "Database");
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

        return getSelectionId(scanner, languageNames, "Language");
    }

    private String getBaseFrameworkSelection(Scanner scanner, Language language) {
        Map<String, String> baseFrameworks = ProjectGenerator.frameworks.values().stream()
                .filter(framework -> framework.getLanguageId() == language.getId())
                .collect(Collectors.toMap(
                        Framework::getBaseFramework,
                        Framework::getBaseFramework,
                        (existing, replacement) -> existing // To handle duplicates
                ));

        return getSelectionStr(scanner, baseFrameworks, "Framework");
    }

    private String getSelectionStr(Scanner scanner, Map<String, String> options, String optionType) {
        List<String> keys = new ArrayList<>(options.keySet());
        Collections.sort(keys);

        while (true) {
            System.out.println(optionType + " options:");
            for (int i = 0; i < keys.size(); i++) {
                System.out.println((i + 1) + ") " + options.get(keys.get(i)));
            }

            System.out.print("Enter the " + optionType.toLowerCase() + " index: ");
            try {
                int index = Integer.parseInt(scanner.nextLine()) - 1;
                if (index >= 0 && index < keys.size()) {
                    String selectedOption = keys.get(index);
                    System.out.println("Using: " + (index + 1) + "- " + options.get(selectedOption) + "\n");
                    return selectedOption;
                } else {
                    System.out.println("Error: Invalid index. Please select a valid option." + "\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Invalid input. Please enter a valid number.\n");
            }
        }
    }


    private int getFrameworkSelection(Scanner scanner, Language language, String baseFramework) {
        Map<Integer, Framework> frameworks = ProjectGenerator.frameworks;

        Map<Integer, String> validFrameworkNames = frameworks.entrySet().stream()
                .filter(entry -> entry.getValue().getLanguageId() == language.getId() &&
                        entry.getValue().getBaseFramework().equalsIgnoreCase(baseFramework))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()
                ));

        if (validFrameworkNames.isEmpty()) {
            System.out.println("No valid frameworks found for the selected base framework.");
            return -1;
        }
        return getSelectionId(scanner, validFrameworkNames, "Type of project");
    }


    private int getProjectId(Scanner scanner, String baseframework) {
        Map<Integer, Project> projects = ProjectGenerator.projects;
        Map<Integer, String> languageNames = projects.entrySet().stream()
                .filter(entry -> entry.getValue().getBaseFrameworks().contains(baseframework))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getName()
                ));

        if (languageNames.isEmpty()) {
            System.out.println("No valid projects found for the selected framework.\n");
            return -1;
        }

        return getSelectionId(scanner, languageNames, "Build tool");
    }

    private int getSelectionId(Scanner scanner, Map<Integer, String> options, String optionType) {
        List<Integer> keys = new ArrayList<>(options.keySet());
        Collections.sort(keys);

        while (true) {
            System.out.println(optionType+" options :");
            for (int i = 0; i < keys.size(); i++) {
                System.out.println((i + 1) + ") " + options.get(keys.get(i)));
            }

            System.out.print("Enter the " + optionType.toLowerCase() + " index: ");
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