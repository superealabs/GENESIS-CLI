package modelGeneration;

import genesis.config.Constantes;
import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import genesis.model.ColumnMetadata;
import genesis.model.TableMetadata;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class TableMetadataTest {
    private Connection connection;
    private Credentials credentials;
    private Database database;
    private Language language;
    Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));
    Language[] languages = FileUtils.fromJson(Language[].class, FileUtils.getFileContent(Constantes.LANGUAGE_JSON));

    public TableMetadataTest(Connection connection) throws FileNotFoundException {
        this.connection = connection;
    }

    @BeforeEach
    public void setUp() {
        credentials = new Credentials()
                .setHost("localhost")
                .setDatabaseName("test_db")
                .setUser("SA")
                .setPwd("ComplexP@ssw0rd321")
                .setTrustCertificate(true)
                .setUseSSL(true);
        database = databases[1];    // PostgreSQL
        language = languages[0];    // Java

    }

    @Test
    public void testInitialize() throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        tableMetadata.setTableName("employe");

        tableMetadata.initialize(connection, credentials, database, language);

        System.out.println("TableName: " + tableMetadata.getTableName());
        System.out.println("ClassName: " + tableMetadata.getClassName());
        for (ColumnMetadata column : tableMetadata.getColumns()) {
            System.out.println("Column: " + column.getName() + ", Type: " + column.getType());
        }
        for (ColumnMetadata field : tableMetadata.getColumns()) {
            System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
        }
        if (tableMetadata.getPrimaryColumn() != null) {
            System.out.println("PrimaryField: " + tableMetadata.getPrimaryColumn().getName());
        }
    }

    @Test
    public void testGetAllTableNames() throws SQLException {
        TableMetadata tableMetadata = new TableMetadata();

        List<String> tableNames = tableMetadata.getAllTableNames(database, connection);

        System.out.println("Tables:");
        for (String tableName : tableNames) {
            System.out.println(tableName);
        }
    }

    @Test
    public void testInitializeTables() throws SQLException, ClassNotFoundException {
        TableMetadata tableMetadata = new TableMetadata();
        List<TableMetadata> metadataList = tableMetadata.initializeTables(null, connection, credentials, database, language);

        for (TableMetadata metadata : metadataList) {
            System.out.println("TableName: " + metadata.getTableName());
            System.out.println("ClassName: " + metadata.getClassName());
            for (ColumnMetadata column : metadata.getColumns()) {
                System.out.println("Column: " + column.getName() + ", Type: " + column.getType());
            }
            for (ColumnMetadata field : tableMetadata.getColumns()) {
                System.out.println("Field: " + field.getName() + ", Type: " + field.getType());
            }
            if (tableMetadata.getPrimaryColumn() != null) {
                System.out.println("PrimaryField: " + tableMetadata.getPrimaryColumn().getName());
            }
            System.out.println();
        }
    }
}
