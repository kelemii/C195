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
import java.util.Optional;


import static Help.JDBC.connection;

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
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
        populateTimeComboBoxes();
        populateCustomers();
        populateUserList();
        identifyNextID();
    }
    private void populateContacts() throws SQLException {
        ObservableList<Contact> contactsList;
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        contactsList = contactDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));
        AppointmentContact.setItems(contactNamesList);
    }
    private void populateCustomers() throws SQLException {
        ObservableList<Customer> customerList;
        ObservableList<String> customerNamesList = FXCollections.observableArrayList();
        customerList = CustomerDAO.getAllCustomers();
        customerList.forEach(customer -> customerNamesList.add(customer.getCustomerName()));
        AppointmentCustomer.setItems(customerNamesList);
    }

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
                // Handle exception
            }
        }


    public void populateUserList() throws SQLException {
        ObservableList<Integer> usersList = FXCollections.observableArrayList();
        usersList = userDAO.getUserIDs();
        AppointmentUser.setItems(userDAO.getUserIDs());
    }



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





    public void AddAppSave(ActionEvent actionEvent) throws SQLException {
        if (validateForm()) {
            int id = Integer.parseInt(AppointmentID.getText());
            String title = AppointmentTitle.getText();
            String description = AppointmentDesc.getText();
            String location = AppointmentLoc.getText();
            String type = AppointmentType.getText();
            int customerId = getCustomerId(AppointmentCustomer.getValue());
            int userId = AppointmentUser.getValue();
            int contactId = getContactId(AppointmentContact.getValue());
            LocalTime startT = LocalTime.parse(AppointmentStartT.getValue());
            LocalTime endT = LocalTime.parse(AppointmentEndT.getValue());
            ZoneId localZone = ZoneId.systemDefault();
            ZoneId utcZone = ZoneId.of("UTC");
            LocalDateTime startDateTime = LocalDateTime.of(AppointmentStartD.getValue(), startT);
            ZonedDateTime startLocal = startDateTime.atZone(localZone);
            ZonedDateTime startUTC = startLocal.withZoneSameInstant(utcZone);
            LocalDateTime endDateTime = LocalDateTime.of(AppointmentEndD.getValue(), endT);
            ZonedDateTime endLocal = endDateTime.atZone(localZone);
            ZonedDateTime endUTC = endLocal.withZoneSameInstant(utcZone);
            LocalDateTime createDate = LocalDateTime.now();
            String createdBy = "Admin";
            LocalDateTime lastUpdate = LocalDateTime.now();
            String lastUpdatedBy = "Admin";
            Appointment newAppointment = new Appointment(id, title, description, location, type,
                    startUTC.toLocalDateTime(), endUTC.toLocalDateTime(), createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
            appointmentDAO.saveAppointment(newAppointment);
            Stage stage = (Stage) AddAppCancelBtn.getScene().getWindow();
            stage.close();
        }
    }


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
            // Handle any SQL exceptions here
            e.printStackTrace();
        }
        return preparedStatement.executeQuery().getInt("CONTACT_ID");
    }
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

    public void AddAppCancel(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel Confirmation");
        alert.setHeaderText("Are you sure you want to cancel?");
        alert.setContentText("All unsaved changes will be lost.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) AddAppCancelBtn.getScene().getWindow();
            stage.close();
        }
    }
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
            alert.setTitle("Form Validation Error");
            alert.setHeaderText("Required fields are empty");
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return false; // At least one required field is empty
        }
        LocalDate startDate = AppointmentStartD.getValue();
        LocalDate endDate = AppointmentEndD.getValue();
        LocalTime startTime = LocalTime.parse(AppointmentStartT.getValue());
        LocalTime endTime = LocalTime.parse(AppointmentEndT.getValue());

        LocalDateTime startDateTime = LocalDateTime.of(startDate, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, endTime);
        if (startDateTime.isAfter(endDateTime)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Form Validation Error");
            alert.setHeaderText("Start must be before end");
            alert.setContentText("Please fill in all required fields.");
            alert.showAndWait();
            return false; // Start date is after the end date
        }

        return true; // All required fields are filled and the start date is before the end date
    }
    }

