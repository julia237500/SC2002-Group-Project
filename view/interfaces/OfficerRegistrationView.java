package view.interfaces;

import config.UserRole;
import model.OfficerRegistration;

public interface OfficerRegistrationView {
    void showOfficerRegistrationDetail(UserRole showingTo, OfficerRegistration officerRegistration);
}
