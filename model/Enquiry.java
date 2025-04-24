package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.EnquiryStatus;
import exception.DataModelException;
import manager.CSVDataManager;

/**
 * Represents an enquiry submitted by a user regarding a specific BTO project.
 * <p>
 * Each enquiry contains a subject, the enquiry content, an optional reply,
 * a status (replied or unreplied), and a timestamp of when it was created.
 * </p>
 *
 * In addition to its data, this class encapsulates business logic related to the
 * application process, adhering to the principles of a rich domain model. 
 * It ensures that the application state and behaviors are consistent with the 
 * domain rules, and manipulates its data through methods that enforce business 
 * rules rather than relying solely on external procedures.
 */
public class Enquiry implements DataModel{
    /**
     * Comparator for sorting {@link Enquiry} objects by their creation timestamp in descending order.
     * This comparator compares enquiries based on the {@link Enquiry#getCreatedAt()} method and 
     * sorts them in reverse order, so that the most recently created enquiry appear first.
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
    private String backupSubject;

    @CSVField(index = 4)
    private String enquiry;
    private String backupEnquiry;

    @CSVField(index = 5)
    private String reply;
    private String backupReply;

    @CSVField(index = 6)
    private EnquiryStatus enquiryStatus;
    private EnquiryStatus backupEnquiryStatus;

    @CSVField(index = 7)
    private LocalDateTime createdAt;

    /**
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
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
     * @throws DataModelException if this enquiry cannot be edited
     */
    public void setSubject(String subject) throws DataModelException {
        if(!canBeAltered()){
            throw new DataModelException("This enquiry cannot be edited.");
        }
        
        backup();
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
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param enquiry the new enquiry message
     * @throws DataModelException it this enquiry cannot be edited
     */
    public void setEnquiry(String enquiry) throws DataModelException {
        if(!canBeAltered()){
            throw new DataModelException("This enquiry cannot be edited.");
        }
        
        backup();
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
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param reply the reply content
     * @throws DataModelException if reply cannot be editted
     */
    public void setReply(String reply) throws DataModelException {
        if(!canBeAltered()){
            throw new DataModelException("This enquiry cannot be replied.");
        }

        backup();
        this.reply = reply;
        this.enquiryStatus = EnquiryStatus.REPLIED;
    }

    /**
     * Returns the BTO project associated with this enquiry.
     *
     * @return the associated BTO project
     */
    public BTOProject getBTOProject() {
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

    @Override
    public String getPK() {
        return uuid;
    }

    @Override
    public void backup() {
        this.backupSubject = subject;
        this.backupEnquiry = enquiry;
        this.backupReply = reply;
        this.backupEnquiryStatus = enquiryStatus;
    }

    @Override
    public void restore() {
        this.subject = backupSubject;
        this.enquiry = backupEnquiry;
        this.reply = backupReply;
        this.enquiryStatus = backupEnquiryStatus;
    }
}
