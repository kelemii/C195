package Model;

/**
 * The type Division report row.
 */
public class DivisionReportRow {
    private String divisionName;
    private int totalCustomers;

    /**
     * Instantiates a new Division report row.
     *
     * @param divisionName   the division name
     * @param totalCustomers the total customers
     */
    public DivisionReportRow(String divisionName, int totalCustomers) {
        this.divisionName = divisionName;
        this.totalCustomers = totalCustomers;
    }

    /**
     * Gets division name.
     *
     * @return the division name
     */
    public String getDivisionName() {
        return divisionName;
    }

    /**
     * Sets division name.
     *
     * @param divisionName the division name
     */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }

    /**
     * Gets total customers.
     *
     * @return the total customers
     */
    public int getTotalCustomers() {
        return totalCustomers;
    }

    /**
     * Sets total customers.
     *
     * @param totalCustomers the total customers
     */
    public void setTotalCustomers(int totalCustomers) {
        this.totalCustomers = totalCustomers;
    }
}
