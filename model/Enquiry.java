package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.EnquiryStatus;

/**
 * Represents an enquiry submitted by a user regarding a specific BTO project.
 * <p>
 * Each enquiry contains a subject, the enquiry content, an optional reply,
 * a status (replied or unreplied), and a timestamp of when it was created.
 * </p>
 *
 * <p>Implements the {@link DataModel} interface to provide a primary key
 * for data persistence and retrieval operations.</p>
 */
public class Enquiry implements DataModel{

    /**
     * A comparator to sort enquiries in descending order of creation time.
     * Useful for displaying recent enquiries first.
     */
    public static final Comparator<Enquiry> SORT_BY_CREATED_AT_DESC =
        Comparator.comparing(Enquiry::getCreatedAt).reversed();

    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 2, foreignKey = true)
    private User enquirer;

    @CSVField(index = 3)
    private String subject;

    @CSVField(index = 4)
    private String enquiry;

    @CSVField(index = 5)
    private String reply;

    @CSVField(index = 6)
    private EnquiryStatus enquiryStatus;

    @CSVField(index = 7)
    private LocalDateTime createdAt;

     /**
     * Private no-arguments constructor for reflective instantiation.
     */
    @SuppressWarnings("unused")
    private Enquiry(){};

    /**
     * Constructs a new enquiry.
     *
     * @param btoProject the project the enquiry is about
     * @param enquirer the user making the enquiry
     * @param subject the subject of the enquiry
     * @param enquiry the content of the enquiry
     */
    public Enquiry(BTOProject btoProject, User enquirer, String subject, String enquiry){
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();

        this.btoProject = btoProject;

        this.subject = subject;
        this.enquiry = enquiry;
        this.reply = "";

        this.enquiryStatus = EnquiryStatus.UNREPLIED;
        this.createdAt = LocalDateTime.now();

        this.enquirer = enquirer;
    }

    @Override
    /**
     * Returns the unique identifier of the enquiry.
     *
     * @return the primary key (UUID as string)
     */
    public String getPK() {
        return uuid;
    }

    /**
     * Returns the user who made the enquiry.
     *
     * @return the enquirer
     */
    public User getEnquirer() {
        return enquirer;
    }

    /**
     * Returns the subject of the enquiry.
     *
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Updates the subject of the enquiry.
     *
     * @param subject the new subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Returns the content of the enquiry.
     *
     * @return the enquiry message
     */
    public String getEnquiry() {
        return enquiry;
    }

    /**
     * Updates the content of the enquiry.
     *
     * @param enquiry the new enquiry message
     */
    public void setEnquiry(String enquiry) {
        this.enquiry = enquiry;
    }

    /**
     * Returns the reply to the enquiry, if any.
     *
     * @return the reply message
     */
    public String getReply() {
        return reply;
    }

    /**
     * Sets the reply for this enquiry and marks it as replied.
     *
     * @param reply the reply content
     */
    public void setReply(String reply) {
        this.reply = reply;
        this.enquiryStatus = EnquiryStatus.REPLIED;
    }

    /**
     * Reverts the reply, removing the content and marking the enquiry as unreplied.
     */
    public void revertReply(){
        this.reply = "";
        this.enquiryStatus = EnquiryStatus.UNREPLIED;
    }

    /**
     * Returns the BTO project associated with this enquiry.
     *
     * @return the associated BTO project
     */
    public BTOProject getBtoProject() {
        return btoProject;
    }

    /**
     * Returns the current status of the enquiry (REPLIED or UNREPLIED).
     *
     * @return the enquiry status
     */
    public EnquiryStatus getEnquiryStatus() {
        return enquiryStatus;
    }

    /**
     * Returns the date and time when this enquiry was created.
     *
     * @return the timestamp of creation
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Checks whether this enquiry can be altered (i.e., if it hasn't been replied to yet).
     *
     * @return {@code true} if unreplied, otherwise {@code false}
     */
    public boolean canBeAltered(){
        return enquiryStatus == EnquiryStatus.UNREPLIED;
    }
}
