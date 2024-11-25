package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            String connectionString = "jdbc:mysql://localhost:4008/LANChatApplication";

            System.out.println("[DatabaseConnection] connecting to " + connectionString);
            try {
                connection = DriverManager.getConnection(connectionString, "root", "abcd1234");
                System.out.println("[DatabaseConnection] connected OK - catalog=" + connection.getCatalog()
                        + " url=" + connection.getMetaData().getURL());
            } catch (SQLException ex) {
                System.out.println("[DatabaseConnection] FAILED to connect: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        return connection;
    }
}
