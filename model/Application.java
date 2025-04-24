package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.ApplicationStatus;
import config.FlatType;
import config.WithdrawalStatus;
import exception.DataModelException;
import manager.CSVDataManager;

/**
 * Represents a BTO flat application submitted by a user.
 * Each application contains information about the applicant, the project,
 * the flat type, application status, and its creation timestamp.
 * <p>
 * In addition to its data, this class encapsulates business logic related to the
 * application process, adhering to the principles of a rich domain model. 
 * It ensures that the application state and behaviors are consistent with the 
 * domain rules, and manipulates its data through methods that enforce business 
 * rules rather than relying solely on external procedures.
 */
public class Application implements DataModel{
    /**
     * Comparator for sorting {@link Application} objects by their creation timestamp in descending order.
     * This comparator compares applications based on the {@link Application#getCreatedAt()} method and 
     * sorts them in reverse order, so that the most recently created applications appear first.
     */
    public static final Comparator<Application> SORT_BY_CREATED_AT_DESC =
        Comparator.comparing(Application::getCreatedAt).reversed();

    @CSVField(index = 0)
    private String uuid;

    @CSVField(index = 1, foreignKey = true)
    private User applicant;

    @CSVField(index = 2, foreignKey = true)
    private BTOProject btoProject;

    @CSVField(index = 3)
    private FlatType flatType;

    @CSVField(index = 4)
    private ApplicationStatus applicationStatus;
    private ApplicationStatus backupApplicationStatus;

    @CSVField(index = 5)
    private WithdrawalStatus withdrawalStatus;
    private WithdrawalStatus backupWithdrawalStatus;

    @CSVField(index = 6)
    private LocalDateTime createdAt;

    /**
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
     */
    @SuppressWarnings("unused")
    private Application(){}

    /**
     * Constructs a new {@code Application} with the specified applicant,
     * BTO project, and flat type. Performs eligibility checks.
     *
     * @param applicant the user applying for a flat
     * @param btoProject the BTO project applied for
     * @param flatType the type of flat applied for
     * @throws DataModelException if validation fail, like user is not eligible for the flat type
     * 
     * @see User
     * @see BTOProject
     * @see FlatType
     */
    public Application(User applicant, BTOProject btoProject, FlatType flatType) throws DataModelException{
        checkFlatTypeEligibility(applicant, btoProject, flatType);

        this.applicant = applicant;
        this.btoProject = btoProject;
        this.flatType = flatType;

        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();

        this.applicationStatus = ApplicationStatus.PENDING;
        this.withdrawalStatus = WithdrawalStatus.NOT_APPLICABLE;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Validates the eligibility of the user to apply for the given flat type in the BTO project.
     *
     * @param applicant the applicant user
     * @param btoProject the BTO project
     * @param flatType the flat type
     * @throws DataModelException if the flat type is unavailable or the user is not eligible
     */
    private void checkFlatTypeEligibility(User applicant, BTOProject btoProject, FlatType flatType) throws DataModelException {
        if(!btoProject.hasAvailableFlats(flatType)){
            throw new DataModelException("%s is not available for the project %s".formatted(flatType.getStoredString(), btoProject.getName()));
        }

        if(!flatType.isEligible(applicant)){
            throw new DataModelException("You are not eligible to apply for %s".formatted(flatType.getStoredString()));
        }
    }

    /**
     * Gets the applicant (user) who submitted the application.
     *
     * @return the applicant
     */
    public User getApplicant() {
        return applicant;
    }

    /**
     * Gets the BTO project applied for.
     *
     * @return the BTO project
     */
    public BTOProject getBTOProject() {
        return btoProject;
    }

    
    /**
     * Gets the flat type applied for.
     *
     * @return the flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }

    /**
     * Gets the current status of the application.
     *
     * @return the application status
     */
    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    /**
     * Sets the current {@link ApplicationStatus} and creates a backup of the previous status.
     * This allows tracking the previous status for potential rollback or audit purposes.
     * It is always recommended to call this method to change application status internally.
     * 
     * @param applicationStatus The new {@code ApplicationStatus} to be set.
     * 
     * @see ApplicationStatus
     */
    private void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.backupApplicationStatus = this.applicationStatus;
        this.applicationStatus = applicationStatus;
    }

    /**
     * Retrieves the current {@link WithdrawalStatus}.
     * 
     * @return The current {@code WithdrawalStatus} of the application.
     * 
     * @see WithdrawalStatus
     */
    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    /**
     * Sets the current {@link WithdrawalStatus} and creates a backup of the previous status.
     * This allows tracking the previous status for potential rollback or audit purposes.
     * It is always recommended to call this method to change withdrawal status internally.
     * 
     * @param withdrawalStatus The new {@link WithdrawalStatus} to be set.
     * 
     * @see WithdrawalStatus
     */
    private void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.backupWithdrawalStatus = this.withdrawalStatus;
        this.withdrawalStatus = withdrawalStatus;
    }
    
    /**
     * Gets the timestamp of when the application was created.
     *
     * @return the creation date and time
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    /**
     * Retrieves the number of flats available for the specified flat type in the BTO project.
     * This method simplifies access to the flat number and prevents messy chaining of methods.
     */
    public int getFlatNum() {
        return btoProject.getFlatNum(flatType);
    }

    /**
     * Checks if the application is in a state where it can be approved.
     * The application is approvable if it is updatable
     * and its status is currently pending.
     *
     * @return {@code true} if the application can be approved, {@code false} otherwise
     */
    public boolean isApprovable() {
        return isUpdatable() && applicationStatus == ApplicationStatus.PENDING;
    }

    /**
     * Checks if the application is in a state where it can be booked.
     * The application is bookable if it is updatable
     * and its status is marked as successful.
     *
     * @return {@code true} if the application can be booked, {@code false} otherwise
     */
    public boolean isBookable() {
        return isUpdatable() && applicationStatus == ApplicationStatus.SUCCESSFUL;
    }

    /**
     * Determines if the application can be updated.
     * The application is considered updatable if its withdrawal status is either 
     * not applicable or unsuccessful, meaning it has not been withdrawn or it was unsuccessful 
     * in the past and can be modified.
     *
     * @return {@code true} if the application can be updated, {@code false} otherwise
     */
    private boolean isUpdatable() {
        return withdrawalStatus == WithdrawalStatus.NOT_APPLICABLE || withdrawalStatus == WithdrawalStatus.UNSUCCESSFUL;
    }

    /**
     * Approve or reject an application based on the provided flag.
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param isApproving if {@code true}, approves the application; otherwise, rejects it
     * @throws DataModelException if the application is not in a valid state to be approved or rejected
     */
    public void approveApplication(boolean isApproving) throws DataModelException {
        if(applicationStatus != ApplicationStatus.PENDING){
            throw new DataModelException("Application approval unsuccessful. The project is not under pending.");
        }

        if(withdrawalStatus == WithdrawalStatus.PENDING){
            throw new DataModelException("Application approval unsuccessful. The project is under pending withdrawal.");
        }

        if(withdrawalStatus == WithdrawalStatus.SUCCESSFUL){
            throw new DataModelException("Application approval unsuccessful. The project is under withdrawed.");
        }
        
        if(isApproving) setApplicationStatus(ApplicationStatus.SUCCESSFUL);
        else setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
    }

    /**
     * Book the flat for this application.
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @throws DataModelException if the application is not in a valid state for booking
     */
    public void bookApplication() throws DataModelException{
        if(applicationStatus == ApplicationStatus.BOOKED){
            throw new DataModelException("Application booking unsuccessful. The application is already booked.");
        }

        if(applicationStatus != ApplicationStatus.SUCCESSFUL){
            throw new DataModelException("Application booking unsuccessful. The application is not approved.");
        }

        if(withdrawalStatus == WithdrawalStatus.PENDING){
            throw new DataModelException("Application booking unsuccessful. The project is under pending withdrawal.");
        }

        if(withdrawalStatus == WithdrawalStatus.SUCCESSFUL){
            throw new DataModelException("Application booking unsuccessful. The project is withdrawed.");
        }
        
        setApplicationStatus(ApplicationStatus.BOOKED);
        backup();
        btoProject.bookFlat(flatType);
    }

     /**
     * Checks if the application is in a state where it can be withdrawed.
     * The application is withdrawable if its withdrawal status is unsuccessful or not applicable.
     *
     * @return {@code true} if the application can be approved, {@code false} otherwise
     */
    public boolean isWithdrawable() {
        return withdrawalStatus == WithdrawalStatus.UNSUCCESSFUL || withdrawalStatus == WithdrawalStatus.NOT_APPLICABLE;
    }

    /**
     * Checks if the application is in a state where the withdrawal can be approved.
     * The withdrawal is approvable and its withdrawal status is currently pending.
     *
     * @return {@code true} if the application can be approved, {@code false} otherwise
     */
    public boolean isWithdrawApprovable() {
        return withdrawalStatus == WithdrawalStatus.PENDING;
    }

    /**
     * Requests withdrawal for the application.
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @throws DataModelException if the application is not withdrawable.
     */
    public void requestWithdrawal() throws DataModelException {
        if(withdrawalStatus == WithdrawalStatus.PENDING){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already under pending withdrawal.");
        }

        if(withdrawalStatus == WithdrawalStatus.SUCCESSFUL){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already withdrawed.");
        }
        
        setWithdrawalStatus(WithdrawalStatus.PENDING);
    }

    /**
     * Approves or rejects a pending withdrawal request.
     * The current state is backup and can be revert by {@link #restore()}.
     *
     * @param isApproving true to approve, false to reject the withdrawal
     * @throws DataModelException if the application is not under pending withdrawal.
     */
    public void approveWithdrawal(boolean isApproving) throws DataModelException {
        if(withdrawalStatus != WithdrawalStatus.PENDING){
            throw new DataModelException("Withdrawal approval unsuccessful. The project is not under pending withdrawal.");
        }

        if(isApproving){
            backup();
            
            if(applicationStatus == ApplicationStatus.BOOKED){
                btoProject.unbookFlat(flatType);
            }

            setWithdrawalStatus(WithdrawalStatus.SUCCESSFUL);
            setApplicationStatus(ApplicationStatus.UNSUCCESSFUL);
        }
        else setWithdrawalStatus(WithdrawalStatus.UNSUCCESSFUL);    
    }

    @Override
    public String getPK() {
        return uuid;
    }

    @Override
    public void backup(){
        this.backupApplicationStatus = this.applicationStatus;
        this.backupWithdrawalStatus = this.withdrawalStatus;
        btoProject.backup();
    }

    @Override
    public void restore(){
        this.applicationStatus = this.backupApplicationStatus;
        this.withdrawalStatus = this.backupWithdrawalStatus;
        btoProject.restore();
    }
}