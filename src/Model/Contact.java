package Model;

public class Contact {

    private int contactId;
    private String contactName;
    private String email;

    // Constructor
    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
    }

    // Getters
    public int getContactId() {
        return contactId;
    }

    public String getContactName() {
        return contactName;
    }

    public String getEmail() {
        return email;
    }

    // Setters
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Override toString for better object representation in logs or debugging
    @Override
    public String toString() {
        return "Contact{" +
                "contactId=" + contactId +
                ", contactName='" + contactName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    // Additional methods like equals and hashCode could be implemented as needed
}
