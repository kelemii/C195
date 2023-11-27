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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Help.JDBC.connection;
/**
 * The `Main` class controls the main application functionality.
 * It manages appointments and customers, including listing, adding, updating, and deleting them.
 */
public class Main implements Initializable {

    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableView<Customer> customersTable;
    @FXML
    private TableColumn<?, ?> AppointmentID, AppointmentTitle, AppointmentType, AppointmentDescription, AppointmentLocation, AppointmentStart, AppointmentEnd, AppContact, AppCustID, AppUserID, CustID, CustName, CustAdd, CustPhone, CustState, CustZip;

    /**
     * Initializes the appointments table with data from the database.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void initializeAppointments() throws SQLException {
        List<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
        List<Appointment> modifiedAppointments = new ArrayList<>();

        ZoneId localZone = ZoneId.systemDefault();

        for (Appointment appointment : allAppointments) {
            LocalDateTime startUTC = appointment.getStart();
            LocalDateTime endUTC = appointment.getEnd();

            // Convert UTC times to local timezone
            ZonedDateTime startLocal = startUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(localZone);
            ZonedDateTime endLocal = endUTC.atZone(ZoneId.of("UTC")).withZoneSameInstant(localZone);

            // Update appointment start and end times with local times
            appointment.setStart(startLocal.toLocalDateTime());
            appointment.setEnd(endLocal.toLocalDateTime());

            modifiedAppointments.add(appointment);
        }

        ObservableList<Appointment> appointmentData = FXCollections.observableArrayList(modifiedAppointments);

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
    /**
     * Initializes the customers table with data from the database.
     *
     * @throws SQLException If an SQL exception occurs.
     */

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

    /**
     *initializes the tables
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        // Initialize tables
        try {
            initializeAppointments();
            initializeCustomers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Handles adding a new appointment.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleNewApp(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/AddAppointment.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Set the window to be modal
        stage.setTitle("Add New Appointment");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        initializeAppointments();
    }
    /**
     * Handles updating an existing appointment.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleUpdateApp(ActionEvent actionEvent) throws IOException, SQLException {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/UpdateAppointment.fxml"));
            Parent root = loader.load();

            // Access the controller for the second view
            UpdateAppointment updateAppointmentController = loader.getController();

            // Pass the data to the second view's controller
            updateAppointmentController.initData(selectedAppointment);

            // Create a new stage for the second view
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Appointment");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            initializeAppointments();
        }
    }

    /**
     * Handles deleting an appointment.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     */
    public void handleDeleteApp(ActionEvent actionEvent) {
        Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();

        if (selectedAppointment != null) {
            int appointmentId = selectedAppointment.getAppointmentId();

            // Call the DAO method to delete the appointment from the database
            try {
                int rowsAffected = AppointmentDAO.deleteAppointment(appointmentId, connection);

                if (rowsAffected > 0) {
                    // Remove the appointment from the ObservableList
                    appointmentsTable.getItems().remove(selectedAppointment);
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/main", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(resourceBundle.getString("Appointment_Deleted_Title"));
                    alert.setHeaderText(resourceBundle.getString("Appointment_Deleted_Header"));
                    alert.setContentText(selectedAppointment.getAppointmentId() + " " + selectedAppointment.getType() + " " + resourceBundle.getString("Appointment_Deleted_Message"));
                    alert.showAndWait();

// Refresh the TableView to reflect the changes
                    appointmentsTable.refresh();

                } else {
                    // Handle the case where deletion fails (e.g., show an error message)
                    // You can display an alert or log an error message here
                    System.err.println("Failed to delete appointment from the database.");
                }
            } catch (SQLException e) {
                // Handle any exceptions that occur during deletion
                e.printStackTrace(); // You can log the error or handle it based on your application's needs
            }
        }
    }
    /**
     * Handles generating reports.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException If an I/O error occurs.
     */
    public void handleReports(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/Reports.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Set the window to be modal
        stage.setTitle("Reports");
        stage.setScene(new Scene(root));
        stage.showAndWait();
//        initializeAppointments();
    }
    /**
     * Handles user logout.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException If an I/O error occurs.
     */
    public void handleLogout(ActionEvent actionEvent) throws IOException {
        // Load the login page
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/Login.fxml"));
        Parent root = loader.load();

        // Get the stage and set the login scene
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }
    /**
     * Handles adding a new customer.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException  If an I/O error occurs.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleAddCust(ActionEvent actionEvent) throws IOException, SQLException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/AddCustomer.fxml"));
        Parent root = loader.load();

        // Create a new stage for the popup
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL); // Set the window to be modal
        stage.setTitle("Add New Customer");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        initializeCustomers();
    }
    /**
     * Handles updating an existing customer.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs.
     * @throws IOException  If an I/O error occurs.
     */
    public void handleUpdateCust(ActionEvent actionEvent) throws SQLException, IOException {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/View/UpdateCustomer.fxml"));
            Parent root = loader.load();

            // Access the controller for the second view
            UpdateCustomer updateCustomerController = loader.getController();

            // Pass the data to the second view's controller
            updateCustomerController.initData(selectedCustomer);

            // Create a new stage for the second view
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Update Customer");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            initializeCustomers();
        }
    }
    /**
     * Handles deleting a customer.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     */
    public void handleDeleteCust(ActionEvent actionEvent) {
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();
        if (selectedCustomer != null) {
            int customerId = selectedCustomer.getCustomerId();
            try {
                int rowsAffected = CustomerDAO.deleteCustomer(customerId);
                if (rowsAffected > 0) {
                    customersTable.getItems().remove(selectedCustomer);
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/main", Locale.getDefault());
                    String successMessage = resourceBundle.getString("Customer_Deleted_Message");
                    String successTitle = resourceBundle.getString("Customer_Deleted_Title");
                    String successHeader = resourceBundle.getString("Customer_Deleted_Header");
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(successTitle);
                    alert.setHeaderText(successHeader); // No header text
                    alert.setContentText(selectedCustomer.getCustomerName() + " " + successMessage);
                    alert.showAndWait();
                } else {
                    System.err.println("Failed to delete customer from the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Handles filtering and displaying all appointments.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleAllAppointments(ActionEvent actionEvent) throws SQLException {
        initializeAppointments();
    }
    /**
     * Handles filtering and displaying appointments for the current month.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleCurrentMonth(ActionEvent actionEvent) throws SQLException {

        LocalDate currentDate = LocalDate.now();
        LocalDate endOfMonth = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        List<Appointment> filteredAppointments;
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

        filteredAppointments = appointmentData.stream()
                .filter(appointment -> {
                    LocalDateTime appointmentStart = appointment.getStart();
                    LocalDate appointmentStartDate = appointmentStart.toLocalDate();
                    return !appointmentStartDate.isBefore(currentDate.withDayOfMonth(1))
                            && !appointmentStartDate.isAfter(endOfMonth);
                })
                .collect(Collectors.toList());

        appointmentsTable.setItems(FXCollections.observableArrayList(filteredAppointments));
    }
    /**
     * Handles filtering and displaying appointments for the current week.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs.
     */
    public void handleCurrentWeek(ActionEvent actionEvent) throws SQLException {
        LocalDate currentDate = LocalDate.now();
        LocalDate endOfWeek = currentDate.plusDays(6 - currentDate.getDayOfWeek().getValue());
        List<Appointment> filteredAppointments;
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

        // Filter the appointments for the current week
        filteredAppointments = appointmentData.stream()
                .filter(appointment -> {
                    LocalDateTime appointmentStart = appointment.getStart();
                    LocalDate appointmentStartDate = appointmentStart.toLocalDate();
                    return !appointmentStartDate.isBefore(currentDate) && !appointmentStartDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());

        // Update the TableView with the filtered appointments
        appointmentsTable.setItems(FXCollections.observableArrayList(filteredAppointments));

    }

}
