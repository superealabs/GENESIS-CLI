package connexionBaseOracle;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        try (Connection c = getConnection()) {
            System.out.println("Connected to Oracle Database : " + c);
        }
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
