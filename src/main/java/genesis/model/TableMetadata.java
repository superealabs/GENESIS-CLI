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

import static utils.FileUtils.*;


@Setter
@Getter
@NoArgsConstructor
public class TableMetadata {
    private Database database;
    private String tableName;
    private ColumnMetadata[] columns;
    private ColumnMetadata primaryColumn;
    private String className;

    public void initialize(Connection connex, Credentials credentials, Database database, Language language) throws SQLException, ClassNotFoundException {
        boolean opened = false;
        Connection connect = connex;

        if (connect == null || connect.isClosed()) {
            connect = database.getConnection(credentials);
            opened = true;
        }

        try {
            DatabaseMetaData metaData = connect.getMetaData();

            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();

            database.setDriverName(driverName);
            database.setDriverVersion(driverVersion);

            setDatabase(database);

            String tableName = getTableName();

            List<ColumnMetadata> listeCols = fetchColumns(metaData, tableName, language, database);
            fetchPrimaryKeys(metaData, tableName, listeCols);
            fetchForeignKeys(metaData, tableName, listeCols);

            setClassName(FileUtils.majStart(toCamelCase(tableName.toLowerCase())));
            setColumns(listeCols.toArray(new ColumnMetadata[0]));

        } finally {
            if (opened && !connect.isClosed()) {
                connect.close();
            }
        }
    }

    public List<String> getAllTableNames(Database database, Connection connection) throws SQLException {
        return database.getAllTableNames(connection);
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
                tableNames = getAllTableNames(database, connect);
            }

            for (String tableName : tableNames) {
                TableMetadata tableMetadata = new TableMetadata();
                tableMetadata.setTableName(tableName.toLowerCase());
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

            column.setName(toCamelCase(columnName.toLowerCase()));
            column.setReferencedColumn(columnName);
            column.setType(language.getTypes().get(database.getTypes().get(columnType)));
            column.setColumnType(columnType);
            listeCols.add(column);
        }

        return listeCols;
    }

    private void fetchPrimaryKeys(DatabaseMetaData metaData, String tableName, List<ColumnMetadata> columns) throws SQLException {
        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, tableName);

        while (primaryKeys.next()) {
            String pkColumnName = primaryKeys.getString("COLUMN_NAME");

            for (ColumnMetadata column : columns) {
                if (column.getReferencedColumn().equalsIgnoreCase(pkColumnName)) {
                    column.setPrimary(true);
                    ColumnMetadata pkfield = new ColumnMetadata();

                    pkfield.setName(toCamelCase(column.getName()));
                    pkfield.setType(column.getType());
                    pkfield.setColumnType(column.getColumnType());
                    pkfield.setPrimary(true);
                    pkfield.setReferencedColumn(column.getReferencedColumn());
                    setPrimaryColumn(pkfield);
                    break;
                }
            }
        }

    }

    private void fetchForeignKeys(DatabaseMetaData metaData, String tableName, List<ColumnMetadata> listeCols) throws SQLException {
        ResultSet foreignKeys = metaData.getImportedKeys(null, null, tableName);

        while (foreignKeys.next()) {
            String fkColumnName = foreignKeys.getString("FKCOLUMN_NAME");
            String pkTableName = foreignKeys.getString("PKTABLE_NAME");
            //String pkColumnName = foreignKeys.getString("PKCOLUMN_NAME");

            for (ColumnMetadata field : listeCols) {
                if (field.getReferencedColumn().equalsIgnoreCase(fkColumnName)) {
                    field.setName(toCamelCase(pkTableName.toLowerCase()));
                    field.setForeign(true);
                    field.setReferencedTable(toCamelCase(pkTableName));
                    field.setReferencedColumn(field.getReferencedColumn());
                    field.setColumnType(toCamelCase(field.getType()));
                    field.setType(FileUtils.majStart(toCamelCase(pkTableName)));
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
            System.out.println("\t" + columnName + " (" + dataTypeName + "), Size: " + columnSize + ", Nullable: " + nullable + "Columname type: " + columnTypeName);
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

            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("Table Name: " + tableName);

                printColumnsInfo(metaData, tableName);
                printPrimaryKeys(metaData, tableName);
                printForeignKeys(metaData, tableName);

                System.out.println();
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException("Error while accessing database metadata: ", e);
        }
    }

}
