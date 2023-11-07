package DAO;

import DBConnection.JDBC;
import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static DBConnection.JDBC.connection;

public class AppointmentDAO {
    public static ObservableList<Appointment> getAllAppointments() throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointments";

        try {
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery();

                    while (resultSet.next()) {
                        int appointmentId = resultSet.getInt("Appointment_ID");
                        String title = resultSet.getString("Title");
                        String description = resultSet.getString("Description");
                        String location = resultSet.getString("Location");
                        String type = resultSet.getString("Type");
                        LocalDateTime start = resultSet.getTimestamp("Start").toLocalDateTime();
                        LocalDateTime end = resultSet.getTimestamp("End").toLocalDateTime();
                        LocalDateTime createDate = resultSet.getTimestamp("Create_Date").toLocalDateTime();
                        String createdBy = resultSet.getString("Created_By");
                        LocalDateTime lastUpdate = resultSet.getTimestamp("Last_Update").toLocalDateTime();
                        String lastUpdatedBy = resultSet.getString("Last_Updated_By");
                        int customerId = resultSet.getInt("Customer_ID");
                        int userId = resultSet.getInt("User_ID");
                        int contactId = resultSet.getInt("Contact_ID");

                        Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                        appointmentsList.add(appointment);
                    }

        } catch (SQLException e) {
            // Properly handle exception
            e.printStackTrace();
        }

        return appointmentsList;
    }

    /**
     * Deletes an appointment based on the appointment ID.
     *
     * @param appointmentId The ID of the appointment to delete
     * @param connection    The database connection object
     * @return The number of rows affected
     * @throws SQLException If a database access error occurs, this method is called on a closed PreparedStatement, or the given SQL statement produces anything other than a single ResultSet object
     */
    public static int deleteAppointment(int appointmentId, Connection connection) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, appointmentId);
            return preparedStatement.executeUpdate();
        }
    }
}
