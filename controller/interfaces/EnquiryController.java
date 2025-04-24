package controller.interfaces;

import model.BTOProject;
import model.Enquiry;
import model.User;

/**
 * A controller that processes {@link Enquiry} in accordance with the MVC architecture.
 * Entry point to actions such as create, edit, delete, and others.
 *
 * @implNote This controller should remain lightweight, with the sole responsibility of 
 * coordinating interactions between the service layer, view layer, and other components.
 * All business logic should be delegated to other components.
 * 
 * @see Enquiry
 */
public interface EnquiryController {
    /**
     * Displays all {@link Enquiry}
     * 
     * @see Enquiry
     */
    void showAllEnquiries();

    /**
     * Displays list of {@link Enquiry} that is created by logged-in {@link User}.
     * 
     * @see Enquiry
     * @see User
     */
    void showEnquiriesByUser();

    /**
     * Displays list of {@link Enquiry} that have been submitted for a specific {@link BTOProject}.
     *
     * @param btoProject the {@code BTOProject} used to search for the related {@code Enquiry}.
     * 
     * @see Enquiry
     * @see BTOProject
     */
    void showEnquiriesByBTOProject(BTOProject btoProject);

    /**
     * Displays the details of a {@link Enquiry} and its related action.
     *
     * @param enquiry the {@code Enquiry} to display
     * 
     * @see Enquiry
     */
    void showEnquiry(Enquiry enquiry);

    /**
     * Prompts and handles the creation process for a new {@link Enquiry} for a specific {@link BTOProject}.
     * 
     * @see Enquiry
     * @see BTOProject
     */
    void addEnquiry(BTOProject btoProject);

    /**
     * Prompts and handles the editing process for a specific {@link Enquiry}.
     *
     * @param enquiry the {@code Enquiry} to edit.
     * 
     * @see Enquiry
     */
    void editEnquiry(Enquiry enquiry);

    /**
     * Deletes a specific {@link Enquiry}.
     *
     * @param enquiry the {@code Enquiry} to delete.
     * 
     * @see Enquiry
     */
    void deleteEnquiry(Enquiry enquiry);

    /**
     * Prompts and handles the replying process for a specific {@link Enquiry}.
     *
     * @param enquiry the {@code Enquiry} to reply to.
     * 
     * @see Enquiry
     */
    void replyEnquiry(Enquiry enquiry);
}
