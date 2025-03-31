package service.interfaces;

import config.FlatType;
import model.BTOProject;
import model.ServiceResponse;
import model.User;

public interface BTOApplicationService {
    ServiceResponse<?> apply(User applicant, BTOProject btoProject, FlatType flatType);
}
