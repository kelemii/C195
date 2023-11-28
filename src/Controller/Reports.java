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
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

import static DAO.AppointmentDAO.generateReport;
import static DAO.CustomerDAO.generateDivisionReport;

/**
 * The `Reports` class controls the reporting functionality of the application.
 * It allows users to generate and view various reports.
 */
public class Reports {
    @FXML
    private TableView<AppointmentReportRow> reportTable;
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
    /**
     * Initializes the Reports view and populates it with data.
     * LAMBDA 2 acts as a listener for the contactCombos selected value
     * @throws SQLException If an SQL exception occurs.
     */
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
        contactCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
            handleContactSelection(newValue);
        });
    }
    /**
     * Populates the contact combo box with contact names.
     *
     * @throws SQLException If an SQL exception occurs.
     */
    private void populateContacts() throws SQLException {
        ObservableList<Contact> contactsList;
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        contactsList = contactDAO.getAllContacts();
        contactsList.forEach(contact -> contactNamesList.add(contact.getContactName()));
        contactCombo.setItems(contactNamesList);
    }
    /**
     * Handles the selection of a contact in the combo box and filters appointments accordingly.
     */
    @FXML
    private void handleContactSelection(String selectedContact) {
        if (selectedContact != null) {
            // Query appointments for the selected contact
            List<Appointment> appointments = appointmentDAO.getAppointmentsForContact(selectedContact);

            // Populate the TableView with the filtered appointments
            populateAppointments(appointments);
        }
    }
    /**
     * Populates the appointments table with a list of appointments.
     *
     * @param appointments The list of appointments to display.
     */
    private void populateAppointments(List<Appointment> appointments) {
        appointmentsTable.getItems().clear();
        appointmentsTable.getItems().addAll(appointments);
    }
    /**
     * Handles the cancel button action to close the reports window.
     *
     * @param actionEvent The ActionEvent triggered by the user.
     */
    public void CancelBtn(ActionEvent actionEvent) {
        Stage stage = (Stage) ((Node)actionEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
