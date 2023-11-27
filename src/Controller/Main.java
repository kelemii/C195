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
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static Help.JDBC.connection;


/// next steps are to create reports, and validate that a user does not have any appointments when before deleting, if they have appointments they have to be deleted FIRST, then customer can be deleted.
//•  When a customer record is deleted, a custom message should display in the user interface. DONE
//•  A custom message is displayed in the user interface with the Appointment_ID and type of appointment canceled. DONE
//•  All of the original appointment information is displayed on the update form in local time zone. DONE

// c.  Write code that enables the user to adjust appointment times. While the appointment times should be stored in Coordinated Universal Time (UTC), they should be automatically and consistently updated according to the local time zone set on the user’s computer wherever appointments are displayed in the application.
// Note: There are up to three time zones in effect. Coordinated Universal Time (UTC) is used for storing the time in the database, the user’s local time is used for display purposes, and Eastern Time (ET) is used for the company’s office hours. Local time will be checked against ET business hours before they are stored in the database as UTC.
// done
//d.  Write code to implement input validation and logical error checks to prevent each of the following changes when adding or updating information; display a custom message specific for each error check in the user interface:
//•  scheduling an appointment outside of business hours defined as 8:00 a.m. to 10:00 p.m. ET, including weekends
//•  scheduling overlapping appointments for customers
//•  entering an incorrect username and password
 // these are all handled pretty much, its not possible to schedule outside of the business hours so i dont have an alert created
//e.  Write code to provide an alert when there is an appointment within 15 minutes of the user’s log-in. A custom message should be displayed in the user interface and include the appointment ID, date, and time. If the user does not have any appointments within 15 minutes of logging in, display a custom message in the user interface indicating there are no upcoming appointments.
//Note: Since evaluation may be testing your application outside of business hours, your alerts must be robust enough to trigger an appointment within 15 minutes of the local time set on the user’s computer, which may or may not be ET. DONE

//f.  Write code that generates accurate information in each of the following reports and will display the reports in the user interface:
//Note: You do not need to save and print the reports to a file or provide a screenshot.
//•  the total number of customer appointments by type and month
//•  a schedule for each contact in your organization that includes appointment ID, title, type and description, start date and time, end date and time, and customer ID
//•  an additional report of your choice that is different from the two other required reports in this prompt and from the user log-in date and time stamp that will be tracked in part C

//C.  Write code that provides the ability to track user activity by recording all user log-in attempts, dates, and time stamps and whether each attempt was successful in a file named login_activity.txt. Append each new record to the existing file, and save to the root folder of the application.

//D.  Provide descriptive Javadoc comments for at least 70 percent of the classes and their members throughout the code, and create an index.html file of your comments to include with your submission based on Oracle’s guidelines for the Javadoc tool best practices. Your comments should include a justification for each lambda expression in the method where it is used.

//Note: The comments on the lambda need to be located in the comments describing the method where it is located for it to export properly.
//E.  Create a README.txt file that includes the following information:
//
//•  title and purpose of the application
//
//•  author, contact information, student application version, and date
//
//•  IDE including version number (e.g., IntelliJ Community 2020.01), full JDK of version used (e.g., Java SE 17.0.1), and JavaFX version compatible with JDK version (e.g. JavaFX-SDK-17.0.1)
//
//•  directions for how to run the program
//
//•  a description of the additional report of your choice you ran in part A3f
//
//•  the MySQL Connector driver version number, including the update number (e.g., mysql-connector-java-8.1.23)
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
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Appointment Deleted");
                    alert.setHeaderText("Appointment has been removed from database.");
//                    String alertmsg = selectedCustomer.getCustomerName() + "has been removed";
                    alert.setContentText(selectedAppointment.getAppointmentId() + " " + selectedAppointment.getType() + " has been removed.");
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

    public void handleLogout(ActionEvent actionEvent) throws IOException {
        // Clear user session data
        currentUserID = 0;
//        currentUserName = null;

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

    public void handleDeleteCust(ActionEvent actionEvent) {
        //•  When a customer record is deleted, a custom message should display in the user interface.
        Customer selectedCustomer = customersTable.getSelectionModel().getSelectedItem();

        if (selectedCustomer != null) {
            int customerId = selectedCustomer.getCustomerId();

            // Call the DAO method to delete the appointment from the database
            try {
                int rowsAffected = CustomerDAO.deleteCustomer(customerId);
                if (rowsAffected > 0) {
                    // Remove the customer from the ObservableList
                    customersTable.getItems().remove(selectedCustomer);
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Customer Deleted");
                    alert.setHeaderText("Customer has been removed from database.");
//                    String alertmsg = selectedCustomer.getCustomerName() + "has been removed";
                    alert.setContentText(selectedCustomer.getCustomerName() + " has been removed.");
                    alert.showAndWait();
                    // Refresh the TableView to reflect the changes
                    customersTable.refresh();
                } else {
                    // Handle the case where deletion fails (e.g., show an error message)
                    // You can display an alert or log an error message here
                    System.err.println("Failed to delete customer from the database.");
                }
            } catch (SQLException e) {
                // Handle any exceptions that occur during deletion
                e.printStackTrace(); // You can log the error or handle it based on your application's needs
            }
        }
    }
    public void handleAllAppointments(ActionEvent actionEvent) throws SQLException {
        initializeAppointments();
    }

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
