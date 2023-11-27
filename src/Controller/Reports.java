package Controller;

import DAO.AppointmentDAO;
import DAO.ContactDAO;
import Model.Appointment;
import Model.AppointmentReportRow;
import Model.Contact;
import Model.DivisionReportRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

import static DAO.AppointmentDAO.generateReport;
import static DAO.CustomerDAO.generateDivisionReport;

public class Reports {
    @FXML
    private TableView<AppointmentReportRow> reportTable;
    @FXML
    private Button CancelBtn;
    @FXML
    private ComboBox<String> contactCombo;
    ContactDAO contactDAO =  new ContactDAO();
    AppointmentDAO appointmentDAO = new AppointmentDAO();
    @FXML
    private TableColumn<?, ?> AppointmentID, AppointmentTitle, AppointmentType, AppointmentDescription, AppointmentLocation, AppointmentStart, AppointmentEnd, AppContact, AppCustID, AppUserID, AppMonth, AppType, AppTotal, DivName, TotalCust;
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableView<DivisionReportRow> divisionTable;
    @FXML
    public void initialize() throws SQLException {
        populateContacts();
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
        AppMonth.setCellValueFactory(new PropertyValueFactory<>("appointmentMonth"));
        AppType.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        AppTotal.setCellValueFactory(new PropertyValueFactory<>("totalAppointments"));
        DivName.setCellValueFactory(new PropertyValueFactory<>("DivisionName"));
        TotalCust.setCellValueFactory(new PropertyValueFactory<>("TotalCustomers"));
        ObservableList<AppointmentReportRow> appointmentReportData = generateReport();
        reportTable.getItems().addAll(appointmentReportData);
        ObservableList<DivisionReportRow> divisionReportData = generateDivisionReport();
        divisionTable.getItems().addAll(divisionReportData);
    }

    private void populateContacts() throws SQLException {
        ObservableList<Contact> contactsList;
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        contactsList = contactDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));
        contactCombo.setItems(contactNamesList);
    }
    @FXML
    private void handleContactSelection() {
        String selectedContact = contactCombo.getValue();

        if (selectedContact != null) {
            // Query appointments for the selected contact
            List<Appointment> appointments = appointmentDAO.getAppointmentsForContact(selectedContact);

            // Populate the TableView with the filtered appointments
            populateAppointments(appointments);
        }
    }
    // Populate the TableView with a list of appointments
    private void populateAppointments(List<Appointment> appointments) {
        appointmentsTable.getItems().clear();
        appointmentsTable.getItems().addAll(appointments);
    }
}
