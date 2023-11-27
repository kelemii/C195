package Model;

public class AppointmentReportRow {
    private String appointmentMonth;
    private String appointmentType;
    private int totalAppointments;

    public AppointmentReportRow(String appointmentMonth, String appointmentType, int totalAppointments) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentType = appointmentType;
        this.totalAppointments = totalAppointments;
    }

    public String getAppointmentMonth() {
        return appointmentMonth;
    }

    public void setAppointmentMonth(String appointmentMonth) {
        this.appointmentMonth = appointmentMonth;
    }

    public String getAppointmentType() {
        return appointmentType;
    }

    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }

    public int getTotalAppointments() {
        return totalAppointments;
    }

    public void setTotalAppointments(int totalAppointments) {
        this.totalAppointments = totalAppointments;
    }
}
