package src;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class configLoginSql {
    static String url = "jdbc:sqlserver://localhost:1433;databaseName=matahari100;encrypt=true;trustServerCertificate=true";
    static String userName = "sa";
    static String password = "hilmi212";

    public static Connection setConnection(){
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            return DriverManager.getConnection(url, userName, password);
        } catch (Exception e) {
        }return null;

    }
}
