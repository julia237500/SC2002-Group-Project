package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.ApplicationStatus;
import config.FlatType;
import config.WithdrawalStatus;
import exception.DataModelException;

public class Application implements DataModel{
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

    @SuppressWarnings("unused")
    private Application(){}

    public Application(User applicant, BTOProject btoProject, FlatType flatType){
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

    public User getApplicant() {
        return applicant;
    }

    public BTOProject getBTOProject() {
        return btoProject;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    private void setApplicationStatus(ApplicationStatus applicationStatus) {
        this.backupApplicationStatus = this.applicationStatus;
        this.applicationStatus = applicationStatus;
    }

    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    private void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.backupWithdrawalStatus = this.withdrawalStatus;
        this.withdrawalStatus = withdrawalStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getFlatNum() {
        return btoProject.getFlatNum(flatType);
    }

    private void checkFlatTypeEligibility(User applicant, BTOProject btoProject, FlatType flatType) {
        if(!btoProject.hasAvailableFlats(flatType)){
            throw new DataModelException("%s is not available for the project %s".formatted(flatType.getStoredString(), btoProject.getName()));
        }

        if(!flatType.isEligible(applicant)){
            throw new DataModelException("You are not eligible to apply for %s".formatted(flatType.getStoredString()));
        }
    }

    private boolean isUpdatable() {
        return withdrawalStatus == WithdrawalStatus.NOT_APPLICABLE || withdrawalStatus == WithdrawalStatus.UNSUCCESSFUL;
    }

    public boolean isApprovable() {
        return isUpdatable() && applicationStatus == ApplicationStatus.PENDING;
    }

    public void approveApplication(boolean isApproving) {
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

    public boolean isBookable() {
        return isUpdatable() && applicationStatus == ApplicationStatus.SUCCESSFUL;
    }

    public void bookApplication(){
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
        btoProject.bookFlat(flatType);
    }

    public boolean isWithdrawable() {
        return withdrawalStatus == WithdrawalStatus.UNSUCCESSFUL || withdrawalStatus == WithdrawalStatus.NOT_APPLICABLE;
    }

    public void requestWithdrawal() {
        if(withdrawalStatus == WithdrawalStatus.PENDING){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already under pending withdrawal.");
        }

        if(withdrawalStatus == WithdrawalStatus.SUCCESSFUL){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already withdrawed.");
        }
        
        setWithdrawalStatus(WithdrawalStatus.PENDING);
    }

    public void approveWithdrawal(boolean isApproving) {
        if(withdrawalStatus != WithdrawalStatus.PENDING){
            throw new DataModelException("Withdrawal approval unsuccessful. The project is not under pending withdrawal.");
        }

        if(isApproving){
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
    