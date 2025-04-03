package service.interfaces;

import java.util.List;

import dto.BTOProjectDTO;
import model.BTOProject;
import model.ServiceResponse;
import model.User;

public interface BTOProjectService {
    ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO);
    ServiceResponse<List<BTOProject>> getBTOProjects();
    ServiceResponse<?> editBTOProject(User user, BTOProjectDTO btoProjectDTO, BTOProject btoProject);
    ServiceResponse<?> toggleBTOProjectVisibilty(BTOProject btoProject);
    ServiceResponse<?> deleteBTOProject(BTOProject btoProject);
}
