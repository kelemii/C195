package Controller;

import DAO.AppointmentDAO;
import Model.Appointment;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main implements Initializable {
    private int currentUserID;

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableView<Customer> customersTable;

    public void setCurrentUserID(int currentUserID) {
        this.currentUserID = currentUserID;
    }

    public void initializeAppointments() throws SQLException {
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
        // Fetch appointments from the database
        // For example:
        appointmentData.addAll(AppointmentDAO.getAllAppointments());
        appointmentsTable.setItems(appointmentData);
    }

//    public void initializeCustomers() {
//        ObservableList<Customer> customerData = FXCollections.observableArrayList();
//        // Fetch customers from the database
//        // For example:
//        customerData.addAll(customerDAO.getAllCustomers());
//        customersTable.setItems(customerData);
//    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize tables
        try {
            initializeAppointments();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
//        initializeCustomers();
    }
    public void handleNewApp(ActionEvent actionEvent) {
    }

    public void handleUpdateApp(ActionEvent actionEvent) {
    }

    public void handleDeleteApp(ActionEvent actionEvent) {
    }

    public void handleReports(ActionEvent actionEvent) {
    }

    public void handleLogout(ActionEvent actionEvent) {
    }

    public void handleAddCust(ActionEvent actionEvent) {
    }

    public void handleUpdateCust(ActionEvent actionEvent) {
    }

    public void handleDeleteCust(ActionEvent actionEvent) {
    }

}
