package service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.ResponseStatus;
import model.BTOApplication;
import model.BTOProject;
import model.ServiceResponse;
import model.User;
import service.interfaces.BTOApplicationService;

public class DefaultBTOApplicationService implements BTOApplicationService{
    private final List<BTOApplication> applications = new ArrayList<>();
    private final Map<User, BTOApplication> userToApplicationMap = new HashMap<>();
    
    private ServiceResponse<?> validate(User applicant, BTOProject btoProject, FlatType flatType){

        // Check if user is HDBOfficer (dont care now if HDBOfficer manage the project) or Applicant

        // Check if user have not apply before using the map
        if (userToApplicationMap.containsKey(applicant)) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "User has already applied for a BTO project!");
        }
        // Check if user can apply for the flat type
        if (!btoProject.isEligibleFlatType(flatType, applicant)) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "User is not eligible for this flat type!")
        }
        // Check if application the number of flat type is >0 using btoProject.getFlatNumByType(flatType)
        // If not return the ServiceResponse with ResponseStatus.ERROR and suitable message
        if (btoProject.getFlatNumByType(flatType) <= 0) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "No flats available for the selected flat type.");
        }
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "");
    }

    @Override
    public ServiceResponse<?> apply(User applicant, BTOProject btoProject, FlatType flatType) {
        ServiceResponse<?> validation = validate(applicant, btoProject, flatType);
        if(validation.getResponseStatus() != ResponseStatus.SUCCESS){
            return validation;
        }

        // Create new BTOApplication
        BTOApplication application = new BTOApplication(applicant, btoProject, flatType);

        // Add the application to the list and map
        applications.add(application);
        userToApplicationMap.put(applicant, application);

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Application submitted successfully. Kindly wait for approval");
    }
}
