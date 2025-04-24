package view.interfaces;

import model.OfficerRegistration;

/**
 * Interface for displaying the details of an officer registration.
 *
 * <p>This view interface is designed to present the registration information
 * of an officer. 
 * It supports separation of concerns between the 
 * view and the business logic layers, following principles of MVC.</p>
 */
public interface OfficerRegistrationView {

    /**
     * Displays the details of the given officer registration.
     *
     * @param showingTo The role of the user who is viewing the registration.
     *                  This can be used to conditionally show or hide information.
     * @param officerRegistration The officer registration data to be displayed.
     */
    void showOfficerRegistrationDetail(OfficerRegistration officerRegistration);
}
