package genesis.connexion;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLRunner {
    public static void execute(Connection connex, String query) throws Exception {
        if (connex == null) {
            throw new Exception("\nConnection is null\n");
        }
        String[] queries = query.split(";");
        try (Statement statement = connex.createStatement()) {
            for (String singleQuery : queries) {
                if (!singleQuery.trim().isEmpty()) {
                    statement.addBatch(singleQuery.trim());
                }
            }
            statement.executeBatch();
            connex.commit();
        } catch (SQLException e) {
            connex.rollback();

            String[] errorMessages = e.getMessage().split("\\.");
            String formattedMessage = String.join(".\n", errorMessages);

            throw new Exception("Batch execution failed: " + formattedMessage, e);
        }
    }
}


