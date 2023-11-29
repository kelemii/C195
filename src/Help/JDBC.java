package Help;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

/**
 * The type Jdbc.
 */
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?serverTimezone=UTC";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    private static final String userName = "sqlUser";
    private static String password = "Passw0rd!";
    /**
     * The constant connection.
     */
    public static Connection connection;

    /**
     * Open connection.
     */
    public static void openConnection() {
        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(jdbcUrl, userName, password);
            System.out.println("Connection successful!");

            // Set the time zone to UTC after opening the connection
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("SET time_zone = '+00:00';");
            }
        } catch (Exception e) {
            System.out.println("Error:" + e.getMessage());
        }
    }


    /**
     * Gets connection.
     *
     * @return the connection
     */
    public static Connection getConnection() {

        return connection;
    }

    /**
     * Close connection.
     */
    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e)
        {
            System.out.println("Error:" + e.getMessage());
        }
    }
}
