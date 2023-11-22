package DAO;

import Model.Appointment;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import static Help.JDBC.connection;

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
    public static void saveAppointment(Appointment appointment) throws SQLException {
        String insertSql = "INSERT INTO appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, appointment.getTitle());
            preparedStatement.setString(2, appointment.getDescription());
            preparedStatement.setString(3, appointment.getLocation());
            preparedStatement.setString(4, appointment.getType());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(appointment.getStart()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(appointment.getEnd()));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(appointment.getCreateDate()));
            preparedStatement.setString(8, appointment.getCreatedBy());
            preparedStatement.setTimestamp(9, Timestamp.valueOf(appointment.getLastUpdate()));
            preparedStatement.setString(10, appointment.getLastUpdatedBy());
            preparedStatement.setInt(11, appointment.getCustomerId());
            preparedStatement.setInt(12, appointment.getUserId());
            preparedStatement.setInt(13, appointment.getContactId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        appointment.setAppointmentId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating appointment failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions here
            e.printStackTrace();
        }
    }
}
