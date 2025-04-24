package service;

import java.util.List;

import config.ResponseStatus;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.Enquiry;
import model.User;
import policy.PolicyResponse;
import policy.interfaces.EnquiryPolicy;
import service.interfaces.EnquiryService;

/**
 * Default implementation of {@link EnquiryService} that manages enquiry lifecycle operations
 * including creation, retrieval, modification, and replies to enquiries about BTO projects.
 * Enforces role-based access control for all operations.
 */
public class DefaultEnquiryService implements EnquiryService{
    private final DataManager dataManager;
    private final EnquiryPolicy enquiryPolicy;
    
    /**
     * Constructs a DefaultEnquiryService with the specified data manager.
     * 
     * @param dataManager the data manager used for persistence operations 
     */
    public DefaultEnquiryService(DataManager dataManager, EnquiryPolicy enquiryPolicy){
        this.dataManager = dataManager;
        this.enquiryPolicy = enquiryPolicy;
    }

    /**
     * Retrieves all enquiries in the system (only HDB manager can perform this action)
     * Results are sorted by creation date in reversed order (Enquiry.SORT_BY_CREATED_AT_DESC)
     * @see model.Enquiry
     * 
     * @param requestedUser the user making the request
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<Enquiry> if authorized
     *         - ERROR status with message if access denied
     */
    @Override
    public ServiceResponse<List<Enquiry>> getAllEnquiries(User requestedUser) {
        final PolicyResponse policyResponse = enquiryPolicy.canViewAllEnquiries(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Enquiry> enquiries = dataManager.getAll(Enquiry.class, Enquiry.SORT_BY_CREATED_AT_DESC);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

    /**
     * Retrieves enquiries submitted by the specified user (Applicant/HDB Officer only).
     * Results are sorted by creation date in reversed order.
     * 
     * @param requestedUser the user whose enquiries to retrieve
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<Enquiry> if authorized
     *         - ERROR status with message if access denied
     */
    @Override
    public ServiceResponse<List<Enquiry>> getEnquiriesByUser(User requestedUser) {
        final PolicyResponse policyResponse = enquiryPolicy.canViewEnquiriesByUser(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Enquiry> enquiries = dataManager.getByQuery(
            Enquiry.class, 
            enquiry -> enquiry.getEnquirer() == requestedUser,
            Enquiry.SORT_BY_CREATED_AT_DESC
        );
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

    /**
     * Retrieves enquiries for a specific BTO project (Project handlers only).
     * Results are sorted by creation date in reversed order.
     * 
     * @param requestedUser the user making the request
     * @param btoProject the project to filter enquiries by
     * @return ServiceResponse containing:
     *         - SUCCESS status with List<Enquiry> if authorized
     *         - ERROR status with message if access denied
     */
    @Override
    public ServiceResponse<List<Enquiry>> getEnquiriesByBTOProject(User requestedUser, BTOProject btoProject) {
        final PolicyResponse policyResponse = enquiryPolicy.canViewEnquiriesByBTOProject(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Enquiry> enquiries = dataManager.getByQuery(
            Enquiry.class, 
            enquiry -> enquiry.getBTOProject() == btoProject,
            Enquiry.SORT_BY_CREATED_AT_DESC
        );
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

    /**
     * Creates a new enquiry about a BTO project (Applicant/HDB Officer only).
     * 
     * @param requestedUser the user submitting the enquiry
     * @param btoProject the project being enquired about
     * @param subject the enquiry subject line
     * @param enquiryString the enquiry content
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if created
     *         - ERROR status with message if validation fails
     * @throws DataModelException if enquiry data is invalid
     * @throws DataSavingException if persistence fails
     */
    @Override
    public ServiceResponse<?> addEnquiry(User requestedUser, BTOProject btoProject, String subject, String enquiryString) {
        PolicyResponse policyResponse = enquiryPolicy.canCreateEnquiry(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            Enquiry enquiry = new Enquiry(btoProject, requestedUser, subject, enquiryString);
            dataManager.save(enquiry);
        } catch (DataSavingException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry created successful. Kindly wait for reply. You can view the enquiry from dashboard.");
    }
    
    /**
     * Modifies an existing enquiry (Original enquirer only).
     * Cannot edit enquiries that have been replied to.
     * 
     * @param requestedUser the user requesting the edit
     * @param enquiry the enquiry to modify
     * @param subject the new subject line
     * @param enquiryString the new enquiry content
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if updated
     *         - ERROR status with message if validation fails
     */
    @Override
    public ServiceResponse<?> editEnquiry(User requestedUser, Enquiry enquiry, String subject, String enquiryString) {
        PolicyResponse policyResponse = enquiryPolicy.canEditEnquiry(requestedUser, enquiry);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            enquiry.setSubject(subject);
            enquiry.setEnquiry(enquiryString);
            dataManager.save(enquiry);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        }catch (DataSavingException e) {
            enquiry.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry edited successful.");
    }

    /**
     * Deletes an enquiry (Original enquirer only).
     * Cannot delete enquiries that have been replied to.
     * 
     * @param requestedUser the user requesting deletion
     * @param enquiry the enquiry to delete
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if deleted
     *         - ERROR status with message if validation fails
     */
    @Override
    public ServiceResponse<?> deleteEnquiry(User requestedUser, Enquiry enquiry) {
        PolicyResponse policyResponse = enquiryPolicy.canDeleteEnquiry(requestedUser, enquiry);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            dataManager.delete(enquiry);
        } catch (Exception e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry deleted successful.");
    }
 
    /**
     * Adds a reply to an enquiry (Project handlers only).
     * Cannot reply to already-replied enquiries.
     * 
     * @param requestedUser the user submitting the reply
     * @param enquiry the enquiry being replied to
     * @param replyString the reply content
     * @return ServiceResponse containing:
     *         - SUCCESS status with confirmation message if replied
     *         - ERROR status with message if validation fails
     */
    @Override
    public ServiceResponse<?> replyEnquiry(User requestedUser, Enquiry enquiry, String replyString) {
        PolicyResponse policyResponse = enquiryPolicy.canReplyEnquiry(requestedUser, enquiry);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            enquiry.setReply(replyString);
            dataManager.save(enquiry);
        } catch (Exception e) {
            enquiry.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry replied successful.");
    }
}