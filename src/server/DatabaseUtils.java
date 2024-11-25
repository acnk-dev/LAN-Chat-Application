package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseUtils {

    public static ResultSet executeQuery(String query, Object... params) {
        try {
            PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int executeUpdate(String query, Object... params) {
        System.out.println("[DatabaseUtils] executeUpdate: " + query);
        try {
            Connection connection = DatabaseConnection.getConnection();
            if (connection == null) {
                System.out.println("[DatabaseUtils] no connection available - aborting update");
                return -1;
            }
            PreparedStatement ps = connection.prepareStatement(query);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            int rowsAffected = ps.executeUpdate();
            System.out.println("[DatabaseUtils] rows affected: " + rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            System.out.println("[DatabaseUtils] executeUpdate FAILED: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
}
