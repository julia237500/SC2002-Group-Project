package service.interfaces;

import java.util.List;

import dto.BTOProjectDTO;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

/**
 * Service interface for managing BTO projects.
 * Provides operations for creating, modifying, and managing the visibility of BTO projects.
 */
public interface BTOProjectService {

    /**
     * Creates a new BTO project after validation.
     * 
     * @param requestedUser the user requesting creation (must be HDB_MANAGER)
     * @param btoProjectDTO the project data transfer object containing project details (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if created
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Project name already exists
     *           - Validation fails
     */
    ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO);

    /**
     * Retrieves all BTO projects in the system.
     * 
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<BTOProject> of all projects
     *         - ERROR status with message if retrieval fails
     */
    ServiceResponse<List<BTOProject>> getBTOProjects();

    /**
     * Modifies an existing BTO project.
     * 
     * @param user the user requesting the edit (must be HDB_MANAGER and responsible manager)
     * @param btoProjectDTO the updated project data (cannot be null)
     * @param btoProject the project to be modified (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if updated
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Project cannot be modified
     *           - Validation fails
     */
    ServiceResponse<?> editBTOProject(User user, BTOProjectDTO btoProjectDTO, BTOProject btoProject);

    /**
     * Toggles the visibility status of a BTO project.
     * 
     * @param requestedUser the user requesting the change (must be HDB_MANAGER and responsible manager)
     * @param btoProject the project to modify (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if toggled
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Project cannot be made visible (e.g., overlaps with other projects)
     *           - Operation fails
     */
    ServiceResponse<?> toggleBTOProjectVisibilty(User requestedUser, BTOProject btoProject);

    /**
     * Deletes a BTO project.
     * 
     * @param requestedUser the user requesting deletion (must be HDB_MANAGER and responsible manager)
     * @param btoProject the project to delete (cannot be null)
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if deleted
     *         - ERROR status with message if:
     *           - User not authorized
     *           - Project cannot be deleted
     *           - Deletion fails
     */
    ServiceResponse<?> deleteBTOProject(User requestedUser, BTOProject btoProject);
}