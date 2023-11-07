package DAO;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static DBConnection.JDBC.connection;

public class UserDAO {
    private static final String LOGIN_QUERY = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";

    public int checkLogin(String username, String password) {
        int userID = -1;

        // Use the connection from the pool
        try {
             PreparedStatement preparedStatement = connection.prepareStatement(LOGIN_QUERY);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userID = resultSet.getInt("User_ID");
                }
            }
        } catch (SQLException e) {
            // Properly handle exception
            e.printStackTrace();
        }

        return userID;
    }
}
