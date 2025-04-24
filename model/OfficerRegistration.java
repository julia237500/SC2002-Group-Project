package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.RegistrationStatus;
import exception.DataModelException;
import manager.CSVDataManager;

/**
 * Represents a registration request from an HDB officer to manage a BTO project.
 * <p>
 * Each registration is uniquely identified and associated with a BTO project and a user.
 * The registration includes status tracking, timestamps, and notification flags for updates.
 * </p>
 * 
 * In addition to its data, this class encapsulates business logic related to the
 * application process, adhering to the principles of a rich domain model. 
 * It ensures that the application state and behaviors are consistent with the 
 * domain rules, and manipulates its data through methods that enforce business 
 * rules rather than relying solely on external procedures.
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
    private LocalDateTime createdAt;

    /**
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
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
     * Returns the timestamp when this registration was created.
     *
     * @return the creation time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Checks if the registration is in a state where it can be approved.
     * The application is approvable its status is currently pending.
     *
     * @return {@code true} if the application can be approved, {@code false} otherwise
     */
    public boolean isApprovable(){
        return registrationStatus == RegistrationStatus.PENDING;
    }

    /**
     * Updates the registration status based on approval or rejection.
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param isApproving true to approve the registration, false to reject
     * @throws DataModelException if the registration has already been processed,
     *                            or if approving would exceed project officer limits
     */
    public void updateRegistrationStatus(boolean isApproving) throws DataModelException{
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
}
