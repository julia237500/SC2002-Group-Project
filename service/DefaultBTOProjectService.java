package service;

import java.time.LocalDate;
import java.util.List;

import config.ResponseStatus;
import config.UserRole;
import dto.BTOProjectDTO;
import model.BTOProject;
import model.ServiceResponse;
import model.User;
import repository.interfaces.BTOProjectRepository;
import service.interfaces.BTOProjectService;

public class DefaultBTOProjectService implements BTOProjectService{
    private BTOProjectRepository btoProjectRepository;
    
    public DefaultBTOProjectService(BTOProjectRepository btoProjectRepository){
        this.btoProjectRepository = btoProjectRepository;
    }

    public ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO){
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can create BTO Project");
        }

        ServiceResponse<?> response = hasActiveBTOProject(requestedUser);
        if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;

        try {
            BTOProject btoProject = btoProjectRepository.getByName(btoProjectDTO.getName());
            if(btoProject != null){
                return new ServiceResponse<>(ResponseStatus.ERROR, "Project name must be unique.");
            }
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        try {
            BTOProject btoProject = BTOProject.fromDTO(requestedUser, btoProjectDTO);
            btoProjectRepository.save(btoProject);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project added successfully.");
    }

    private ServiceResponse<?> hasActiveBTOProject(User requestedUser){
        List<BTOProject> btoProjects = null;

        try {
            if(requestedUser.getUserRole() == UserRole.HDB_MANAGER){
                btoProjects = btoProjectRepository.getByHDBManager(requestedUser);
            }
            else{
                btoProjects = btoProjectRepository.getByHDBOfficer(requestedUser);
            }
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        for(BTOProject btoProject:btoProjects){
            if(btoProject.isActive()) return new ServiceResponse<>(ResponseStatus.ERROR, "You are involving in active project: %s. This action cannot be performed.");
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS);
    }

    public ServiceResponse<List<BTOProject>> getBTOProjects(){
        try {
            List<BTOProject> btoProjects = btoProjectRepository.getAll();
            return new ServiceResponse<>(ResponseStatus.SUCCESS, btoProjects);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }
    }

    public ServiceResponse<?> editBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO, BTOProject editingBTOProject){
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access Denied. Only HDB Manager can create BTO Project");
        }

        ServiceResponse<?> response = hassOverlappingActiveProject(requestedUser, btoProjectDTO, editingBTOProject);
        if(response.getResponseStatus() != ResponseStatus.SUCCESS) return response;

        try {
            editingBTOProject.edit(btoProjectDTO);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }
        
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project edited successfully.");
    }

    private ServiceResponse<?> hassOverlappingActiveProject(User requestedUser, BTOProjectDTO btoProjectDTO, BTOProject editingBTOProject){
        if(!editingBTOProject.isVisible()) return new ServiceResponse<>(ResponseStatus.SUCCESS);

        LocalDate openingDate = btoProjectDTO.getOpeningDate();
        LocalDate closingDate = btoProjectDTO.getClosingDate();
        
        List<BTOProject> btoProjects = null;

        try {
            btoProjects = btoProjectRepository.getByHDBManager(requestedUser);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        for(BTOProject btoProject:btoProjects){
            if(btoProject.isActive() && btoProject.isOverlappingWith(openingDate, closingDate)){
                if(btoProject == editingBTOProject) break;

                return new ServiceResponse<>(ResponseStatus.ERROR, 
                    "Application period (%s to %s) overlap with active project: %s (%s to %s)".formatted(
                        openingDate, closingDate, btoProject.getName(), btoProject.getOpeningDate(), btoProject.getClosingDate()
                    )
                );
            } 
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS);
    }

    public ServiceResponse<?> toggleBTOProjectVisibilty(BTOProject btoProject){
        btoProject.toggleVisibility();

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project visibility toggled successfully.");
    }

    public ServiceResponse<?> deleteBTOProject(BTOProject btoProject){
        try {
            btoProjectRepository.delete(btoProject);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "BTO Project deleted successfully.");
    }
}
