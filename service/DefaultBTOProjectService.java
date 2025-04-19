package service;

import java.time.LocalDate;
import java.util.List;

import config.ResponseStatus;
import dto.BTOProjectDTO;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.User;
import policy.PolicyResponse;
import policy.interfaces.BTOProjectPolicy;
import service.interfaces.BTOProjectService;

public class DefaultBTOProjectService implements BTOProjectService{
    private final DataManager dataManager;
    private final BTOProjectPolicy btoProjectPolicy;
    
    public DefaultBTOProjectService(DataManager dataManager, BTOProjectPolicy btoProjectPolicy){
        this.dataManager = dataManager;
        this.btoProjectPolicy = btoProjectPolicy;
    }

    public ServiceResponse<List<BTOProject>> getAllBTOProjects(User requestedUser){
        PolicyResponse policyResponse = btoProjectPolicy.canViewAllBTOProjects(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<BTOProject> btoProjects = dataManager.getAll(BTOProject.class, BTOProject.DEFAULT_COMPARATOR);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, btoProjects);
    }

    public ServiceResponse<List<BTOProject>> getBTOProjectsHandledByUser(User requestedUser){
        PolicyResponse policyResponse = btoProjectPolicy.canViewBTOProjectsHandledByUser(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<BTOProject> btoProjects = dataManager.getByQuery(BTOProject.class, 
            btoProject -> btoProject.isHandlingBy(requestedUser),
            BTOProject.DEFAULT_COMPARATOR
        );

        return new ServiceResponse<>(ResponseStatus.SUCCESS, btoProjects);
    }

    public ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO){
        PolicyResponse policyResponse = btoProjectPolicy.canCreateBTOProject(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        BTOProject btoProject = dataManager.getByPK(BTOProject.class, btoProjectDTO.getName());
        if(btoProject != null){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Project name must be unique.");
        }

        try {
            btoProject = BTOProject.fromDTO(requestedUser, btoProjectDTO);
            dataManager.save(btoProject);
        } catch (DataModelException e) { 
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project added successfully.");
    }

    public ServiceResponse<?> editBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO, BTOProject editingBTOProject){
        PolicyResponse policyResponse = btoProjectPolicy.canEditBTOProject(requestedUser, editingBTOProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        if(editingBTOProject.isVisible()){
            ServiceResponse<?> response = hasOverlappingVisibleProject(requestedUser, btoProjectDTO.getOpeningDate(), btoProjectDTO.getClosingDate(), editingBTOProject);
            if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;
        }

        try {
            editingBTOProject.edit(btoProjectDTO);
            dataManager.save(editingBTOProject);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            editingBTOProject.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project edited successfully.");
    }

    private ServiceResponse<?> hasOverlappingVisibleProject(User requestedUser, LocalDate openingDate, LocalDate closingDate, BTOProject editingBTOProject){
        List<BTOProject> btoProjects = dataManager.getByQueries(BTOProject.class, List.of(
            btoProject -> btoProject.getHDBManager() == requestedUser,
            btoProject -> btoProject.isVisible() && btoProject != editingBTOProject,
            btoProject -> btoProject.isOverlappingWith(openingDate, closingDate)
        ));

        if(!btoProjects.isEmpty()){
            BTOProject btoProject = btoProjects.get(0);
            return new ServiceResponse<>(ResponseStatus.ERROR, "Application period (%s to %s) overlap with other project: %s (%s to %s). No two project can be visible at the same time".formatted(
                openingDate, closingDate, btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
            ));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS);
    }

    public ServiceResponse<?> toggleBTOProjectVisibilty(User requestedUser, BTOProject btoProject){
        PolicyResponse policyResponse = btoProjectPolicy.canToggleBTOProjectVisibility(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        if(!btoProject.isVisible()){
            ServiceResponse<?> response = hasOverlappingVisibleProject(requestedUser, btoProject.getOpeningDate(), btoProject.getClosingDate(), btoProject);
            if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;
        }
        
        btoProject.toggleVisibility();
        
        try {
            dataManager.save(btoProject);
        } catch (Exception e) {
            btoProject.toggleVisibility();
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project visibility toggled successfully.");
    }

    public ServiceResponse<?> deleteBTOProject(User requestedUser, BTOProject btoProject){
        PolicyResponse policyResponse = btoProjectPolicy.canDeleteBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            dataManager.delete(btoProject);
        } catch (DataSavingException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project deleted successfully.");
    }
}
