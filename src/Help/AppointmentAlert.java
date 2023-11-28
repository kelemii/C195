package Help;

import DAO.AppointmentDAO;
import Model.Appointment;
import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The type Appointment alert.
 */
public class AppointmentAlert {
    /**
     * The Appointment dao.
     */
    AppointmentDAO appointmentDAO = new AppointmentDAO();

    /**
     * Check upcoming appointments.
     *
     * @throws SQLException the sql exception
     */
    public void checkUpcomingAppointments() throws SQLException {
        LocalDateTime currentLocalTime = LocalDateTime.now();

        LocalDateTime fifteenMinutesLater = currentLocalTime.plusMinutes(15);

        ZoneId userTimeZone = ZoneId.systemDefault();
        ZonedDateTime currentDateTime = currentLocalTime.atZone(userTimeZone);
        ZonedDateTime fifteenMinutesLaterDateTime = fifteenMinutesLater.atZone(userTimeZone);

        ZoneId localZone = ZoneId.systemDefault();
        ZoneId utcZone = ZoneId.of("UTC");

        ZonedDateTime currentUTC = currentDateTime.withZoneSameInstant(utcZone);
        ZonedDateTime fifteenMinutesLaterUTC = fifteenMinutesLaterDateTime.withZoneSameInstant(utcZone);
        List<Appointment> upcomingAppointments = appointmentDAO.getAppointmentsWithinTimeRange(
                currentUTC.toLocalDateTime(), fifteenMinutesLaterUTC.toLocalDateTime());

        if (!upcomingAppointments.isEmpty()) {
            for (Appointment appointment : upcomingAppointments) {
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AppointmentAlert", Locale.getDefault());
                String message = resourceBundle.getString("Upcoming_Appointment_Alert") + ":\n"
                        + resourceBundle.getString("Appointment_ID") + ": " + appointment.getAppointmentId() + "\n"
                        + resourceBundle.getString("Date") + ": " + appointment.getStart().toLocalDate() + "\n"
                        + resourceBundle.getString("Time") + ": " + appointment.getStart().toLocalTime() + " - " + appointment.getEnd().toLocalTime();
                displayAlert(message);
            }
        } else {
            String message = "No upcoming appointments within the next 15 minutes.";
            displayAlert(message);
        }
    }

    private void displayAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AppointmentAlert", Locale.getDefault());
        alert.setTitle(resourceBundle.getString("Appointment_Alert_Title"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
