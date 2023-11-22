package Controller;

import DAO.AppointmentDAO;
import DAO.UserDAO;
import Model.Appointment;
import Model.Contact;
import Model.Customer;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


import static Controller.Login.currentUserName;
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
    private ComboBox<LocalTime> AppointmentStartT, AppointmentEndT;
    @FXML
    private ComboBox<Contact> AppointmentContact, AppointmentCustomer;
    @FXML
    private ComboBox<Integer> AppointmentUser;
    private AppointmentDAO appointmentDAO;
    private UserDAO userDAO = new UserDAO();
    //private ContactsDAO contactsDAO;
    @FXML
    public void initialize() throws SQLException {
        ObservableList<Contact> contactsList = FXCollections.observableArrayList();
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
//        contactsList = contactsDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));

        populateTimeComboBoxes();
        populateUserList();
        identifyNextID();
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
//            return -1; // Return -1 or any other appropriate error value
        }


    public void populateContactList() {}
    public void populateUserList() throws SQLException {
        ObservableList<Integer> usersList = FXCollections.observableArrayList();
        usersList = userDAO.getUserIDs();
        AppointmentUser.setItems(userDAO.getUserIDs());
    }



    public void populateTimeComboBoxes() {
        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

        while (firstAppointment.isBefore(lastAppointment)) {
            appointmentTimes.add(firstAppointment);
            firstAppointment = firstAppointment.plusMinutes(15);
        }

        AppointmentStartT.setItems(appointmentTimes);
        AppointmentEndT.setItems(appointmentTimes);
    }



    public void AddAppSave(ActionEvent actionEvent) throws SQLException {
        int id = Integer.parseInt(AppointmentID.getText());
        String title = AppointmentTitle.getText();
        String description = AppointmentDesc.getText();
        String location = AppointmentLoc.getText();
        String type = AppointmentType.getText();
        int customerId = Integer.parseInt(AppointmentCustomer.getId());
        int userId = Integer.parseInt(AppointmentUser.getId());
        int contactId = Integer.parseInt(AppointmentContact.getId());
        LocalDateTime startDateTime = LocalDateTime.of(AppointmentStartD.getValue(), AppointmentStartT.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(AppointmentEndD.getValue(), AppointmentEndT.getValue());
        LocalDateTime createDate = LocalDateTime.now();
        String createdBy = currentUserName;
        LocalDateTime lastUpdate = LocalDateTime.now();
        String lastUpdatedBy = currentUserName;
        Appointment newAppointment = new Appointment(id, title, description, location, type, startDateTime, endDateTime, createDate, createdBy, lastUpdate, lastUpdatedBy, customerId, userId, contactId);
        appointmentDAO.saveAppointment(newAppointment);
    }

    public void AddAppCancel(ActionEvent actionEvent) {
    }
}
