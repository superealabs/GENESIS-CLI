package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.framework.GenesisGenerator;
import genesis.config.langage.generator.framework.MVCGenerator;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.connexion.providers.OracleDatabase;
import genesis.model.TableMetadata;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;

public class OracleTest {
    Credentials credentials;

    public OracleTest() {
        this.credentials = new Credentials();
        credentials
                .setHost("localhost")
                .setDatabaseName("ORCLCDB")
                .setUser("c##test_db")
                .setPwd("test_db")
                .setTrustCertificate(true)
                .setUseSSL(true);
    }

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void OraclexJavaSpringMVC() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        OracleDatabase database = (OracleDatabase) databases[3];            // Oracle
        Language language = languages[0];                                   // Java
        Framework framework = frameworks[0];                                // Spring MVC

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata tableMetadata = new TableMetadata();
            tableMetadata.setTableName("EMPLOYEE_T");
            tableMetadata.initialize(connection, credentials, database, language);

            GenesisGenerator mvcGenerator = new MVCGenerator();
            String projectName = "TestProject", groupLink = "com";

            String model = mvcGenerator.generateModel(framework, language, tableMetadata, projectName, groupLink);
            String dao = mvcGenerator.generateDao(framework, language, tableMetadata, projectName, groupLink);

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n"+model);
            System.out.println("\n====== GENERATED ======\n"+dao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void OraclexNET() throws IOException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromYaml(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_YAML));

        OracleDatabase database = (OracleDatabase) databases[3];    // Oracle
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

            System.out.println("\n====== GENERATED ======\n"+model);
            System.out.println("\n====== GENERATED ======\n"+dao);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    void metaDataTEST() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));

        OracleDatabase database = (OracleDatabase) databases[3];    // Oracle

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
            System.out.println("Database Product Name: " + databaseProductName+"\n\n");

/*
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
*/
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
