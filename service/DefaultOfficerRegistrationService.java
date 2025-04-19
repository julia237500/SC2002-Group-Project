package service;

import java.util.List;

import config.ResponseStatus;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import policy.PolicyResponse;
import policy.interfaces.OfficerRegistrationPolicy;
import service.interfaces.OfficerRegistrationService;

public class DefaultOfficerRegistrationService implements OfficerRegistrationService{
    private final DataManager dataManager;
    private final OfficerRegistrationPolicy officerRegistrationPolicy;

    public DefaultOfficerRegistrationService(DataManager dataManager, OfficerRegistrationPolicy officerRegistrationPolicy) {
        this.dataManager = dataManager;
        this.officerRegistrationPolicy = officerRegistrationPolicy;
    }

    @Override
    public ServiceResponse<OfficerRegistration> getOfficerRegistrationByOfficerAndBTOProject(User HDBOfficer, BTOProject btoProject) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationByUserAndBTOProject(HDBOfficer, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

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
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationsByOfficer(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
        final PolicyResponse policyResponse = officerRegistrationPolicy.canViewOfficerRegistrationsByBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class,
            registration -> registration.getBTOProject() == btoProject, 
            OfficerRegistration.SORT_BY_CREATED_AT_DESC
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, officerRegistrations);
    }
    
    @Override
    public ServiceResponse<?> addOfficerRegistration(User requestedUser, BTOProject btoProject) {
        final PolicyResponse policyResponse = officerRegistrationPolicy.canCreateOfficerRegistration(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
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
        final PolicyResponse policyResponse = officerRegistrationPolicy.canApproveOfficerRegistration(requestedUser, officerRegistration, isApproving);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            officerRegistration.updateRegistrationStatus(isApproving);
            dataManager.save(officerRegistration);
            officerRegistration.getBTOProject().addHDBOfficer(officerRegistration.getHDBOfficer());
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            officerRegistration.restore();;
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "%s registration successful.".formatted(isApproving ? "Approve" : "Reject"));
    }
}
