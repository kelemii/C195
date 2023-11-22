package DAO;

import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

import static Help.JDBC.connection;

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

//    public ArrayList<Integer> getUserIDs() throws SQLException {
//        return connection.prepareStatement("Select USER_ID FROM USERS");
//    }
    public ObservableList<Integer> getUserIDs() throws SQLException {
        ObservableList<Integer> userIds = FXCollections.observableArrayList();

        String sql = "SELECT USER_ID FROM client_schedule.users";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int userId = resultSet.getInt("USER_ID");
                userIds.add(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.reverse(userIds);
        return userIds;
    }
}
