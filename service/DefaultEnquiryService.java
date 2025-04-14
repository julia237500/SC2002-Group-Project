package service;

import java.util.List;

import config.ResponseStatus;
import config.UserRole;
import exception.DataModelException;
import exception.DataSavingException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.Enquiry;
import model.User;
import service.interfaces.EnquiryService;

public class DefaultEnquiryService implements EnquiryService{
    private DataManager dataManager;

    public DefaultEnquiryService(DataManager dataManager){
        this.dataManager = dataManager;
    }

    @Override
    public ServiceResponse<List<Enquiry>> getAllEnquiries(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Manager can performed this action.");
        }

        List<Enquiry> enquiries = dataManager.getAll(Enquiry.class, Enquiry.SORT_BY_CREATED_AT_DESC);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

    @Override
    public ServiceResponse<List<Enquiry>> getEnquiriesByUser(User requestedUser) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Applicant/HDB Officer can performed this action.");
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
        if(requestedUser.getUserRole() != UserRole.HDB_MANAGER && !btoProject.isHandlingBy(requestedUser)){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only HDB Officer/Manager handling the project can performed this action.");
        }

        List<Enquiry> enquiries = dataManager.getByQuery(
            Enquiry.class, 
            enquiry -> enquiry.getBtoProject() == btoProject,
            Enquiry.SORT_BY_CREATED_AT_DESC
        );
        return new ServiceResponse<>(ResponseStatus.SUCCESS, enquiries);
    }

    @Override
    public ServiceResponse<?> addEnquiry(User requestedUser, BTOProject btoProject, String subject, String enquiryString) {
        if(requestedUser.getUserRole() != UserRole.APPLICANT && requestedUser.getUserRole() != UserRole.HDB_OFFICER){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Applicant/HDB Officer can performed this action.");
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
        if(requestedUser != enquiry.getEnquirer()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only enquirer can performed this action.");
        }

        if(!enquiry.canBeAltered()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Replied enquiries cannot be edited.");
        }

        String oldSubject = enquiry.getSubject();
        String oldEnquiryString = enquiry.getEnquiry();
        try {
            enquiry.setSubject(subject);
            enquiry.setEnquiry(enquiryString);
            dataManager.save(enquiry);
        } catch (Exception e) {
            enquiry.setSubject(oldSubject);
            enquiry.setEnquiry(oldEnquiryString);
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        } 

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry edited successful.");
    }

    @Override
    public ServiceResponse<?> deleteEnquiry(User requestedUser, Enquiry enquiry) {
        if(requestedUser != enquiry.getEnquirer()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only enquirer can performed this action.");
        }

        if(!enquiry.canBeAltered()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Replied enquiries cannot be deleted.");
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
        if(!enquiry.getBtoProject().isHandlingBy(requestedUser)){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Access denied. Only Manager/Officer in-charge can performed this action.");
        }

        if(!enquiry.canBeAltered()){
            return new ServiceResponse<>(ResponseStatus.ERROR, "Enquiries already been replied.");
        }

        try {
            enquiry.setReply(replyString);
            dataManager.save(enquiry);
        } catch (Exception e) {
            enquiry.revertReply();
            return new ServiceResponse<>(ResponseStatus.ERROR, "Internal error. %s".formatted(e.getMessage()));
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Enquiry replied successful.");
    }
}
