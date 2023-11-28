package DAO;

import Model.AppointmentReportRow;
import Model.Customer;
import Model.DivisionReportRow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static Help.JDBC.connection;

/**
 * The type Customer dao.
 */
public class CustomerDAO {
    /**
     * Gets all customers.
     *
     * @return the all customers
     * @throws SQLException the sql exception
     */
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
     * @return The number of rows affected
     * @throws SQLException If a database access error occurs, this method is called on a closed PreparedStatement, or the given SQL statement produces anything other than a single ResultSet object
     */
    public static int deleteCustomer(int appointmentId) throws SQLException {
        String query = "DELETE FROM customers WHERE Customer_ID = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, appointmentId);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Save customer int.
     *
     * @param customer the customer
     * @return the int
     * @throws SQLException the sql exception
     */
    public static int saveCustomer(Customer customer) throws SQLException {
        String  query = "INSERT INTO customers (Customer_Name, Address, Postal_Code, Phone, Division_ID) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setInt(5, customer.getDivisionId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Customer saving failed, no rows affected.");
            }

            if (customer.getCustomerId() == 0) {
                // If it's a new customer, get the generated customer ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Customer saving failed, no ID obtained.");
                }
            }
            return customer.getCustomerId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Update customer int.
     *
     * @param customer the customer
     * @return the int
     * @throws SQLException the sql exception
     */
    public static int updateCustomer(Customer customer) throws SQLException {
        String query = "UPDATE customers SET Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Division_ID = ? WHERE Customer_ID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getAddress());
            preparedStatement.setString(3, customer.getPostalCode());
            preparedStatement.setString(4, customer.getPhone());
            preparedStatement.setInt(5, customer.getDivisionId());
            preparedStatement.setInt(6, customer.getCustomerId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Customer saving failed, no rows affected.");
            }

            if (customer.getCustomerId() == 0) {
                // If it's a new customer, get the generated customer ID
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    customer.setCustomerId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Customer saving failed, no ID obtained.");
                }
            }
            return customer.getCustomerId();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Gets country id by division name.
     *
     * @param division the division
     * @return the country id by division name
     * @throws SQLException the sql exception
     */
    public static String getCountryIdByDivisionName(String division) throws SQLException {
        String sql = "SELECT Country_ID FROM first_level_divisions WHERE Division = ?";
        int countryId = -1;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, division);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                countryId = resultSet.getInt("Country_ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }
        Map<Integer, String> countryIdToCodeMap = new HashMap<>();
        countryIdToCodeMap.put(1, "US");
        countryIdToCodeMap.put(2, "UK");
        countryIdToCodeMap.put(3, "Canada");

        return countryIdToCodeMap.get(countryId);
    }

    /**
     * Generate division report observable list.
     *
     * @return the observable list
     */
    public static ObservableList<DivisionReportRow> generateDivisionReport() {
        ObservableList<DivisionReportRow> reportDataList = FXCollections.observableArrayList();
        String sql = "SELECT fld.Division AS DivisionName, COUNT(c.Customer_ID) AS TotalCustomers " +
                "FROM first_level_divisions fld " +
                "INNER JOIN customers c ON fld.Division_ID = c.Division_ID " +
                "GROUP BY fld.Division_ID";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String divisionName = resultSet.getString("DivisionName");
                int totalCustomers = resultSet.getInt("TotalCustomers");
                DivisionReportRow divisionReportRow = new DivisionReportRow(divisionName, totalCustomers);
                reportDataList.add(divisionReportRow);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return reportDataList;
    }

    /**
     * Customer has appointments boolean.
     *
     * @param customerId the customer id
     * @return the boolean
     */
    public static boolean customerHasAppointments(int customerId) {
        String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, customerId);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exception
        }

        return false;
    }
}

