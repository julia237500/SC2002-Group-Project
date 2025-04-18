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

public class DefaultEnquiryService implements EnquiryService{
    private final DataManager dataManager;
    private final EnquiryPolicy enquiryPolicy;

    public DefaultEnquiryService(DataManager dataManager, EnquiryPolicy enquiryPolicy){
        this.dataManager = dataManager;
        this.enquiryPolicy = enquiryPolicy;
    }

    @Override
    public ServiceResponse<List<Enquiry>> getAllEnquiries(User requestedUser) {
        final PolicyResponse policyResponse = enquiryPolicy.canViewAllEnquiries(requestedUser);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        List<Enquiry> enquiries = dataManager.getAll(Enquiry.class, Enquiry.SORT_BY_CREATED_AT_DESC);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

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

    @Override
    public ServiceResponse<?> addEnquiry(User requestedUser, BTOProject btoProject, String subject, String enquiryString) {
        PolicyResponse policyResponse = enquiryPolicy.canCreateEnquiry(requestedUser, btoProject);
        if(!policyResponse.isAllowed()){
            return new ServiceResponse<>(policyResponse);
        }

        try {
            Enquiry enquiry = new Enquiry(btoProject, requestedUser, subject, enquiryString);
            dataManager.save(enquiry);
        } catch (DataModelException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, e.getMessage());
        } catch (DataSavingException e) {
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry created successful. Kindly wait for reply. You can view the enquiry from dashboard.");
    }
    
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
        } catch (DataSavingException e) {
            enquiry.restore();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry edited successful.");
    }

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
