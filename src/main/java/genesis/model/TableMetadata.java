package genesis.model;

import genesis.config.langage.Language;
import genesis.connexion.Credentials;
import genesis.connexion.Database;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.FileUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

    public TableMetadata(Connection connex, Credentials credentials, Database database, Language language) throws SQLException, ClassNotFoundException {
        initialize(connex, credentials, database, language);
    }

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

}
