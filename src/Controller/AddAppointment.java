package Controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import DAO.CustomerDAO;
import DAO.UserDAO;
import Model.Appointment;
import Model.Contact;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


import static Help.JDBC.connection;
/**
 * The `AddAppointment` class controls the UI for adding new appointments.
 * It allows users to input appointment details and save them to the database.
 */
public class AddAppointment {
    @FXML
    private TextField AppointmentID;
    @FXML
    private TextField AppointmentTitle;
    @FXML
    private TextField AppointmentDesc;
    @FXML
    private TextField AppointmentLoc;
    @FXML
    private TextField AppointmentType;
    @FXML
    private DatePicker AppointmentStartD, AppointmentEndD;
    @FXML
    private ComboBox<String> AppointmentStartT, AppointmentEndT;
    @FXML
    private ComboBox<String> AppointmentContact, AppointmentCustomer;
    @FXML
    private ComboBox<Integer> AppointmentUser;
    @FXML
    private Button AddAppCancelBtn;
    private AppointmentDAO appointmentDAO;
    private UserDAO userDAO = new UserDAO();
    private ContactDAO contactDAO;
    /**
     * Initializes the AddAppointment controller.
     *
     * @throws SQLException If an SQL exception occurs while populating data.
     */
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
        populateTimeComboBoxes();
        populateCustomers();
        populateUserList();
        identifyNextID();
    }

    /**
     * Populates the contact selection ComboBox.
     *
     * @throws SQLException If an SQL exception occurs while retrieving contact data.
     */
    private void populateContacts() throws SQLException {
        ObservableList<Contact> contactsList;
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        contactsList = contactDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));
        AppointmentContact.setItems(contactNamesList);
    }
    /**
     * Populates the customer selection ComboBox.
     *
     * @throws SQLException If an SQL exception occurs while retrieving customer data.
     */
    private void populateCustomers() throws SQLException {
        ObservableList<Customer> customerList;
        ObservableList<String> customerNamesList = FXCollections.observableArrayList();
        customerList = CustomerDAO.getAllCustomers();
        customerList.forEach(customer -> customerNamesList.add(customer.getCustomerName()));
        AppointmentCustomer.setItems(customerNamesList);
    }

    /**
     * Identifies the next available appointment ID.
     */
    private void identifyNextID() {
            String sql = "SELECT AUTO_INCREMENT \n" +
                    "FROM information_schema.TABLES \n" +
                    "WHERE TABLE_SCHEMA = 'client_schedule' \n" +
                    "AND TABLE_NAME = 'appointments';\n";

            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);

                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        AppointmentID.setText(rs.getString("AUTO_INCREMENT"));

                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    /**
     * Populates the user selection ComboBox.
     *
     * @throws SQLException If an SQL exception occurs while retrieving user data.
     */
    public void populateUserList() throws SQLException {
        ObservableList<Integer> usersList = FXCollections.observableArrayList();
        usersList = userDAO.getUserIDs();
        AppointmentUser.setItems(userDAO.getUserIDs());
    }
    /**
     * Populates the time selection ComboBoxes with appointment times.
     */
    public void populateTimeComboBoxes() {
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();
        ZoneId easternZone = ZoneId.of("US/Eastern");  // Set the timezone to Eastern Time (ET)
        ZoneId localZone = ZoneId.systemDefault();  // Get the local timezone of the system

        LocalTime firstAppointment = LocalTime.of(8, 0);  // Set the business hours in ET (0800)
        LocalTime lastAppointment = LocalTime.of(22, 0);  // Set the business hours in ET (2200)
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        while (firstAppointment.isBefore(lastAppointment)) {
            ZonedDateTime etTime = LocalDateTime.of(LocalDate.now(), firstAppointment)
                    .atZone(easternZone);
            ZonedDateTime localTime = etTime.withZoneSameInstant(localZone);

            String timeStr = localTime.toLocalTime().format(timeFormatter);
            appointmentTimes.add(timeStr);
            firstAppointment = firstAppointment.plusMinutes(15);
        }

        AppointmentStartT.setItems(appointmentTimes);
        AppointmentEndT.setItems(appointmentTimes);
    }
    /**
     * Handles the action to save a new appointment.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     * @throws SQLException If an SQL exception occurs while saving the appointment.
     */

    public void AddAppSave(ActionEvent actionEvent) throws SQLException {
        if (validateForm()) {
            LocalDate startDate = AppointmentStartD.getValue();
            LocalTime startTime = LocalTime.parse(AppointmentStartT.getValue());
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            DayOfWeek startDayOfWeek = startDateTime.getDayOfWeek();

            LocalDate endDate = AppointmentEndD.getValue();
            LocalTime endTime = LocalTime.parse(AppointmentEndT.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            DayOfWeek endDayOfWeek = endDateTime.getDayOfWeek();

            if (startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY ||
                    endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
                alert.setTitle(resourceBundle.getString("weekendAppointmentTitle"));
                alert.setHeaderText(null);
                alert.setContentText(resourceBundle.getString("weekendAppointmentContent"));
                alert.showAndWait();
            } else {
                int id = Integer.parseInt(AppointmentID.getText());
                String title = AppointmentTitle.getText();
                String description = AppointmentDesc.getText();
                String location = AppointmentLoc.getText();
                String type = AppointmentType.getText();
                int customerId = getCustomerId(AppointmentCustomer.getValue());
                int userId = AppointmentUser.getValue();
                int contactId = getContactId(AppointmentContact.getValue());
                ZoneId localZone = ZoneId.systemDefault();
                ZoneId utcZone = ZoneId.of("UTC");
                LocalDateTime startUTC = startDateTime.atZone(localZone).withZoneSameInstant(utcZone).toLocalDateTime();
                LocalDateTime endUTC = endDateTime.atZone(localZone).withZoneSameInstant(utcZone).toLocalDateTime();
                LocalDateTime createDate = LocalDateTime.now();
                String createdBy = "Admin";
                LocalDateTime lastUpdate = LocalDateTime.now();
                String lastUpdatedBy = "Admin";
                Appointment newAppointment = new Appointment(id, title, description, location, type,
                        startUTC, endUTC, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                appointmentDAO.saveAppointment(newAppointment);
                Stage stage = (Stage) AddAppCancelBtn.getScene().getWindow();
                stage.close();
            }
        }
    }
    /**
     * Retrieves the contact ID based on the contact name.
     *
     * @param contactName The name of the contact.
     * @return The ID of the contact, or -1 if not found.
     * @throws SQLException If an SQL exception occurs while retrieving the contact ID.
     */
    public int getContactId(String contactName) throws SQLException {
        String sql = "SELECT CONTACT_ID FROM client_schedule.contacts WHERE Contact_Name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setString(1, contactName);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CONTACT_ID");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement.executeQuery().getInt("CONTACT_ID");
    }
    /**
     * Retrieves the customer ID based on the customer name.
     *
     * @param customerName The name of the customer.
     * @return The ID of the customer, or -1 if not found.
     * @throws SQLException If an SQL exception occurs while retrieving the customer ID.
     */
    public int getCustomerId(String customerName) throws SQLException {
        String sql = "SELECT CUSTOMER_ID FROM client_schedule.customers WHERE Customer_Name = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setString(1, customerName);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CUSTOMER_ID");
                } else {
                    return -1;
                }
            }
        } catch (SQLException e) {
            // Handle any SQL exceptions here
            e.printStackTrace();
        }
        return preparedStatement.executeQuery().getInt("CUSTOMER_ID");
    }
    /**
     * Handles the action to cancel adding a new appointment.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     */
    public void AddAppCancel(ActionEvent actionEvent) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/cancelConfirmation", Locale.getDefault());

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("Cancel_Confirmation_Title"));
        alert.setHeaderText(resourceBundle.getString("Cancel_Confirmation_Header"));
        alert.setContentText(resourceBundle.getString("Cancel_Confirmation_Content"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) AddAppCancelBtn.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Validates the appointment form data.
     *
     * @return True if the form data is valid, otherwise false.
     */
    private boolean validateForm() {
        if (AppointmentTitle.getText().isEmpty() ||
                AppointmentDesc.getText().isEmpty() ||
                AppointmentLoc.getText().isEmpty() ||
                AppointmentType.getText().isEmpty() ||
                AppointmentStartD.getValue() == null ||
                AppointmentEndD.getValue() == null ||
                AppointmentStartT.getValue() == null ||
                AppointmentEndT.getValue() == null ||
                AppointmentContact.getValue() == null ||
                AppointmentCustomer.getValue() == null ||
                AppointmentUser.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
            alert.setTitle(resourceBundle.getString("Form_Validation_Error_Title"));
            alert.setHeaderText(resourceBundle.getString("Form_Validation_Error_Header"));
            alert.setContentText(resourceBundle.getString("Form_Validation_Error_Content"));
            alert.showAndWait();

            return false;
        }
        LocalDate startDate = AppointmentStartD.getValue();
        LocalDate endDate = AppointmentEndD.getValue();
        LocalTime startTime = LocalTime.parse(AppointmentStartT.getValue());
        LocalTime endTime = LocalTime.parse(AppointmentEndT.getValue());

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
        if (startDateTime.isAfter(endDateTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
            alert.setTitle(resourceBundle.getString("Form_Validation_Bad_Dates"));
            alert.setHeaderText(resourceBundle.getString("Form_Validation_Bad_Date"));
            alert.setContentText(resourceBundle.getString("Form_Validation_Fix_Date"));
            alert.showAndWait();
            return false;
        }

        return true;
    }
    }

