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
import static Help.TimeConversion.convertTime;
import static Help.TimeConversion.convertUtcToTime;

/**
 * The `Main` class controls the main application functionality.
 * It manages appointments and customers, including listing, adding, updating, and deleting them.
 */
public class Main implements Initializable {
    /**
     * appointments tableview
     */

    @FXML
    private TableView<Appointment> appointmentsTable;
    /**
     * customers tableview
     */
    @FXML
    private TableView<Customer> customersTable;
    /**
     * tableview columns
     */
    @FXML
    private TableColumn<?, ?> AppointmentID, AppointmentTitle, AppointmentType, AppointmentDescription, AppointmentLocation, AppointmentStart, AppointmentEnd, AppContact, AppCustID, AppUserID, CustID, CustName, CustAdd, CustPhone, CustState, CustZip;

    /**
     * Initializes the appointments table with data from the database.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    public void initializeAppointments() throws SQLException {
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList(AppointmentDAO.getAllAppointments());
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
        appointmentsTable.setItems(allAppointments);
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

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
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

            UpdateAppointment updateAppointmentController = loader.getController();

            updateAppointmentController.initData(selectedAppointment);

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

            try {
                int rowsAffected = AppointmentDAO.deleteAppointment(appointmentId, connection);

                if (rowsAffected > 0) {
                    appointmentsTable.getItems().remove(selectedAppointment);
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/main", Locale.getDefault());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle(resourceBundle.getString("Appointment_Deleted_Title"));
                    alert.setHeaderText(resourceBundle.getString("Appointment_Deleted_Header"));
                    alert.setContentText(selectedAppointment.getAppointmentId() + " " + selectedAppointment.getType() + " " + resourceBundle.getString("Appointment_Deleted_Message"));
                    alert.showAndWait();

                    appointmentsTable.refresh();

                } else {
                    System.err.println("Failed to delete appointment from the database.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
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

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Reports");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }
    /**
     * Handles user logout.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws IOException If an I/O error occurs.
     */
    public void handleLogout(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/View/Login.fxml"));
        Parent root = loader.load();

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

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
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

            UpdateCustomer updateCustomerController = loader.getController();

            updateCustomerController.initData(selectedCustomer);

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
                boolean hasAppointments = CustomerDAO.customerHasAppointments(customerId);
                if (hasAppointments) {
                    ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/main", Locale.getDefault());
                    String errorMessage = resourceBundle.getString("Customer_Delete_Appointments_Error");
                    String errorTitle = resourceBundle.getString("Customer_Delete_Error_Title");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle(errorTitle);
                    alert.setHeaderText(null);
                    alert.setContentText(errorMessage);
                    alert.showAndWait();
                } else {
                    int rowsAffected = CustomerDAO.deleteCustomer(customerId);
                    if (rowsAffected > 0) {
                        customersTable.getItems().remove(selectedCustomer);
                        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/main", Locale.getDefault());
                        String successMessage = resourceBundle.getString("Customer_Deleted_Message");
                        String successTitle = resourceBundle.getString("Customer_Deleted_Title");
                        String successHeader = resourceBundle.getString("Customer_Deleted_Header");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(successTitle);
                        alert.setHeaderText(successHeader);
                        alert.setContentText(selectedCustomer.getCustomerName() + " " + successMessage);
                        alert.showAndWait();
                    } else {
                        System.err.println("Failed to delete customer from the database.");
                    }
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

        filteredAppointments = appointmentData.stream()
                .filter(appointment -> {
                    LocalDateTime appointmentStart = appointment.getStart();
                    LocalDate appointmentStartDate = appointmentStart.toLocalDate();
                    return !appointmentStartDate.isBefore(currentDate) && !appointmentStartDate.isAfter(endOfWeek);
                })
                .collect(Collectors.toList());

        appointmentsTable.setItems(FXCollections.observableArrayList(filteredAppointments));

    }

}
