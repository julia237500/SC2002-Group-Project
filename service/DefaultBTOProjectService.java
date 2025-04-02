package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.ResponseStatus;
import config.UserRole;
import dto.BTOProjectDTO;
import model.BTOProject;
import model.ServiceResponse;
import model.User;
import service.interfaces.BTOProjectService;

public class DefaultBTOProjectService implements BTOProjectService{
    private final static Map<String, BTOProject> btoProjects = new HashMap<>();
    private final static Map<User, List<BTOProject>> btoProjectManager = new HashMap<>();

    private ServiceResponse<?> validate(User HDBManager, BTOProjectDTO btoProjectDTO, BTOProject editingBTOProject){
        if(HDBManager.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can alter BTO Project");
        }

        if(btoProjectManager.get(HDBManager) == null) btoProjectManager.put(HDBManager, new ArrayList<BTOProject>());
        List<BTOProject> resposibleBtoProjects = btoProjectManager.get(HDBManager);

        LocalDate openingDate = btoProjectDTO.getOpeningDate();
        LocalDate closingDate = btoProjectDTO.getClosingDate();

        for(BTOProject btoProject:resposibleBtoProjects){
            if(btoProject == editingBTOProject) continue;
            if(btoProject.isOverlappingWith(openingDate, closingDate)){
                return new ServiceResponse<>(ResponseStatus.ERROR, 
                    String.format("Application period (%s to %s) overlap with another project: %s (%s to %s)", 
                    openingDate, closingDate, btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                ));
            }
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "");
    }

    public ServiceResponse<?> addBTOProject(User HDBManager, BTOProjectDTO btoProjectDTO){
        ServiceResponse<?> validationResponse = validate(HDBManager, btoProjectDTO, null);
        if(validationResponse.getResponseStatus() != ResponseStatus.SUCCESS) return validationResponse;

        try {
            BTOProject btoProject = BTOProject.fromDTO(HDBManager, btoProjectDTO);
            btoProjects.add(btoProject);
            btoProjectManager.get(HDBManager).add(btoProject);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project added successfully.");
    }

    public ServiceResponse<List<BTOProject>> getBTOProjects(){
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "", btoProjects);
    }

    public ServiceResponse<?> editBTOProject(User user, BTOProjectDTO btoProjectDTO, BTOProject btoProject){
        ServiceResponse<?> validationResponse = validate(user, btoProjectDTO, btoProject);
        if(validationResponse.getResponseStatus() != ResponseStatus.SUCCESS) return validationResponse;

        try {
            btoProject.edit(btoProjectDTO);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project edited successfully.");
    }

    public ServiceResponse<?> toggleBTOProjectVisibilty(BTOProject btoProject){
        btoProject.toggleVisibility();

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project visibility toggled successfully.");
    }

    public ServiceResponse<?> deleteBTOProject(BTOProject btoProject){
        btoProjects.remove(btoProject);
        btoProjectManager.get(btoProject.getHDBManager()).remove(btoProject);

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project deleted successfully.");
    }
}
