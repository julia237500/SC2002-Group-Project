package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.RegistrationStatus;
import exception.DataModelException;

/**
 * Represents a registration request from an HDB officer to manage a BTO project.
 * <p>
 * Each registration is uniquely identified and associated with a BTO project and a user.
 * The registration includes status tracking, timestamps, and notification flags for updates.
 * </p>
 * 
 * <p>This class implements the {@link DataModel} interface for standardized data handling.</p>
 */
public class OfficerRegistration implements DataModel{
    /**
     * Comparator for sorting officer registrations by creation time in descending order.
     */
    public static final Comparator<OfficerRegistration> SORT_BY_CREATED_AT_DESC =
        Comparator.comparing(OfficerRegistration::getCreatedAt).reversed();

    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 2, foreignKey = true)
    private User HDBOfficer;

    @CSVField(index = 3)
    private RegistrationStatus registrationStatus;
    private RegistrationStatus backupRegistrationStatus;

    @CSVField(index = 4)
    private boolean updated;

    @CSVField(index = 5)
    private LocalDateTime createdAt;

    /**
     * Private no-argument constructor for reflective instantiation.
     */
    @SuppressWarnings("unused")
    private OfficerRegistration() {}

    /**
     * Constructs a new OfficerRegistration with the specified project and officer.
     *
     * @param btoProject the BTO project to be managed
     * @param HDBOfficer the officer applying for registration
     * @throws DataModelException if the user is not an HDB officer
     */
    public OfficerRegistration(BTOProject btoProject, User HDBOfficer) {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.registrationStatus = RegistrationStatus.PENDING;
        this.updated = false;
        this.createdAt = LocalDateTime.now();
        
        this.btoProject = btoProject;
        this.HDBOfficer = HDBOfficer;
    }

    /**
     * Returns the associated BTO project.
     *
     * @return the BTO project
     */
    public BTOProject getBTOProject() {
        return btoProject;
    }
    
    /**
     * Returns the officer who submitted the registration.
     *
     * @return the HDB officer
     */
    public User getHDBOfficer() {
        return HDBOfficer;
    }

    /**
     * Returns the current registration status.
     *
     * @return the registration status
     */
    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    /**
     * Indicates whether the registration has an unread update.
     *
     * @return true if there is an unread update, false otherwise
     */
    public boolean hasUnreadUpdate() {
        return updated;
    }

    /**
     * Marks the registration as having an unread update.
     */
    public void markAsUnread(){
        updated = true;
    }

    /**
     * Marks the registration as read (no unread update).
     */
    public void markAsRead(){
        updated = false;
    }

    /**
     * Returns the timestamp when this registration was created.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    
    public boolean isApprovable(){
        return registrationStatus == RegistrationStatus.PENDING;
    }

    /**
     * Updates the registration status based on approval or rejection.
     *
     * @param isApproving true to approve the registration, false to reject
     * @throws DataModelException if the registration has already been processed,
     *                            or if approving would exceed project officer limits
     */
    public void updateRegistrationStatus(boolean isApproving){
        backup();
        
        if(!isApprovable()){
            throw new DataModelException("Approve/reject unsuccessful, The registration is already approved/rejected.");
        }

        if(isApproving){
            if(btoProject.isExceedingHDBOfficerLimit()){
                throw new DataModelException("Approval unsuccessful. The project has reached the maximum number of officers in-charge.");
            }
            
            registrationStatus = RegistrationStatus.SUCCESSFUL;
        } 
        else{
            registrationStatus = RegistrationStatus.UNSUCCESSFUL;
        }
    }

    /**
     * Returns the primary key (UUID) of the registration.
     *
     * @return the UUID string
     */
    @Override
    public String getPK() {
        return uuid;
    }

    @Override
    public void backup() {
        this.backupRegistrationStatus = registrationStatus;
    }

    @Override
    public void restore() {
        this.registrationStatus = backupRegistrationStatus;
    }

    /**
     * Returns a string representation of the officer registration.
     *
     * @return a formatted string of UUID, project ID, and officer ID
     */
    @Override
    public String toString() {
        return "%s, %s, %s".formatted(
            uuid,
            btoProject.getPK(),
            HDBOfficer.getPK()
        );
    }
}
