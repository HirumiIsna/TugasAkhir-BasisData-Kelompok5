

import java.sql.*;


public class DBConfig {
    private static final String SERVER = "localhost";
    private static final String PORT = "1433";
    private static final String DATABASE = "matahari100";

    private static final String URL =
                                "jdbc:sqlserver://" + SERVER + ":" + PORT +
                                ";databaseName=" + DATABASE +
                                ";integratedSecurity=true" +
                                ";encrypt=true" +
                                ";trustServerCertificate=true";

    public static Connection getConnection() {
        try {
    
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
    
            return DriverManager.getConnection(URL);
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
        return null;
    }
}
