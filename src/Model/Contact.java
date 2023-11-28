package Model;

/**
 * The type Contact.
 */
public class Contact {

    private int contactId;
    private String contactName;
    private String email;

    /**
     * Instantiates a new Contact.
     *
     * @param contactId   the contact id
     * @param contactName the contact name
     * @param email       the email
     */
// Constructor
    public Contact(int contactId, String contactName, String email) {
        this.contactId = contactId;
        this.contactName = contactName;
        this.email = email;
    }

    /**
     * Gets contact id.
     *
     * @return the contact id
     */
// Getters
    public int getContactId() {
        return contactId;
    }

    /**
     * Gets contact name.
     *
     * @return the contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets contact id.
     *
     * @param contactId the contact id
     */
// Setters
    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    /**
     * Sets contact name.
     *
     * @param contactName the contact name
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
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
