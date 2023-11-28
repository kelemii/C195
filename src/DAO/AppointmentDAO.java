package DAO;

import Model.Appointment;
import Model.AppointmentReportRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static Help.JDBC.connection;

/**
 * Data access object for appointments
 */
public class AppointmentDAO {
    /**
     * retrieves all appointments from DB
     * @return returns all appointments
     * @throws SQLException if sql error occurs
     */
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

    /**
     * saves a new appointment to the DB
     * @param appointment the appointment that gets saved
     * @throws SQLException if an error occurs with sql
     */
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
            e.printStackTrace();
        }
    }

    /**
     * updates an existing appointment in the DB
     * @param appointment app that is updated
     * @throws SQLException if sql error occurs
     */
    public static void updateAppointment(Appointment appointment) throws SQLException {
        String sql = "UPDATE appointments " +
                "SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, " +
                "Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, " +
                "Customer_ID = ?, User_ID = ?, Contact_ID = ? " +
                "WHERE Appointment_ID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

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
            preparedStatement.setInt(14, appointment.getAppointmentId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns a list of appointments within a time frame
     * @param startTime app start
     * @param endTime app end
     * @return returns the appointemnts in that timeframe
     * @throws SQLException if an error occurs with sql
     */
    public List<Appointment> getAppointmentsWithinTimeRange(
            LocalDateTime startTime, LocalDateTime endTime) throws SQLException {

        List<Appointment> appointments = new ArrayList<>();

        try {
            LocalDate currentDate = LocalDate.now();
            LocalTime startTimeLocal = startTime.toLocalTime();
            LocalTime endTimeLocal = endTime.toLocalTime();
            LocalDateTime startDateTime = LocalDateTime.of(currentDate, startTimeLocal);
            LocalDateTime endDateTime = LocalDateTime.of(currentDate, endTimeLocal);
            ZoneId localZone = ZoneId.systemDefault();
            ZoneId utcZone = ZoneId.of("UTC");
            ZonedDateTime startLocal = startDateTime.atZone(localZone);
            ZonedDateTime endLocal = endDateTime.atZone(localZone);
            ZonedDateTime startUTC = startLocal.withZoneSameInstant(utcZone);
            ZonedDateTime endUTC = endLocal.withZoneSameInstant(utcZone);
            Timestamp startTimestamp = Timestamp.from(startUTC.toInstant());
            Timestamp endTimestamp = Timestamp.from(endUTC.toInstant());
            String sql = "SELECT * FROM appointments WHERE start >= ? AND start <= ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            System.out.println(startTimestamp);
            System.out.println(endTimestamp);
            statement.setTimestamp(1, startTimestamp);
            statement.setTimestamp(2, endTimestamp);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String type = resultSet.getString("type");
                LocalDateTime start = resultSet.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = resultSet.getTimestamp("end").toLocalDateTime();
                LocalDateTime createDate = resultSet.getTimestamp("create_date").toLocalDateTime();
                String createdBy = resultSet.getString("created_by");
                LocalDateTime lastUpdate = resultSet.getTimestamp("last_update").toLocalDateTime();
                String lastUpdatedBy = resultSet.getString("last_updated_by");
                int customerId = resultSet.getInt("customer_id");
                int userId = resultSet.getInt("user_id");
                int contactId = resultSet.getInt("contact_id");
                Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointments;
    }

    /**
     * gets appointment for specific contact
     * @param contactName name of contact
     * @return returns that contacts appointments
     */
    public List<Appointment> getAppointmentsForContact(String contactName) {
        List<Appointment> appointments = new ArrayList<>();
        try {
            String sql = "SELECT a.* FROM appointments a JOIN contacts c ON a.contact_id = c.contact_id WHERE c.contact_name = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, contactName);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int appointmentId = resultSet.getInt("appointment_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                String location = resultSet.getString("location");
                String type = resultSet.getString("type");
                LocalDateTime start = resultSet.getTimestamp("start").toLocalDateTime();
                LocalDateTime end = resultSet.getTimestamp("end").toLocalDateTime();
                LocalDateTime createDate = resultSet.getTimestamp("create_date").toLocalDateTime();
                String createdBy = resultSet.getString("created_by");
                LocalDateTime lastUpdate = resultSet.getTimestamp("last_update").toLocalDateTime();
                String lastUpdatedBy = resultSet.getString("last_updated_by");
                int customerId = resultSet.getInt("customer_id");
                int userId = resultSet.getInt("user_id");
                int contactId = resultSet.getInt("contact_id");
                Appointment appointment = new Appointment(appointmentId, title, description, location, type, start, end, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return appointments;
    }

    /**
     * generates data for my appointment report tableview
     * @return returns appointment data for reports
     * @throws SQLException if an error occurs with sql
     */
    public static ObservableList<AppointmentReportRow> generateReport() throws SQLException {
        ObservableList<AppointmentReportRow> reportDataList = FXCollections.observableArrayList();

        String sql = "SELECT DATE_FORMAT(Start, '%M') AS AppointmentMonth, Type, COUNT(*) AS TotalAppointments " +
                "FROM appointments " +
                "GROUP BY Type, DATE_FORMAT(Start, '%M')";

        try {
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String appointmentMonth = resultSet.getString("AppointmentMonth");
                String appointmentType = resultSet.getString("Type");
                int totalAppointments = resultSet.getInt("TotalAppointments");

                AppointmentReportRow appointmentReportRow = new AppointmentReportRow(appointmentMonth, appointmentType, totalAppointments);
                reportDataList.add(appointmentReportRow);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reportDataList;
    }

}
