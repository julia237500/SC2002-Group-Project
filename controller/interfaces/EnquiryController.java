package controller.interfaces;

import model.BTOProject;
import model.Enquiry;

/**
 * Interface for handling operations related to user enquiries in the system.
 * Provides methods for creating, editing, replying to, deleting, and displaying enquiries.
 */
public interface EnquiryController {

    /**
     * Creates and adds a new enquiry for a specified BTO project.
     *
     * @param btoProject the {@link BTOProject} related to the enquiry.
     */
    void addEnquiry(BTOProject btoProject);

    /**
     * Edits an existing enquiry.
     *
     * @param enquiry the {@link Enquiry} to be edited.
     */
    void editEnquiry(Enquiry enquiry);

    /**
     * Deletes an existing enquiry from the system.
     *
     * @param enquiry the {@link Enquiry} to be deleted.
     */
    void deleteEnquiry(Enquiry enquiry);

    /**
     * Allows an officer or system user to reply to an enquiry.
     *
     * @param enquiry the {@link Enquiry} to which a reply will be added.
     */
    void replyEnquiry(Enquiry enquiry);

    /**
     * Displays all enquiries in the system.
     */
    void showAllEnquiries();

    void showEnquiriesByUser();

    /**
     * Displays all enquiries associated with a specific BTO project.
     *
     * @param btoProject the {@link BTOProject} for which enquiries should be shown.
     */
    void showEnquiriesByBTOProject(BTOProject btoProject);

    /**
     * Displays a brief overview of the specified enquiry.
     *
     * @param enquiry the {@link Enquiry} to be displayed.
     */
    void showEnquiry(Enquiry enquiry);

    /**
     * Displays detailed information about the specified enquiry
     *
     * @param enquiry the {@link Enquiry} whose details should be shown.
     */
    void showEnquiryDetail(Enquiry enquiry);
}
