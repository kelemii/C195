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
import java.util.Optional;
import java.util.stream.Collectors;

import static Help.JDBC.connection;

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
    @FXML
    public void initialize() throws SQLException {
        populateCountryComboBoxes();
        identifyNextID();
    }
    public void handleCustomerAdd(ActionEvent actionEvent) throws SQLException {
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

    public void populateCountryComboBoxes() {
        ObservableList<String> countries = FXCollections.observableArrayList();
        countries.add("US"); // 1
        countries.add("UK"); // 2
        countries.add("Canada"); //3
        CustomerCountry.setItems(countries);
    }
    public int countrySelectedtoId(String country) {
        if (country.equals("US")) return 1;
        if (country.equals("UK")) return 2;
        if (country.equals("Canada")) return 3;
        return 0;
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
            Stage stage = (Stage) customerCancel.getScene().getWindow();
            stage.close();
        }
    }
}
