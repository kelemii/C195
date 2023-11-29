package DAO;

import Model.Contact;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static Help.JDBC.connection;

/**
 * The type Contact dao.
 */
public class ContactDAO {
    /**
     * this returns all contacts in my DB
     * @return list of contacts
     * @throws SQLException if sql error occurs
     */
    public static ObservableList<Contact> getAllContacts() throws SQLException {
        ObservableList<Contact> contactObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM client_schedule.CONTACTS";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()) {
            int contactID = resultSet.getInt("Contact_ID");
            String contactName = resultSet.getString("Contact_Name");
            String contactEmail = resultSet.getString("Email");
            Contact contact = new Contact(contactID, contactName, contactEmail);
            contactObservableList.add(contact);
        }
        return contactObservableList;
    }
}
