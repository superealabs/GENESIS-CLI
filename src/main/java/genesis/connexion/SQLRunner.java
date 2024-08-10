package genesis.connexion;


import java.sql.Connection;
import java.sql.PreparedStatement;

public class SQLRunner {
    public static void execute(Connection connex, String query) throws Exception {
        if (connex == null) {
            throw new Exception("Connexion is null");
        }
        try (PreparedStatement statement = connex.prepareStatement(query)) {
            statement.execute();
        }
    }
}

