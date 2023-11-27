package Controller;

import DAO.CustomerDAO;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UpdateCustomer {
    FirstLevelDivisionDAO firstLevelDivisionDAO;
    CustomerDAO customerDAO;
    @FXML
    private Button customerCancel,customerSave;
    @FXML
    private TextField CustomerID, CustomerName, CustomerAdd, CustomerZip, CustomerPhone;
    @FXML
    private ComboBox<String> CustomerCountry, CustomerState;

    public void handleCustomerUpdate(ActionEvent actionEvent) throws SQLException {
        if(validateForm()) {
            int id = Integer.parseInt(CustomerID.getText());
            String name = CustomerName.getText();
            String address = CustomerAdd.getText();
            String postalCode = CustomerZip.getText();
            String division = CustomerState.getValue();
            String phone = CustomerPhone.getText();
            int divisionId = firstLevelDivisionDAO.getDivisionIdByName(CustomerState.getValue());
            Customer newCustomer = new Customer(id, name, address, postalCode, phone, divisionId, division);
            customerDAO.updateCustomer(newCustomer);
            Stage stage = (Stage) customerCancel.getScene().getWindow();
            stage.close();
        }
    }
    public void initData(Customer selectedCustomer) throws SQLException {
        CustomerID.setText(String.valueOf(selectedCustomer.getCustomerId()));
        CustomerName.setText(selectedCustomer.getCustomerName());
        CustomerAdd.setText(selectedCustomer.getAddress());
        CustomerZip.setText(selectedCustomer.getPostalCode());
        CustomerPhone.setText(selectedCustomer.getPhone());
        CustomerCountry.setValue(customerDAO.getCountryIdByDivisionName(selectedCustomer.getDivision())); //this needs a query to identify the country code and a conversion into the string
        CustomerState.setValue(selectedCustomer.getDivision());
    }

    public void handleCustomerCancel(ActionEvent actionEvent) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/cancelConfirmation", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("Cancel_Confirmation_Title"));
        alert.setHeaderText(resourceBundle.getString("Cancel_Confirmation_Header"));
        alert.setContentText(resourceBundle.getString("Cancel_Confirmation_Content"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) customerCancel.getScene().getWindow();
            stage.close();
        }
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
    private boolean validateForm() {
        if (CustomerName.getText().isEmpty() ||
                CustomerAdd.getText().isEmpty() ||
                CustomerZip.getText().isEmpty() ||
                CustomerPhone.getText().isEmpty() ||
                CustomerCountry.getValue() == null ||
                CustomerState.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddCustomer", Locale.getDefault());
            alert.setTitle(resourceBundle.getString("Form_Validation_Error_Title"));
            alert.setHeaderText(resourceBundle.getString("Form_Validation_Error_Header"));
            alert.setContentText(resourceBundle.getString("Form_Validation_Error_Content"));
            alert.showAndWait();
            return false; // At least one required field is empty
        }

        return true; // All required fields are filled, and the division is valid
    }
}
