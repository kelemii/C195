package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.util.Optional;

public class AddCustomer {
    @FXML
    private Button customerRecordsCancel;
    public void handleCustomerAdd(ActionEvent actionEvent) {
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
