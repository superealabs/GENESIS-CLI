package genesis.model;

import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.FileUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Setter
@Getter
@NoArgsConstructor
public class TableMetadata {
    private String tableName;
    private ColumnMetadata[] columns;
    private String className;
    private FieldMetadata[] fields;
    private FieldMetadata primaryField;

    public void initialize(Connection connex, Credentials credentials, Database database, Language language) throws SQLException, ClassNotFoundException {
        boolean opened = false;
        Connection connect = connex;

        if (connect == null || connect.isClosed()) {
            connect = database.getConnection(credentials);
            opened = true;
        }

        try {
            DatabaseMetaData metaData = connect.getMetaData();
            String tableName = getTableName();

            List<ColumnMetadata> listeCols = fetchColumns(metaData, tableName, language, database);
            List<FieldMetadata> listeFields = fetchPrimaryKeys(metaData, tableName, listeCols);
            fetchForeignKeys(metaData, tableName, listeFields);

            setClassName(FileUtils.majStart(FileUtils.toCamelCase(tableName)));
            setColumns(listeCols.toArray(new ColumnMetadata[0]));
            setFields(listeFields.toArray(new FieldMetadata[0]));

        } finally {
            if (opened && !connect.isClosed()) {
                connect.close();
            }
        }
    }

    public List<String> getAllTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<>();
        DatabaseMetaData metaData = connection.getMetaData();

        try (ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"})) {
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                tableNames.add(tableName);
            }
        }

        return tableNames;
    }

    public List<TableMetadata> initializeTables(List<String> tableNames, Connection connex, Credentials credentials, Database database, Language language) throws SQLException, ClassNotFoundException {
        List<TableMetadata> tableMetadataList = new ArrayList<>();
        boolean opened = false;
        Connection connect = connex;

        if (connect == null || connect.isClosed()) {
            connect = database.getConnection(credentials);
            opened = true;
        }

        try {
            if (tableNames == null || tableNames.isEmpty()) {
                tableNames = getAllTableNames(connect);
            }

            for (String tableName : tableNames) {
                TableMetadata tableMetadata = new TableMetadata();
                tableMetadata.setTableName(tableName);
                tableMetadata.initialize(connect, credentials, database, language);
                tableMetadataList.add(tableMetadata);
            }
        } finally {
            if (opened && !connect.isClosed()) {
                connect.close();
            }
        }

        return tableMetadataList;
    }



    private List<ColumnMetadata> fetchColumns(DatabaseMetaData metaData, String tableName, Language language, Database database) throws SQLException {
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        List<ColumnMetadata> listeCols = new ArrayList<>();

        while (columns.next()) {
            ColumnMetadata column = new ColumnMetadata();
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");

            column.setName(columnName);
            column.setType(language.getTypes().get(database.getTypes().get(columnType)));
            listeCols.add(column);
        }

        return listeCols;
    }

    private List<FieldMetadata> fetchPrimaryKeys(DatabaseMetaData metaData, String tableName, List<ColumnMetadata> columns) throws SQLException {
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
        List<FieldMetadata> listeFields = new ArrayList<>();

        while (primaryKeys.next()) {
            String pkColumnName = primaryKeys.getString("COLUMN_NAME");

            for (ColumnMetadata column : columns) {
                if (column.getName().equalsIgnoreCase(pkColumnName)) {
                    column.setPrimary(true);
                    FieldMetadata field = new FieldMetadata();
                    field.setName(FileUtils.toCamelCase(column.getName()));
                    field.setPrimary(true);
                    listeFields.add(field);
                    setPrimaryField(field);
                }
            }
        }

        return listeFields;
    }

    private void fetchForeignKeys(DatabaseMetaData metaData, String tableName, List<FieldMetadata> fields) throws SQLException {
        ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);

        while (foreignKeys.next()) {
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");

            for (FieldMetadata field : fields) {
                if (field.getName().equalsIgnoreCase(FileUtils.toCamelCase(fkColumnName))) {
                    field.setForeign(true);
                    field.setReferencedField(FileUtils.toCamelCase(pkColumnName));
                    field.setType(FileUtils.majStart(FileUtils.toCamelCase(pkTableName)));
                }
            }
        }

    }

    private void printColumnsInfo(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet columns = metaData.getColumns(null, null, tableName, null);
        System.out.println("columns:");

        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            int columnType = columns.getInt("DATA_TYPE");
            String columnTypeName = columns.getString("TYPE_NAME");
            int columnSize = columns.getInt("COLUMN_SIZE");
            boolean nullable = columns.getBoolean("NULLABLE");

            String dataTypeName = JDBCType.valueOf(columnType).getName();
            System.out.println("\t" + columnName + " (" + dataTypeName + "), Size: " + columnSize + ", Nullable: " + nullable + "Columname type: "+columnTypeName);
        }
    }

    private void printPrimaryKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);
        System.out.println("Primary Keys:");
        while (primaryKeys.next()) {
            String pkColumnName = primaryKeys.getString("COLUMN_NAME");
            System.out.println("\t" + pkColumnName);
        }
    }

    private void printForeignKeys(DatabaseMetaData metaData, String tableName) throws SQLException {
        ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);
        System.out.println("Foreign Keys:");
        while (foreignKeys.next()) {
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String fkName = foreignKeys.getString("FK_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");
            System.out.println("\t" + fkColumnName + " -> " + pkTableName + "." + pkColumnName + " (" + fkName + ")");
        }
    }

    public void getMetaData(Credentials credentials, Database database) {
        try (Connection connection = database.getConnection(credentials)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Obtenir toutes les tables de la base de données
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table Name: " + tableName);

                // Appel des méthodes pour afficher les informations de la table
                printColumnsInfo(metaData, tableName);
                printPrimaryKeys(metaData, tableName);
                printForeignKeys(metaData, tableName);

                System.out.println(); // Séparation entre les tables
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error while accessing database metadata: ", e);
        }
    }

}
