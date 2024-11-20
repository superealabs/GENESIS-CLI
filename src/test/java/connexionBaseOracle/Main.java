package connexionBaseOracle;

import genesis.config.Constantes;
import genesis.config.langage.generator.project.GroqApiClient;
import genesis.connexion.Database;
import utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
        /*try (Connection c = getConnection()) {
            System.out.println("Connected to Oracle Database : " + c);
        }*/
        Database[] databases = FileUtils.fromJson(Database[].class, FileUtils.getFileContent(Constantes.DATABASE_JSON));

        String sql = GroqApiClient.generateSQL(databases[1],"A table for customers in a resto");
        System.out.println(sql);
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        String url = getJdbcUrl();
        Connection connection = DriverManager.getConnection(url, "SYS as SYSDBA", "ComplexP@ssw0rd321");
        connection.setAutoCommit(false);
        return connection;
    }

    protected static String getJdbcUrl() {
        return "jdbc:oracle:thin:@localhost:1521:ORCLCDB";
    }

}
