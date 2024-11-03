package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Editor;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.config.langage.generator.framework.MVCGenerator;
import genesis.config.langage.generator.project.ProjectGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.PostgreSQLDatabase;
import genesis.model.TableMetadata;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class PostgreSQLTest {

    Credentials credentials;

    public PostgreSQLTest() {
        this.credentials = new Credentials();
        credentials
                .setHost("localhost")
                .setDatabaseName("post_db")
                .setUser("postgres")
                .setPwd("nikami")
//                .setUser("nomena")
//                .setPwd("root")
                .setPort("5432")
                .setTrustCertificate(true)
                .setUseSSL(true)
                .setAllowPublicKeyRetrieval(true);
    }

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void PostgreSQLxJavaSpringMVC() throws IOException {
        Editor[] editors = FileUtils.fromYaml(Editor[].class, FileUtils.getFileContent(Constantes.EDITOR_YAML));
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));


        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL
        Language language = languages[0];                                   // Java
        Framework framework = frameworks[0];                                // Spring MVC
        Editor editor = editors[0];

        String projectName = "TestProject", groupLink = "com";

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            GenesisGenerator mvcGenerator = new MVCGenerator();

            for (TableMetadata tableMetadata : entities) {
//                mvcGenerator.generateModel(framework, language, tableMetadata, projectName, groupLink);
//                mvcGenerator.generateDao(framework, language, tableMetadata, projectName, groupLink);
//                mvcGenerator.generateService(framework, language, tableMetadata, projectName, groupLink);
//                mvcGenerator.generateController(framework, language, tableMetadata, projectName, groupLink);

                String layout = mvcGenerator.generateView(framework, language, editor, tableMetadata, "Test", "labs");
                System.out.println("\n====== GENERATED ======\n" + layout);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateProject() {
        try {
            // Déclaration et initialisation des variables
            int databaseId = 1;
            int languageId = 0;
            int frameworkId = 0;
            int projectId = 0;
            int editorId = 0;
            String projectName = "Begin";
            String groupLink = "com.labs";
            String projectPort = "8000";
            String logLevel = "INFO";
            String hibernateDdlAuto = "update";
            String frameworkVersion = "3.0.1";
            String projectDescription = "A Spring Boot BEGIN Project";
            String languageVersion = "21";

            // Création de l'instance du générateur de projet
            ProjectGenerator projectGenerator = new ProjectGenerator();

            // Appel de la méthode generateProject avec les arguments
            projectGenerator.generateProject(
                    databaseId,
                    languageId,
                    frameworkId,
                    projectId,
                    editorId,
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

    @Test
    void PostgreSQLxNET() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL
        Language language = languages[1];                                   // C#
        Framework framework = frameworks[1];                                // .NET

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            TableMetadata tableMetadata = entities[1]; //Employe

            GenesisGenerator mvcGenerator = new MVCGenerator();
            String projectName = "TestProject", groupLink = "com";

            String model = mvcGenerator.generateModel(framework, language, tableMetadata, projectName, groupLink);
            String dao = mvcGenerator.generateDao(framework, language, tableMetadata, projectName, groupLink);

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
            System.out.println("\n====== GENERATED ======\n" + dao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void metaDataTEST() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL

        try (Connection connection = database.getConnection(credentials)) {
            DatabaseMetaData metaData = connection.getMetaData();

            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();
            String majorVersion = String.valueOf(metaData.getDatabaseMajorVersion());
            String minorVersion = String.valueOf(metaData.getDatabaseMinorVersion());
            String databaseProductName = metaData.getDatabaseProductName();

            System.out.println("\n\nDriver Name: " + driverName);
            System.out.println("Driver Version: " + driverVersion);
            System.out.println("Major Version: " + majorVersion);
            System.out.println("Minor Version: " + minorVersion);
            System.out.println("Database Product Name: " + databaseProductName + "\n\n");

            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table Name: " + tableName);

                // Obtenir les informations sur les colonnes
                ResultSet columns = metaData.getColumns(null, null, tableName, null);
                System.out.println("Columns:");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    int dataType = columns.getInt("DATA_TYPE"); // Obtenir le type de données sous forme d'entier
                    String typeName = columns.getString("TYPE_NAME"); // Obtenir le nom du type de données
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    boolean nullable = columns.getBoolean("NULLABLE");

                    // Convertir le type de données entier en nom de type pour une meilleure lisibilité
                    String dataTypeName = JDBCType.valueOf(dataType).getName(); // Utilisation de JdbcType pour convertir le type de données entier en nom

                    System.out.println("\t" + columnName + " (" + dataTypeName + "), TypeName: " + typeName + ", Size: " + columnSize + ", Nullable: " + nullable);
                }


                // Obtenir les contraintes de clé primaire
                ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
                System.out.println("Primary Keys:");
                while (primaryKeys.next()) {
                    String pkColumnName = primaryKeys.getString("COLUMN_NAME");
                    System.out.println("\t" + pkColumnName);
                }

                // Obtenir les contraintes de clé étrangère
                ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
                System.out.println("Foreign Keys:");
                while (foreignKeys.next()) {
                    String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
                    String fkName = foreignKeys.getString("FK_NAME");
                    String pkTableName = foreignKeys.getString("PKTABLE_NAME");
                    String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
                    System.out.println("\t" + fkColumnName + " -> " + pkTableName + "." + pkColumnName + " (" + fkName + ")");
                }

                System.out.println(); // Séparation entre les tables
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


}
