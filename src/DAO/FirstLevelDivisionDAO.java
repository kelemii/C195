package DAO;


import Model.Appointment;
import Model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static Help.JDBC.connection;

/**
 * The type First level division dao.
 */
public class FirstLevelDivisionDAO {

    /**
     * Gets all divisions.
     *
     * @param id the id
     * @return the all divisions
     * @throws SQLException the sql exception
     */
    public static ObservableList<FirstLevelDivision> getAllDivisions( String id) throws SQLException {
        ObservableList<FirstLevelDivision> divisions = FXCollections.observableArrayList();
        String sql = "SELECT * FROM client_schedule.first_level_divisions WHERE Country_ID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int divisionId = resultSet.getInt("Division_ID");
                String division = resultSet.getString("Division");
                LocalDateTime createDate = resultSet.getTimestamp("Create_Date").toLocalDateTime();
                String createdBy = resultSet.getString("Created_By");
                LocalDateTime lastUpdate = resultSet.getTimestamp("Last_Update").toLocalDateTime();
                String lastUpdatedBy = resultSet.getString("Last_Updated_By");
                int countryId = resultSet.getInt("Country_ID");
                FirstLevelDivision firstLevelDivision = new FirstLevelDivision(divisionId, division, createDate, createdBy, lastUpdate, lastUpdatedBy, countryId);
                divisions.add(firstLevelDivision);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    /**
     * Gets division id by name.
     *
     * @param divisionName the division name
     * @return the division id by name
     * @throws SQLException the sql exception
     */
    public static int getDivisionIdByName(String divisionName) throws SQLException {
        String sql = "SELECT Division_ID FROM client_schedule.first_level_divisions WHERE Division = ?";
        int divisionId = -1;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, divisionName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                divisionId = resultSet.getInt("Division_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisionId;
    }
}
