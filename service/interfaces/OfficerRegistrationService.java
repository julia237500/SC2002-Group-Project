package service.interfaces;

import java.util.List;

import model.BTOProject;
import model.OfficerRegistration;
import model.User;
import service.ServiceResponse;

public interface OfficerRegistrationService {
    ServiceResponse<OfficerRegistration> getOfficerRegistrationByOfficerAndBTOProject(User HDBOfficer, BTOProject btoProject);
    ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByOfficer(User requestedUser);
    ServiceResponse<List<OfficerRegistration>> getOfficerRegistrationsByBTOProject(User requestedUser, BTOProject btoProject);
    ServiceResponse<?> addOfficerRegistration(User requestedUser, BTOProject btoProject);
    ServiceResponse<?> approveOfficerRegistration(User requestedUser, OfficerRegistration officerRegistration, boolean isApproving);
}
