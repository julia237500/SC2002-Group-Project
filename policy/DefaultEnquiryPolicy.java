package policy;

import config.UserRole;
import model.BTOProject;
import model.Enquiry;
import model.User;
import policy.interfaces.EnquiryPolicy;


/**
 * Default implementation of the {@link EnquiryPolicy} interface.
 * <p>
 * Defines the authorization logic for various operations related to Enquiries.
 */
public class DefaultEnquiryPolicy implements EnquiryPolicy{

    /**
     * Checks if the given user can view all enquiries in the system.
     * Only users with the role {@code HDB_MANAGER} are allowed.
     *
     * @param requestedUser the user making the request
     * @return a {@link PolicyResponse} indicating whether access is granted
     */
    @Override
    public PolicyResponse canViewAllEnquiries(User requestedUser) {
        if(requestedUser.getUserRole() == UserRole.HDB_MANAGER) return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only HDB Manager can view all enquiries");
    }

    /**
     * Checks if the user can view enquiries associated with a specific BTO project.
     * Allowed if the user is the HDB Manager handling this project.
     *
     * @param requestedUser the user making the request
     * @param btoProject the project whose enquiries are being accessed
     * @return a {@link PolicyResponse} indicating access permission
     */
    @Override
    public PolicyResponse canViewEnquiriesByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() == UserRole.HDB_MANAGER) return PolicyResponse.allow();
        if(btoProject.isHandlingBy(requestedUser)) return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only HDB Officer handling this project can view enquiries.");
    }

    /**
     * Determines whether a user can view their own submitted enquiries.
     * Allowed for applicants and HDB officers.
     *
     * @param requestedUser the user making the request
     * @return a {@link PolicyResponse} with the result
     */
    @Override
    public PolicyResponse canViewEnquiriesByUser(User requestedUser) {
        if(requestedUser.getUserRole() == UserRole.APPLICANT || requestedUser.getUserRole() == UserRole.HDB_OFFICER)
            return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only Applicant or HDB Officer can view their enquiries.");
    }

    /**
     * Checks if a user can create an enquiry for a BTO project.
     * Allowed for applicants or HDB officers not handling the project.
     *
     * @param requestedUser the user making the request
     * @param btoProject the BTO project the enquiry is for
     * @return a {@link PolicyResponse} with access information
     */
    @Override
    public PolicyResponse canCreateEnquiry(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() == UserRole.APPLICANT ||
            (requestedUser.getUserRole() == UserRole.HDB_OFFICER && !btoProject.isHandlingBy(requestedUser))){
                return PolicyResponse.allow();
        }

        return PolicyResponse.deny("Access denied. Only Applicant or HDB Officer not handling this project can create enquiry.");
    }

    /**
     * Checks if a user can edit a specific enquiry.
     * Only the original enquirer can edit, and only if the enquiry is still alterable.
     *
     * @param requestedUser the user making the request
     * @param enquiry the enquiry to edit
     * @return a {@link PolicyResponse} indicating the result
     */
    @Override
    public PolicyResponse canEditEnquiry(User requestedUser, Enquiry enquiry) {
        if(enquiry.getEnquirer() != requestedUser){
            return PolicyResponse.deny("Access denied. Only the enquirer can edit this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry edited unsuccessful. Enquiry cannot be edited.");

        return PolicyResponse.allow();
    }

    /**
     * Checks if a user can delete a specific enquiry.
     * Only allowed if the user is the original enquirer and the enquiry is still alterable.
     *
     * @param requestedUser the user requesting the action
     * @param enquiry the enquiry to delete
     * @return a {@link PolicyResponse} indicating whether deletion is permitted
     */
    @Override
    public PolicyResponse canDeleteEnquiry(User requestedUser, Enquiry enquiry) {
        if(enquiry.getEnquirer() != requestedUser){
            return PolicyResponse.deny("Access denied. Only the enquirer can delete this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry deleted unsuccessful. Enquiry cannot be deleted.");

        return PolicyResponse.allow();
    }

    /**
     * Checks if a user can reply to a specific enquiry.
     * Only HDB staff handling the project can reply and only if the enquiry is alterable.
     *
     * @param requestedUser the user attempting to reply
     * @param enquiry the enquiry to reply to
     * @return a {@link PolicyResponse} indicating permission
     */
    @Override
    public PolicyResponse canReplyEnquiry(User requestedUser, Enquiry enquiry) {
        if(!enquiry.getBTOProject().isHandlingBy(requestedUser)){
            return PolicyResponse.deny("Access denied. Only HDB Manager/Officer handling this project can reply to this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry replied unsuccessful. Enquiry cannot be replied.");

        return PolicyResponse.allow();
    }
}
