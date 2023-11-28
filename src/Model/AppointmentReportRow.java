package Model;

/**
 * The type Appointment report row.
 */
public class AppointmentReportRow {
    private String appointmentMonth;
    private String appointmentType;
    private int totalAppointments;

    /**
     * Instantiates a new Appointment report row.
     *
     * @param appointmentMonth  the appointment month
     * @param appointmentType   the appointment type
     * @param totalAppointments the total appointments
     */
    public AppointmentReportRow(String appointmentMonth, String appointmentType, int totalAppointments) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentType = appointmentType;
        this.totalAppointments = totalAppointments;
    }

    /**
     * Gets appointment month.
     *
     * @return the appointment month
     */
    public String getAppointmentMonth() {
        return appointmentMonth;
    }

    /**
     * Sets appointment month.
     *
     * @param appointmentMonth the appointment month
     */
    public void setAppointmentMonth(String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    /**
     * Gets appointment type.
     *
     * @return the appointment type
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * Sets appointment type.
     *
     * @param appointmentType the appointment type
     */
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    /**
     * Gets total appointments.
     *
     * @return the total appointments
     */
    public int getTotalAppointments() {
        return totalAppointments;
    }

    /**
     * Sets total appointments.
     *
     * @param totalAppointments the total appointments
     */
    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
}
