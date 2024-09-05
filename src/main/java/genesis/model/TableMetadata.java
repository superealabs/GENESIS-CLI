package genesis.model;

import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.FileUtils;

import java.sql.*;
import java.util.Vector;


@Setter
@Getter
@NoArgsConstructor
public class TableMetadata {
    private String tableName;
    private ColumnMetadata[] columns;
    private String className;
    private FieldMetadata[] fields;
    private FieldMetadata primaryField;

    public void initialize(Connection connex, Credentials credentials, Database database, Language language) throws ClassNotFoundException, SQLException {
        boolean opened = false;
        Connection connect = connex;

        // Vérifier si la connexion est nulle ou fermée
        if (connect == null || connect.isClosed()) {
            connect = database.getConnection(credentials);
            opened = true;
        }

        String query = database.getGetColumnsQuery().replace("[tableName]", getTableName());

        // Utilisation du try-with-resources pour assurer la fermeture de PreparedStatement et ResultSet
        try (PreparedStatement statement = connect.prepareStatement(query);
             ResultSet result = statement.executeQuery()) {

            Vector<ColumnMetadata> listeCols = new Vector<>();
            Vector<FieldMetadata> listeFields = new Vector<>();
            setClassName(FileUtils.majStart(FileUtils.toCamelCase(getTableName())));

            while (result.next()) {
                ColumnMetadata column = new ColumnMetadata();
                column.setName(result.getString("column_name"));
                column.setType(result.getString("data_type"));
                column.setPrimary(result.getBoolean("is_primary"));
                column.setForeign(result.getBoolean("is_foreign"));
                column.setReferencedTable(result.getString("foreign_table_name"));
                column.setReferencedColumn(result.getString("foreign_column_name"));

                FieldMetadata field = new FieldMetadata();
                if (column.isForeign()) {
                    field.setName(FileUtils.minStart(FileUtils.toCamelCase(column.getReferencedTable())));
                    field.setType(FileUtils.majStart(FileUtils.toCamelCase(column.getReferencedTable())));
                    field.setReferencedField(FileUtils.toCamelCase(column.getReferencedColumn()));
                } else {
                    field.setName(FileUtils.toCamelCase(column.getName()));
                    field.setType(language.getTypes().get(database.getTypes().get(column.getType())));
                }

                field.setPrimary(column.isPrimary());
                field.setForeign(column.isForeign());

                if (field.isPrimary()) {
                    setPrimaryField(field);
                }

                listeCols.add(column);
                listeFields.add(field);
            }

            setColumns(listeCols.toArray(new ColumnMetadata[0]));
            setFields(listeFields.toArray(new FieldMetadata[0]));

        } finally {
            if (opened && !connect.isClosed()) {
                connect.close();
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
            System.out.println("\t" + columnName + " (" + dataTypeName + "), Size: " + columnSize + ", Nullable: " + nullable);
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
