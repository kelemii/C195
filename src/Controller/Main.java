package Controller;

import DAO.AppointmentDAO;
import DAO.CustomerDAO;
import Model.Appointment;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class Main implements Initializable {
    private int currentUserID;

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<?, ?> AppointmentID, AppointmentTitle, AppointmentType, AppointmentDescription, AppointmentLocation, AppointmentStart, AppointmentEnd, AppContact, AppCustID, AppUserID, CustID, CustName, CustAdd, CustPhone, CustState, CustZip;

    public void setCurrentUserID(int currentUserID) {
        this.currentUserID = currentUserID;
    }

    public void initializeAppointments() throws SQLException {
        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList();
        appointmentData.addAll(AppointmentDAO.getAllAppointments());
        AppointmentID.setCellValueFactory(new PropertyValueFactory<>("AppointmentId"));
        AppointmentTitle.setCellValueFactory(new PropertyValueFactory<>("Title"));
        AppointmentType.setCellValueFactory(new PropertyValueFactory<>("Type"));
        AppointmentDescription.setCellValueFactory(new PropertyValueFactory<>("Description"));
        AppointmentLocation.setCellValueFactory(new PropertyValueFactory<>("Location"));
        AppointmentStart.setCellValueFactory(new PropertyValueFactory<>("Start"));
        AppointmentEnd.setCellValueFactory(new PropertyValueFactory<>("End"));
        AppContact.setCellValueFactory(new PropertyValueFactory<>("ContactId"));
        AppCustID.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        AppUserID.setCellValueFactory(new PropertyValueFactory<>("UserId"));
        appointmentsTable.setItems(appointmentData);
    }

    public void initializeCustomers() throws SQLException {
        ObservableList<Customer> customerData = FXCollections.observableArrayList();
        customerData.addAll(CustomerDAO.getAllCustomers());
        CustID.setCellValueFactory(new PropertyValueFactory<>("CustomerId"));
        CustName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustAdd.setCellValueFactory(new PropertyValueFactory<>("address"));
        CustPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        CustState.setCellValueFactory(new PropertyValueFactory<>("Division"));
        CustZip.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customersTable.setItems(customerData);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize tables
        try {
            initializeAppointments();
            initializeCustomers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void handleNewApp(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/AddAppointment.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Set the window to be modal
        stage.setTitle("Add New Appointment");
        stage.setScene(new Scene(root));
        stage.showAndWait();
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
