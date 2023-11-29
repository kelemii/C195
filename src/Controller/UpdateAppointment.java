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

/**
 * The `UpdateAppointment` class controls the updating of appointment records.
 */
public class UpdateAppointment {
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
    private Button UpdateAppCancelBtn;
    public AppointmentDAO appointmentDAO = new AppointmentDAO();
    public UserDAO userDAO = new UserDAO();
    public ContactDAO contactDAO;
    /**
     * Initializes the `UpdateAppointment` view.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
        populateTimeComboBoxes();
        populateCustomers();
        populateUserList();
    }
    /**
     * Initializes the `UpdateAppointment` view with data from a selected appointment.
     *
     * @param selectedAppointment The appointment to edit.
     * @throws SQLException If an SQL exception occurs.
     */
    public void initData(Appointment selectedAppointment) throws SQLException {
        AppointmentID.setText(String.valueOf(selectedAppointment.getAppointmentId()));
        AppointmentTitle.setText(selectedAppointment.getTitle());
        AppointmentDesc.setText(selectedAppointment.getDescription());
        AppointmentLoc.setText(selectedAppointment.getLocation());
        AppointmentType.setText(selectedAppointment.getType());
        AppointmentStartD.setValue(selectedAppointment.getStart().toLocalDate());
        AppointmentEndD.setValue(selectedAppointment.getEnd().toLocalDate());
        AppointmentStartT.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
        AppointmentEndT.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));
        AppointmentCustomer.setValue(getCustomerName(selectedAppointment.getCustomerId()));
        AppointmentUser.setValue(selectedAppointment.getUserId());
        AppointmentContact.setValue(getContactName(selectedAppointment.getContactId()));
    }
    /**
     * Populates the contact names in the AppointmentContact ComboBox.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    private void populateContacts() throws SQLException {
        ObservableList<Contact> contactsList;
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        contactsList = contactDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));
        AppointmentContact.setItems(contactNamesList);
    }
    /**
     * Populates the customer names in the AppointmentCustomer ComboBox.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    private void populateCustomers() throws SQLException {
        ObservableList<Customer> customerList;
        ObservableList<String> customerNamesList = FXCollections.observableArrayList();
        customerList = CustomerDAO.getAllCustomers();
        customerList.forEach(customer -> customerNamesList.add(customer.getCustomerName()));
        AppointmentCustomer.setItems(customerNamesList);
    }
    /**
     * Populates the user IDs in the AppointmentUser ComboBox.
     *
     * @throws SQLException If an SQL exception occurs.
     */

    public void populateUserList() throws SQLException {
        ObservableList<Integer> usersList = FXCollections.observableArrayList();
        usersList = userDAO.getUserIDs();
        AppointmentUser.setItems(userDAO.getUserIDs());
    }
    /**
     * Populates the time ComboBoxes (AppointmentStartT and AppointmentEndT) with time slots.
     */
    public void populateTimeComboBoxes() {
        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        while (firstAppointment.isBefore(lastAppointment)) {
            String timeStr = firstAppointment.format(timeFormatter);
            appointmentTimes.add(timeStr);
            firstAppointment = firstAppointment.plusMinutes(15);
        }

        AppointmentStartT.setItems(appointmentTimes);
        AppointmentEndT.setItems(appointmentTimes);
    }
    /**
     * Handles the action when the "Save" button is clicked to update an appointment.
     *
     * @param actionEvent The ActionEvent triggered by the button click.
     * @throws SQLException If an SQL exception occurs.
     */
    public void UpdateAppSave(ActionEvent actionEvent) throws SQLException {
        if (validateForm()) {
            int id = Integer.parseInt(AppointmentID.getText());
            String title = AppointmentTitle.getText();
            String description = AppointmentDesc.getText();
            String location = AppointmentLoc.getText();
            String type = AppointmentType.getText();
            int customerId = getCustomerId(AppointmentCustomer.getValue());
            int userId = AppointmentUser.getValue();
            int contactId = getContactId(AppointmentContact.getValue());
            LocalDate startDate = AppointmentStartD.getValue();
            LocalTime startTime = LocalTime.parse(AppointmentStartT.getValue());
            LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
            LocalDate endDate = AppointmentEndD.getValue();
            LocalTime endTime = LocalTime.parse(AppointmentEndT.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
            List<Appointment> customerAppointments = appointmentDAO.getAppointmentsForCustomer(customerId);

            LocalDateTime startUTC = convertTime(startDateTime).toLocalDateTime();
            LocalDateTime endUTC = convertTime(endDateTime).toLocalDateTime();
            LocalDateTime createDate = LocalDateTime.now();
            String createdBy = "Admin";
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdatedBy = "Admin";

            DayOfWeek startDayOfWeek = startDateTime.getDayOfWeek();
            DayOfWeek endDayOfWeek = endDateTime.getDayOfWeek();
            ZoneId estTimeZone = ZoneId.of("America/New_York"); // Eastern Standard Time
            LocalTime estBusinessStart = LocalTime.of(8, 0); // 8:00 AM EST
            LocalTime estBusinessEnd = LocalTime.of(22, 0); // 10:00 PM EST

            ZonedDateTime startZonedDateTime = startDateTime.atZone(estTimeZone);
            ZonedDateTime endZonedDateTime = endDateTime.atZone(estTimeZone);


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
            } else if (startZonedDateTime.toLocalTime().isBefore(estBusinessStart) || endZonedDateTime.toLocalTime().isAfter(estBusinessEnd)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/AddAppointment", Locale.getDefault());
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText(resourceBundle.getString("badTime") + startZonedDateTime.toLocalTime() + " - " + endZonedDateTime.toLocalTime() + " EST");
                alert.showAndWait();
            }
            else {
                Appointment newAppointment = new Appointment(id, title, description, location, type,
                        startUTC, endUTC, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
                System.out.println("appointment added");
                appointmentDAO.updateAppointment(newAppointment);
                Stage stage = (Stage) UpdateAppCancelBtn.getScene().getWindow();
                stage.close();
            }
        }
    }

    /**
     * Retrieves the contact ID for a given contact name.
     *
     * @param contactName The name of the contact.
     * @return The contact ID, or -1 if not found.
     * @throws SQLException If an SQL exception occurs.
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
     * Retrieves the contact name for a given contact ID.
     *
     * @param contactID The ID of the contact.
     * @return The contact name, or null if not found.
     * @throws SQLException If an SQL exception occurs.
     */
    public String getContactName(int contactID) throws SQLException {
        String sql = "SELECT CONTACT_Name FROM client_schedule.contacts WHERE Contact_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, contactID);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("CONTACT_Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement.executeQuery().getString("CONTACT_Name");
    }
    /**
     * Retrieves the customer ID for a given customer name.
     *
     * @param customerName The name of the customer.
     * @return The customer ID, or -1 if not found.
     * @throws SQLException If an SQL exception occurs.
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
     * Retrieves the customer name for a given customer ID.
     *
     * @param customerID The ID of the customer.
     * @return The customer name, or null if not found.
     * @throws SQLException If an SQL exception occurs.
     */
    public String getCustomerName(int customerID) throws SQLException {
        String sql = "SELECT Customer_Name FROM client_schedule.customers WHERE CUSTOMER_ID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        try {
            preparedStatement.setInt(1, customerID);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("customer_Name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return preparedStatement.executeQuery().getString("CUSTOMER_Name");
    }
    /**
     * Handles the action when the "Cancel" button is clicked to close the update window.
     * @param actionEvent event
     */

    public void UpdateAppCancel(ActionEvent actionEvent) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("lang/cancelConfirmation", Locale.getDefault());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(resourceBundle.getString("Cancel_Confirmation_Title"));
        alert.setHeaderText(resourceBundle.getString("Cancel_Confirmation_Header"));
        alert.setContentText(resourceBundle.getString("Cancel_Confirmation_Content"));
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) UpdateAppCancelBtn.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * Validates the appointment update form.
     *
     * @return True if the form is valid; otherwise, false.
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
            alert.setTitle(resourceBundle.getString("Form_Validation_Error_Title"));
            alert.setHeaderText(resourceBundle.getString("Form_Validation_Error_Header"));
            alert.setContentText(resourceBundle.getString("Form_Validation_Error_Content"));
            alert.showAndWait();
            return false;
        }

        return true;
    }
}
