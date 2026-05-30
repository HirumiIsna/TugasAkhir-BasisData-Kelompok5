package src.backend.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=matahari4;encrypt=true;trustServerCertificate=true";
    private static final String USERNAME = "testhdr";
    private static final String PASSWORD = "pass123";

    private static Connection connection = null;

    public static Connection getConnection() {
        System.out.println("Get Connection!");
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Koneksi database berhasil!");
            } catch (SQLException e) {
                System.out.println("Koneksi database gagal: " + e.getMessage());
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}