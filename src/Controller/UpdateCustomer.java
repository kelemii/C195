package Controller;

import Model.Appointment;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Optional;

public class UpdateCustomer {
    @FXML
    private Button customerRecordsCancel;

    public void handleCustomerUpdate(ActionEvent actionEvent) {
    }
    public void initData(Appointment selectedAppointment) throws SQLException {
        AppointmentID.setText(String.valueOf(selectedAppointment.getAppointmentId()));
        AppointmentTitle.setText(selectedAppointment.getTitle());
        AppointmentDesc.setText(selectedAppointment.getDescription());
        AppointmentLoc.setText(selectedAppointment.getLocation());
        AppointmentType.setText(selectedAppointment.getType());
        AppointmentStartD.setValue(selectedAppointment.getStart().toLocalDate());
        AppointmentEndD.setValue(selectedAppointment.getEnd().toLocalDate());
        AppointmentStartT.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
        AppointmentEndT.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));
        AppointmentCustomer.setValue(getCustomerName(selectedAppointment.getCustomerId())); // Assuming you have a method to get customer name
        AppointmentUser.setValue(selectedAppointment.getUserId()); // Assuming you have a method to get user name
        AppointmentContact.setValue(getContactName(selectedAppointment.getContactId()));
    }

    public void handleCustomerCancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Confirmation");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("All unsaved changes will be lost.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) customerRecordsCancel.getScene().getWindow();
            stage.close();
        }
    }
}
