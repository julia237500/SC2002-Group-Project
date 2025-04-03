package repository.interfaces;

import java.util.List;

import model.BTOProject;
import model.User;

public interface BTOProjectRepository extends DeletableRepository<BTOProject>{
    BTOProject getByName(String name);
    List<BTOProject> getByHDBManager(User HDBManager);
    List<BTOProject> getByHDBOfficer(User HDBOfficer);
}
