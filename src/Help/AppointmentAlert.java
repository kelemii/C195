package Help;

import DAO.AppointmentDAO;
import Model.Appointment;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class AppointmentAlert {
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    public void checkUpcomingAppointments() throws SQLException {
        // Get the current local time of the user's computer
        LocalDateTime currentLocalTime = LocalDateTime.now();

        // Calculate the time 15 minutes from the current time
        LocalDateTime fifteenMinutesLater = currentLocalTime.plusMinutes(15);

        // Convert the local time to the user's timezone (if needed)
        ZoneId userTimeZone = ZoneId.systemDefault();
        ZonedDateTime currentDateTime = currentLocalTime.atZone(userTimeZone);
        ZonedDateTime fifteenMinutesLaterDateTime = fifteenMinutesLater.atZone(userTimeZone);

        // Query the database for upcoming appointments
        ZoneId localZone = ZoneId.systemDefault();
        ZoneId utcZone = ZoneId.of("UTC");

        ZonedDateTime currentUTC = currentDateTime.withZoneSameInstant(utcZone); //.atZone(localZone).withZoneSameInstant(utcZone);
        ZonedDateTime fifteenMinutesLaterUTC = fifteenMinutesLaterDateTime.withZoneSameInstant(utcZone);
        List<Appointment> upcomingAppointments = appointmentDAO.getAppointmentsWithinTimeRange(
                currentUTC.toLocalDateTime(), fifteenMinutesLaterUTC.toLocalDateTime());

        if (!upcomingAppointments.isEmpty()) {
            // Display an alert with appointment details
            for (Appointment appointment : upcomingAppointments) {
                String message = "Upcoming Appointment Alert:\n"
                        + "Appointment ID: " + appointment.getAppointmentId() + "\n"
                        + "Date: " + appointment.getStart().toLocalDate() + "\n"
                        + "Time: " + appointment.getStart().toLocalTime() + " - " + appointment.getEnd().toLocalTime();
                displayAlert(message);
            }
        } else {
            // Display a message indicating no upcoming appointments
            String message = "No upcoming appointments within the next 15 minutes.";
            displayAlert(message);
        }
    }

    private void displayAlert(String message) {
        // Implement code to display an alert dialog with the given message
        // This can be done using JavaFX's Alert class or a custom dialog.
        // Here's an example using JavaFX's Alert:
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Appointment Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
