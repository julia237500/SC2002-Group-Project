package model;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.UUID;

import config.RegistrationStatus;
import exception.DataModelException;

public class OfficerRegistration implements DataModel{
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

    @SuppressWarnings("unused")
    private OfficerRegistration() {}

    public OfficerRegistration(BTOProject btoProject, User HDBOfficer) {
        UUID uuid = UUID.randomUUID();
        this.uuid = uuid.toString();
        this.registrationStatus = RegistrationStatus.PENDING;
        this.updated = false;
        this.createdAt = LocalDateTime.now();
        
        this.btoProject = btoProject;
        this.HDBOfficer = HDBOfficer;
    }

    public BTOProject getBTOProject() {
        return btoProject;
    }
    
    public User getHDBOfficer() {
        return HDBOfficer;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public boolean hasUnreadUpdate() {
        return updated;
    }

    public void markAsUnread(){
        updated = true;
    }

    public void markAsRead(){
        updated = false;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isApprovable(){
        return registrationStatus == RegistrationStatus.PENDING;
    }

    public void updateRegistrationStatus(boolean isApproving){
        backup();
        
        if(!isApprovable()){
            throw new DataModelException("Approve/reject unsuccessful, The registration is already approved/rejected.");
        }

        if(isApproving){
            if(btoProject.isExceedingHDBOfficerLimit()){
                throw new DataModelException("Approve unsuccessful. The project has reach maximum number of officer in-charge.");
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

    @Override
    public String toString() {
        return "%s, %s, %s".formatted(
            uuid,
            btoProject.getPK(),
            HDBOfficer.getPK()
        );
    }
}
