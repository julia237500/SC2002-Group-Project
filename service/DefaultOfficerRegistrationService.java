package service;

import java.util.List;

import config.RegistrationStatus;
import config.ResponseStatus;
import config.UserRole;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import service.interfaces.OfficerRegistrationService;

public class DefaultOfficerRegistrationService implements OfficerRegistrationService{
    private DataManager dataManager;

    public DefaultOfficerRegistrationService(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    public ServiceResponse<OfficerRegistration> getOfficerRegistrationByOfficerAndBTOProject(User HDBOfficer, BTOProject btoProject) {
        List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getBTOProject() == btoProject,
            registration -> registration.getHDBOfficer() == HDBOfficer
        )); 

        OfficerRegistration officerRegistration = null;
        if(officerRegistrations.size() > 0) officerRegistration = officerRegistrations.get(0);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistration);
    }

    @Override
    public ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByOfficer(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer can performed this action.");
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getHDBOfficer() == requestedUser, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }

    @Override
    public ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager can performed this action.");
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getBTOProject() == btoProject, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }
    
    // To do: 
    // 1. Applicant check logic
    @Override
    public ServiceResponse<?> addOfficerRegistration(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer can perform this action.");
        }

        ServiceResponse<OfficerRegistration> serviceResponse = getOfficerRegistrationByOfficerAndBTOProject(requestedUser, btoProject);
        if(serviceResponse.getData() != null){
            return new ServiceResponse<>(ResponseStatus.ERROR, "You have registered for the same project before.");
        }

        if(btoProject.isExceedingHDBOfficerLimit()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "The project has reach maximum number of officer in-charge.");
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
            registration -> registration.getHDBOfficer() == requestedUser,
            registration -> registration.getRegistrationStatus() != RegistrationStatus.UNSUCCESSFUL,
            registration -> registration.getBTOProject().isOverlappingWith(btoProject)
        )); 
        if(officerRegistrations.size() > 0){
            BTOProject otherBTOProject = officerRegistrations.get(0).getBTOProject();
            return new ServiceResponse<>(ResponseStatus.ERROR, """
                You have Pending/Successful registration under project with overlapping application period:
                Previous Registered Project: %s (%s - %s)
                Registering Project: %s (%s - %s)
                """.formatted(
                    otherBTOProject.getName(), otherBTOProject.getOpeningDate(), otherBTOProject.getClosingDate(), 
                    btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                ));
        }

        try {
            OfficerRegistration officerRegistration = new OfficerRegistration(btoProject, requestedUser);
            dataManager.save(officerRegistration);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Registration successful. Kindly wait for approval from HDB Manager.");
    }

    @Override
    public ServiceResponse<?> approveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager can perform this action.");
        }

        if(officerRegistration.getBTOProject().getHDBManager() != requestedUser){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Responsible HDB Manager can perform this action.");
        }

        try {
            officerRegistration.updateRegistrationStatus(isApproving);
            dataManager.save(officerRegistration);
            officerRegistration.getBTOProject().addHDBOfficer(officerRegistration.getHDBOfficer());
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            officerRegistration.revertRegistrationStatus();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        if(isApproving) return new ServiceResponse<>(ResponseStatus.SUCCESS, "Approve registration successful.");
        else return new ServiceResponse<>(ResponseStatus.SUCCESS, "Reject registration successful.");
    }

    @Override
    public ServiceResponse<?> markOfficerRegistrationAsRead(OfficerRegistration officerRegistration) {
        if(!officerRegistration.hasUnreadUpdate()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Registration don't have unread update.");
        }

        officerRegistration.markAsRead();
        try {
            dataManager.save(officerRegistration);
        } catch (Exception e) {
            officerRegistration.markAsUnread();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Mark as read success.");
    }
}
