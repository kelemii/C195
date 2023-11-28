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

/**
 * The type User dao.
 */
public class UserDAO {
    private static final String LOGIN_QUERY = "SELECT * FROM users WHERE User_Name = ? AND Password = ?";

    /**
     * Check login int.
     *
     * @param username the username
     * @param password the password
     * @return the int
     */
    public int checkLogin(String username, String password) {
        int userID = -1;

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
            e.printStackTrace();
        }

        return userID;
    }

    /**
     * Gets user i ds.
     *
     * @return the user i ds
     * @throws SQLException the sql exception
     */

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
