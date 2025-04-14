package service;

import java.time.LocalDate;
import java.util.List;

import config.ResponseStatus;
import config.UserRole;
import dto.BTOProjectDTO;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.User;
import service.interfaces.BTOProjectService;

public class DefaultBTOProjectService implements BTOProjectService{
    private DataManager dataManager;
    
    public DefaultBTOProjectService(DataManager dataManager){
        this.dataManager = dataManager;
    }

    public ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO){
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can create BTO Project");
        }

        ServiceResponse<?> response = hasActiveBTOProject(requestedUser);
        if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;

        try {
            BTOProject btoProject = dataManager.getByPK(BTOProject.class, btoProjectDTO.getName());
            if(btoProject != null){
                return new ServiceResponse<>(ResponseStatus.ERROR, "Project name must be unique.");
            }
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        try {
            BTOProject btoProject = BTOProject.fromDTO(requestedUser, btoProjectDTO);
            dataManager.save(btoProject);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project added successfully.");
    }

    private ServiceResponse<?> hasActiveBTOProject(User requestedUser){
        List<BTOProject> btoProjects = null;

        try {
            if(requestedUser.getUserRole() == UserRole.HDB_MANAGER){
                btoProjects = dataManager.getByQuery(BTOProject.class, 
                    btoProject -> btoProject.getHDBManager() == requestedUser
                );
            }
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        for(BTOProject btoProject:btoProjects){
            if(btoProject.isActive()) return new ServiceResponse<>(ResponseStatus.ERROR, "You are involving in active project: %s. This action cannot be performed.".formatted(btoProject.getName()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS);
    }

    public ServiceResponse<List<BTOProject>> getBTOProjects(){
        try {
            List<BTOProject> btoProjects = dataManager.getAll(BTOProject.class);
            return new ServiceResponse<>(ResponseStatus.SUCCESS, btoProjects);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }
    }

    public ServiceResponse<?> editBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO, BTOProject editingBTOProject){
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can edit BTO Project");
        }

        if(requestedUser != editingBTOProject.getHDBManager()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only Responsible HDB Manager can edit this BTO Project");
        }

        if(editingBTOProject.isVisible()){
            ServiceResponse<?> response = hasOverlappingVisibleProject(requestedUser, btoProjectDTO.getOpeningDate(), btoProjectDTO.getClosingDate(), editingBTOProject);
            if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;
        }

        try {
            editingBTOProject.edit(btoProjectDTO);
            dataManager.save(editingBTOProject);
        } catch (Exception e) {
            editingBTOProject.revertEdit();
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project edited successfully.");
    }

    private ServiceResponse<?> hasOverlappingVisibleProject(User requestedUser, LocalDate openingDate, LocalDate closingDate, BTOProject editingBTOProject){
        List<BTOProject> btoProjects = null;

        try {
            btoProjects = dataManager.getByQuery(BTOProject.class, 
                btoProject -> btoProject.getHDBManager() == requestedUser
            );
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        for(BTOProject btoProject:btoProjects){
            if(btoProject.isVisible() && btoProject.isOverlappingWith(openingDate, closingDate)){
                if(btoProject == editingBTOProject) continue;

                return new ServiceResponse<>(ResponseStatus.ERROR, 
                    "Application period (%s to %s) overlap with other project: %s (%s to %s). No two project can be visible at the same time".formatted(
                        openingDate, closingDate, btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                    )
                );
            } 
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS);
    }

    public ServiceResponse<?> toggleBTOProjectVisibilty(User requestedUser, BTOProject btoProject){
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can edit BTO Project");
        }

        if(requestedUser != btoProject.getHDBManager()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only Responsible HDB Manager can edit this BTO Project");
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
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can delete BTO Project");
        }

        if(requestedUser != btoProject.getHDBManager()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only Responsible HDB Manager can delete this BTO Project");
        }

        try {
            dataManager.delete(btoProject);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project deleted successfully.");
    }
}
