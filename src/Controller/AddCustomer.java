package Controller;

import DAO.FirstLevelDivisionDAO;
import Model.Appointment;
import Model.Customer;
import Model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.stream.Collectors;

public class AddCustomer {
    FirstLevelDivisionDAO firstLevelDivisionDAO;
    @FXML
    private Button customerRecordsCancel;
    @FXML
    private TextField CustomerID, CustomerName, CustomerAdd, CustomerZip, CustomerPhone;
    @FXML
    private ComboBox<String> CustomerCountry;
    @FXML
    private ComboBox<String>  CustomerState;
    @FXML
    public void initialize() throws SQLException {
        populateCountryComboBoxes();
    }
    public void handleCustomerAdd(ActionEvent actionEvent) {
        int id = Integer.parseInt(CustomerID.getText());
        String name = CustomerName.getText();
        String address = CustomerAdd.getText();
        String postalCode = CustomerZip.getText();
        String phone = CustomerPhone.getText();
//        Customer customer = new Customer(id, name, address, postalCode, phone, CustomerCountry, CustomerState);
//        firstLevelDivisionDAO.saveAppointment(newAppointment);
//        Stage stage = (Stage) AddAppCancelBtn.getScene().getWindow();
//        stage.close();
    }
    public void populateCountryComboBoxes() {
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("US"); // 1
        countries.add("UK"); // 2
        countries.add("Canada"); //3
        CustomerCountry.setItems(countries);
    }
    public void populateStateComboBoxes() throws SQLException {
        System.out.println("populating states");
        ObservableList<FirstLevelDivision> states1 = FXCollections.observableArrayList();

        if (CustomerCountry.getValue() != null) {
            if (CustomerCountry.getValue().equals("US")) {
                states1.setAll(firstLevelDivisionDAO.getAllDivisions("1"));
            } else if (CustomerCountry.getValue().equals("UK")) {
                states1.setAll(firstLevelDivisionDAO.getAllDivisions("2"));
            } else if (CustomerCountry.getValue().equals("Canada")) {
                states1.setAll(firstLevelDivisionDAO.getAllDivisions("3"));
            }
        }

        ObservableList<String> stateNames = FXCollections.observableArrayList();

        // Extract division names from the FirstLevelDivision objects and add them to stateNames
        stateNames.addAll(states1.stream().map(FirstLevelDivision::getDivision).collect(Collectors.toList()));

        // Set the items in the CustomerState ComboBox
        CustomerState.setItems(stateNames);
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
