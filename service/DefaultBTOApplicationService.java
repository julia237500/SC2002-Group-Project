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
        // Check if user can apply for the flat type
        // Check if application the number of flat type is >0 using btoProject.getFlatNumByType(flatType)
        // If not return the ServiceResponse with ResponseStatus.ERROR and suitable message
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "");
    }

    @Override
    public ServiceResponse<?> apply(User applicant, BTOProject btoProject, FlatType flatType) {
        ServiceResponse<?> validation = validate(applicant, btoProject, flatType);
        if(validation.getResponseStatus() != ResponseStatus.SUCCESS){
            return validation;
        }

        // Create new BTOApplication
        // Add the application to the list and map

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Application submitted successfully. Kindly wait for approval");
    }
}
