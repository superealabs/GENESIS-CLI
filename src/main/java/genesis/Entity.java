package genesis;

import lombok.Getter;
import lombok.Setter;
import utils.FileUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


@Setter
@Getter
public class Entity {
    private String tableName;
    private EntityColumn[] columns;
    private String className;
    private EntityField[] fields;
    private EntityField primaryField;

    public void initialize(Connection connex, Credentials credentials, Database database, Language language) throws ClassNotFoundException, SQLException {
        boolean opened = false;
        Connection connect = connex;
        if (connect == null) {
            connect = database.getConnexion(credentials);
            opened = true;
        }
        String query = database.getGetcolumnsQuery().replace("[tableName]", getTableName());
        PreparedStatement statement = connect.prepareStatement(query);
        try {
            Vector<EntityColumn> listeCols = new Vector<>();
            Vector<EntityField> listeFields = new Vector<>();
            EntityColumn column;
            EntityField field;
            try (ResultSet result = statement.executeQuery()) {
                setClassName(FileUtils.majStart(FileUtils.toCamelCase(getTableName())));
                while (result.next()) {
                    column = new EntityColumn();
                    column.setName(result.getString("column_name"));
                    column.setType(result.getString("data_type"));
                    column.setPrimary(result.getBoolean("is_primary"));
                    column.setForeign(result.getBoolean("is_foreign"));
                    column.setReferencedTable(result.getString("foreign_table_name"));
                    column.setReferencedColumn(result.getString("foreign_column_name"));
                    field = new EntityField();
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
                EntityColumn[] cols = new EntityColumn[listeCols.size()];
                for (int i = 0; i < cols.length; i++) {
                    cols[i] = listeCols.get(i);
                }
                EntityField[] fiels = new EntityField[listeFields.size()];
                for (int i = 0; i < fiels.length; i++) {
                    fiels[i] = listeFields.get(i);
                }
                setColumns(cols);
                setFields(fiels);
            }
        } finally {
            statement.close();
            if (opened) {
                connect.close();
            }
        }
    }
}
