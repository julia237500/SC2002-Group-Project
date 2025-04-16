package service.interfaces;

import java.util.List;

import dto.BTOProjectDTO;
import model.BTOProject;
import model.User;
import service.ServiceResponse;

public interface BTOProjectService {
    ServiceResponse<?> addBTOProject(User requestedUser, BTOProjectDTO btoProjectDTO);
    ServiceResponse<List<BTOProject>> getBTOProjects();
    ServiceResponse<?> editBTOProject(User user, BTOProjectDTO btoProjectDTO, BTOProject btoProject);
    ServiceResponse<?> toggleBTOProjectVisibilty(User requestedUser, BTOProject btoProject);
    ServiceResponse<?> deleteBTOProject(User requestedUser, BTOProject btoProject);
}


