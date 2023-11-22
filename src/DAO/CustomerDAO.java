package DAO;

import Model.Customer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static Help.JDBC.connection;

public class CustomerDAO {
    public static ObservableList<Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> customerList = FXCollections.observableArrayList();
        String sql = "SELECT customers.Customer_ID, customers.Customer_Name, customers.Address, customers.Postal_Code, customers.Phone, customers.Division_ID, first_level_divisions.Division from customers INNER JOIN  first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("address");
                String postalCode = resultSet.getString("postal_Code");
                String phone = resultSet.getString("phone");
                int divisionId = resultSet.getInt("Division_ID");
                String division = resultSet.getString("Division");
                Customer customer = new Customer(customerId, customerName, address, postalCode, phone, divisionId, division);
                customerList.add(customer);
            }

        } catch (SQLException e) {
            // Properly handle exception
            e.printStackTrace();
        }

        return customerList;
    }

    /**
     * Deletes an appointment based on the appointment ID.
     *
     * @param appointmentId The ID of the appointment to delete
     * @param connection    The database connection object
     * @return The number of rows affected
     * @throws SQLException If a database access error occurs, this method is called on a closed PreparedStatement, or the given SQL statement produces anything other than a single ResultSet object
     */
    public static int deleteCustomer(int appointmentId, Connection connection) throws SQLException {
        String query = "DELETE FROM appointments WHERE Appointment_ID = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, appointmentId);
            return preparedStatement.executeUpdate();
        }
    }
}

