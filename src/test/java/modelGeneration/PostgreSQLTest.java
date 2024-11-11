package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.framework.APIGenerator;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.model.TableMetadata;
import genesis.connexion.providers.PostgreSQLDatabase;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class PostgreSQLTest {

    Credentials credentials;

    public PostgreSQLTest() {
        this.credentials = new Credentials()
                .setHost("localhost")
                .setPort("5432")
                .setDatabaseName("test_db")
                .setSchemaName("public")
                .setUser("nomena")
                .setPwd("root");
    }

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void PostgreSQLxJavaSpringMVC() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        PostgreSQLDatabase database = (PostgreSQLDatabase) databases[1];    // PostgreSQL
        Language language = languages[0];                                   // Java
        Framework framework = frameworks[0];                                // Spring MVC

        String projectName = "TestProject", groupLink = "com", destinationFolder = "/Users/nomena/STAGE/GENESIS";

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            GenesisGenerator mvcGenerator = new APIGenerator();

            for (TableMetadata tableMetadata : entities) {
                mvcGenerator.generateModel(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateDao(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateService(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateController(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
            }

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

        String projectName = "TestProject", groupLink = "com", destinationFolder = "/Users/nomena/STAGE/GENESIS";

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, language).toArray(new TableMetadata[0]);
            GenesisGenerator mvcGenerator = new APIGenerator();

            String model = "";
            for (TableMetadata tableMetadata : entities) {
                model = mvcGenerator.generateModel(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateDao(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateService(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
                mvcGenerator.generateController(framework, language, tableMetadata, destinationFolder, projectName, groupLink);
            }

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
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
