package service.interfaces;

import java.util.List;

import model.BTOProject;
import model.Enquiry;
import model.User;
import service.ServiceResponse;

/**
 * Service interface for managing enquiries about BTO projects.
 * Provides operations for creating, modifying, and retrieving enquiries,
 * as well as replying to enquiries.
 */
public interface EnquiryService {
    /**
     * Creates a new enquiry about a BTO project.
     * 
     * @param requestedUser the user submitting the enquiry (must be APPLICANT or HDB_OFFICER)
     * @param btoProject the project being enquired about (cannot be null)
     * @param subject the enquiry subject line (cannot be null or empty)
     * @param enquiryString the enquiry content (cannot be null or empty)
     * @return ServiceResponse with:
     *         - SUCCESS status and confirmation message if created
     *         - ERROR status with message if validation fails
     */
    ServiceResponse<?> addEnquiry(User requestedUser, BTOProject btoProject, String subject, String enquiryString);

    /**
     * Modifies an existing enquiry.
     * 
     * @param requestedUser the user requesting the edit (must be original enquirer)
     * @param enquiry the enquiry to modify (cannot be null)
     * @param subject the new subject line (cannot be null or empty)
     * @param enquiryString the new enquiry content (cannot be null or empty)
     * @return ServiceResponse with:
     *         - SUCCESS status if updated
     *         - ERROR status if:
     *           - User is not original enquirer
     *           - Enquiry has been replied to
     *           - Validation fails
     */
    ServiceResponse<?> editEnquiry(User requestedUser, Enquiry enquiry, String subject, String enquiryString);

    /**
     * Deletes an existing enquiry.
     * 
     * @param requestedUser the user requesting deletion (must be original enquirer)
     * @param enquiry the enquiry to delete (cannot be null)
     * @return ServiceResponse with:
     *         - SUCCESS status if deleted
     *         - ERROR status if:
     *           - User is not original enquirer
     *           - Enquiry has been replied to
     *           - Deletion fails
     */
    ServiceResponse<?> deleteEnquiry(User requestedUser, Enquiry enquiry);

    /**
     * Adds a reply to an enquiry.
     * 
     * @param requestedUser the user replying (must be handling officer/manager)
     * @param enquiry the enquiry to reply to (cannot be null)
     * @param replyString the reply content (cannot be null or empty)
     * @return ServiceResponse with:
     *         - SUCCESS status if replied
     *         - ERROR status if:
     *           - User not authorized
     *           - Enquiry has already been replied to 
     *           - Validation fails
     */
    ServiceResponse<?> replyEnquiry(User requestedUser, Enquiry enquiry, String replyString);

     /**
     * Retrieves all enquiries in the system (HDB Manager only).
     * 
     * @param requestedUser the user making the request
     * @return ServiceResponse with:
     *         - SUCCESS status and List<Enquiry> if authorized
     *         - ERROR status if access denied
     */
    ServiceResponse<List<Enquiry>> getAllEnquiries(User requestedUser);

    /**
     * Retrieves enquiries submitted by a specific user (owner only).
     * 
     * @param requestedUser the user whose enquiries to retrieve
     * @return ServiceResponse with:
     *         - SUCCESS status and List<Enquiry> if authorized
     *         - ERROR status if access denied
     */
    ServiceResponse<List<Enquiry>> getEnquiriesByUser(User requestedUser);

    /**
     * Retrieves enquiries for a specific BTO project (handlers only).
     * 
     * @param requestedUser the user making the request
     * @param btoProject the project to filter by (cannot be null)
     * @return ServiceResponse with:
     *         - SUCCESS status and List<Enquiry> if authorized
     *         - ERROR status if access denied
     */
    ServiceResponse<List<Enquiry>> getEnquiriesByBTOProject(User requestedUser, BTOProject btoProject);
}