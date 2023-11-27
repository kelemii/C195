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


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Help.JDBC.connection;

/**
 * The `AddCustomer` class controls the UI for adding new customers.
 * It allows users to input customer details and save them to the database.
 */
public class AddCustomer {
    FirstLevelDivisionDAO firstLevelDivisionDAO;
    CustomerDAO customerDao;
    @FXML
    private Button customerCancel, customerSave;
    @FXML
    private TextField CustomerID, CustomerName, CustomerAdd, CustomerZip, CustomerPhone;
    @FXML
    private ComboBox<String> CustomerCountry;
    @FXML
    private ComboBox<String>  CustomerState;
    /**
     * Initializes the AddCustomer controller.
     *
     * @throws SQLException If an SQL exception occurs while populating data.
     */
    @FXML
    public void initialize() throws SQLException {
        populateCountryComboBoxes();
        identifyNextID();
    }
    /**
     * Handles the action to add a new customer.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs while adding the customer.
     */
    public void handleCustomerAdd(ActionEvent actionEvent) throws SQLException {
        if(validateForm()) {
            int id = Integer.parseInt(CustomerID.getText());
            String name = CustomerName.getText();
            String address = CustomerAdd.getText();
            String postalCode = CustomerZip.getText();
            String phone = CustomerPhone.getText();
            String division = CustomerState.getValue();
            int divisionId = firstLevelDivisionDAO.getDivisionIdByName(CustomerState.getValue());
            Customer customer = new Customer(id, name, address, postalCode, phone, divisionId, division);
            customerDao.saveCustomer(customer);
            Stage stage = (Stage) customerSave.getScene().getWindow();
            stage.close();
        }
    }
    /**
     * Identifies the next available customer ID.
     */
    private void identifyNextID() {
        String sql = "SELECT AUTO_INCREMENT " +
                "FROM information_schema.TABLES " +
                "WHERE TABLE_SCHEMA = 'client_schedule' " +
                "AND TABLE_NAME = 'customers';";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                CustomerID.setText(rs.getString("AUTO_INCREMENT"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
    /**
     * Populates the country selection ComboBox.
     */
    public void populateCountryComboBoxes() {
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("US"); // 1
        countries.add("UK"); // 2
        countries.add("Canada"); //3
        CustomerCountry.setItems(countries);
    }

    /**
     * Populates the state selection ComboBox based on the selected country.
     *
     * @throws SQLException If an SQL exception occurs while retrieving state data.
     */
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
    /**
     * Handles the action to cancel adding a new customer.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     */
    public void handleCustomerCancel(ActionEvent actionEvent) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/CancelConfirmation", Locale.getDefault());

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
    /**
     * Validates the customer form data.
     *
     * @return True if the form data is valid, otherwise false.
     */
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
