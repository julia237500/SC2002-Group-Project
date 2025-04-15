package model;

import java.time.LocalDateTime;
import java.util.UUID;

import config.ApplicationStatus;
import config.FlatType;
import config.WithdrawalStatus;
import exception.DataModelException;

public class Application implements DataModel{
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

    @Override
    public String getPK() {
        return uuid;
    }

    public User getApplicant() {
        return applicant;
    }

    public BTOProject getBtoProject() {
        return btoProject;
    }

    public FlatType getFlatType() {
        return flatType;
    }

    public ApplicationStatus getApplicationStatus() {
        return applicationStatus;
    }

    public WithdrawalStatus getWithdrawalStatus() {
        return withdrawalStatus;
    }

    private void setWithdrawalStatus(WithdrawalStatus withdrawalStatus) {
        this.backupWithdrawalStatus = this.withdrawalStatus;
        this.withdrawalStatus = withdrawalStatus;
    }

    public void revertWithdrawalStatus() {
        this.withdrawalStatus = this.backupWithdrawalStatus;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    private void checkFlatTypeEligibility(User applicant, BTOProject btoProject, FlatType flatType) {
        if(!btoProject.hasAvailableFlats(flatType)){
            throw new DataModelException("%s is not available for the project %s".formatted(flatType.getStoredString(), btoProject.getName()));
        }

        if(!flatType.isEligible(applicant)){
            throw new DataModelException("You are not eligible to apply for %s".formatted(flatType.getStoredString()));
        }
    }

    public void requestWithdrawal() {
        if(withdrawalStatus == WithdrawalStatus.PENDING){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already under pending withdrawal.");
        }

        if(withdrawalStatus == WithdrawalStatus.SUCCESSFUL){
            throw new DataModelException("Withdrawal requested unsuccessful. The project is already under pending withdrawal.");
        }
        
        setWithdrawalStatus(WithdrawalStatus.PENDING);
    }
}
    