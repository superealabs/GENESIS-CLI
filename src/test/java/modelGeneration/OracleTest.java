package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Framework;
import genesis.config.langage.Language;
import genesis.config.langage.generator.GenesisGenerator;
import genesis.config.langage.generator.MVCGenerator;
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

    Credentials credentials = new Credentials("test_db", "c##test_db", "test_db", "localhost", true, true);

    @Test
    void test() {
        System.out.println("Hey !");
    }

    @Test
    void OraclexJavaSpringMVC() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));

        OracleDatabase database = (OracleDatabase) databases[3];    // Oracle
        Language language = languages[0];                                   // Java
        Framework framework = frameworks[0];                                // Spring MVC

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, "employe");
            TableMetadata tableMetadata = entities[0];
            tableMetadata.initialize(connection, credentials, database, language);


            GenesisGenerator mvcGenerator = new MVCGenerator();
            String model = mvcGenerator.generateModel(framework, language, tableMetadata, "Test");

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void OraclexNET() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
        Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));
        Framework[] frameworks = FileUtils.fromJson(Framework[].class, FileUtils.getFileContent(Constantes.FRAMEWORK_MVC_JSON));

        OracleDatabase database = (OracleDatabase) databases[3];    // Oracle
        Language language = languages[1];                                   // C#
        Framework framework = frameworks[1];                                // .NET

        try (Connection connection = database.getConnection(credentials)) {
            TableMetadata[] entities = database.getEntities(connection, credentials, "employe");
            TableMetadata tableMetadata = entities[0];
            tableMetadata.initialize(connection, credentials, database, language);
            ;

            GenesisGenerator mvcGenerator = new MVCGenerator();
            String model = mvcGenerator.generateModel(framework, language, tableMetadata, "Test");

            System.out.println(database);
            System.out.println(language);
            System.out.println(framework);

            System.out.println("\n====== GENERATED ======\n" + model);
        } catch (SQLException | ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    void metaDataTEST() throws FileNotFoundException {
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));

        OracleDatabase database = (OracleDatabase) databases[3];    // Oracle

        try (Connection connection = database.getConnection(credentials)) {
            DatabaseMetaData metaData = connection.getMetaData();

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

                    System.out.println("\t" + columnName + " (" + dataTypeName + "), Size: " + columnSize + ", Nullable: " + nullable);
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