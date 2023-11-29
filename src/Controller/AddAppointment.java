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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;


import static Help.JDBC.connection;
import static Help.TimeConversion.convertTime;
import static Help.TimeConversion.convertUtcToTime;
import static java.time.Clock.systemDefaultZone;

/**
 * The `AddAppointment` class controls the UI for adding new appointments.
 * It allows users to input appointment details and save them to the database.
 */
public class AddAppointment {
    /**
     * textfield
     */
    @FXML
    private TextField AppointmentID;
    /**
     * textfield
     */
    @FXML
    private TextField AppointmentTitle;
    /**
     * textfield
     */
    @FXML
    private TextField AppointmentDesc;
    /**
     * textfield
     */
    @FXML
    private TextField AppointmentLoc;
    /**
     * textfield
     */
    @FXML
    private TextField AppointmentType;
    /**
     * date picker for appointment start/end
     */
    @FXML
    private DatePicker AppointmentStartD, AppointmentEndD;
    /**
     * combo box
     */
    @FXML
    private ComboBox<String> AppointmentStartT, AppointmentEndT;
    /**
     * combo box
     */
    @FXML
    private ComboBox<String> AppointmentContact, AppointmentCustomer;
    /**
     * combo box for user
     */
    @FXML
    private ComboBox<Integer> AppointmentUser;
    /**
     * apps cancel button
     */
    @FXML
    private Button AddAppCancelBtn;
    /**
     * initializes my appointmentDAO
     */
    public AppointmentDAO appointmentDAO = new AppointmentDAO();
    /**
     * initializes my userDAO
     */
    public UserDAO userDAO = new UserDAO();
    /**
     * initializes my contactDAO
     */
    public ContactDAO contactDAO;
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
     * LAMBDA - acquires contactName from each contact in the list and adds it to the contactNameList
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
        ZoneId easternZone = ZoneId.of("US/Eastern");
        ZoneId localZone = ZoneId.systemDefault();

        LocalTime firstAppointment = LocalTime.of(8, 0);
        LocalTime lastAppointment = LocalTime.of(22, 0);
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
        int id = Integer.parseInt(AppointmentID.getText());
        String title = AppointmentTitle.getText();
        String description = AppointmentDesc.getText();
        String location = AppointmentLoc.getText();
        String type = AppointmentType.getText();
        int customerId = getCustomerId(AppointmentCustomer.getValue());
        int userId = AppointmentUser.getValue();
        int contactId = getContactId(AppointmentContact.getValue());
        LocalDateTime createDate = LocalDateTime.now();
        String createdBy = "Admin";
        LocalDateTime lastUpdate = LocalDateTime.now();
        String lastUpdatedBy = "Admin";

        if (validateForm()) {
            LocalDate startDate = AppointmentStartD.getValue();
            LocalTime startTime = LocalTime.parse(AppointmentStartT.getValue());
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);

            LocalDate endDate = AppointmentEndD.getValue();
            LocalTime endTime = LocalTime.parse(AppointmentEndT.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);

            LocalDateTime startUTC = convertTime(startDateTime).toLocalDateTime();
            LocalDateTime endUTC = convertTime(endDateTime).toLocalDateTime();

            DayOfWeek startDayOfWeek = startDateTime.getDayOfWeek();
            DayOfWeek endDayOfWeek = endDateTime.getDayOfWeek();
            List<Appointment> customerAppointments = appointmentDAO.getAppointmentsForCustomer(customerId);

            ZoneId localTimeZone = ZoneId.systemDefault();
            ZoneId estTimeZone = ZoneId.of("America/New_York");

            ZonedDateTime startLocalDateTime = LocalDate.now().atTime(startTime).atZone(localTimeZone);
            ZonedDateTime startEstDateTime = startLocalDateTime.withZoneSameInstant(estTimeZone);
            LocalTime startEstTime = startEstDateTime.toLocalTime();

            ZonedDateTime endLocalDateTime = LocalDate.now().atTime(endTime).atZone(localTimeZone);
            ZonedDateTime endEstDateTime = endLocalDateTime.withZoneSameInstant(estTimeZone);
            LocalTime endEstTime = endEstDateTime.toLocalTime();

            LocalTime estBusinessStart = LocalTime.of(8, 0); // 8:00 AM EST
            LocalTime estBusinessEnd = LocalTime.of(22, 0); // 10:00 PM EST

            boolean hasOverlap = false;

            for (Appointment appointment : customerAppointments) {
                if (startUTC.isBefore(appointment.getEnd()) && endUTC.isAfter(appointment.getStart())) {
                    hasOverlap = true;
                    break;
                }
            }

            if (hasOverlap) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
                alert.setTitle(resourceBundle.getString("overlapAppointmentTitle"));
                alert.setHeaderText(null);
                alert.setContentText(resourceBundle.getString("overlapAppointmentContent"));
                alert.showAndWait();
            } else if (startDayOfWeek == DayOfWeek.SATURDAY || startDayOfWeek == DayOfWeek.SUNDAY ||
                    endDayOfWeek == DayOfWeek.SATURDAY || endDayOfWeek == DayOfWeek.SUNDAY) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
                alert.setTitle(resourceBundle.getString("weekendAppointmentTitle"));
                alert.setHeaderText(null);
                alert.setContentText(resourceBundle.getString("weekendAppointmentContent"));
                alert.showAndWait();
            } else if (startEstTime.isBefore(estBusinessStart) || endEstTime.isAfter(estBusinessEnd)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(resourceBundle.getString("badTime") + startEstTime + " - " + endEstTime + " EST");
                alert.showAndWait();
            } else {


                Appointment newAppointment = new Appointment(id, title, description, location, type,
                        startDateTime, endDateTime, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);

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

