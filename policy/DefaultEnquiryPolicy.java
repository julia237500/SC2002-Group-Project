package policy;

import config.UserRole;
import model.BTOProject;
import model.Enquiry;
import model.User;
import policy.interfaces.EnquiryPolicy;

public class DefaultEnquiryPolicy implements EnquiryPolicy{
    @Override
    public PolicyResponse canViewAllEnquiries(User requestedUser) {
        if(requestedUser.getUserRole() == UserRole.HDB_MANAGER) return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only HDB Manager can view all enquiries");
    }

    @Override
    public PolicyResponse canViewEnquiriesByBTOProject(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() == UserRole.HDB_MANAGER) return PolicyResponse.allow();
        if(btoProject.isHandlingBy(requestedUser)) return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only HDB Officer handling this project can view enquiries.");
    }

    @Override
    public PolicyResponse canViewEnquiriesByUser(User requestedUser) {
        if(requestedUser.getUserRole() == UserRole.APPLICANT || requestedUser.getUserRole() == UserRole.HDB_OFFICER)
            return PolicyResponse.allow();

        return PolicyResponse.deny("Access denied. Only Applicant or HDB Officer can view their enquiries.");
    }

    @Override
    public PolicyResponse canCreateEnquiry(User requestedUser, BTOProject btoProject) {
        if(requestedUser.getUserRole() == UserRole.APPLICANT ||
            (requestedUser.getUserRole() == UserRole.HDB_OFFICER && !btoProject.isHandlingBy(requestedUser))){
                return PolicyResponse.allow();
        }

        return PolicyResponse.deny("Access denied. Only Applicant or HDB Officer not handling this project can create enquiry.");
    }

    @Override
    public PolicyResponse canEditEnquiry(User requestedUser, Enquiry enquiry) {
        if(enquiry.getEnquirer() != requestedUser){
            return PolicyResponse.deny("Access denied. Only the enquirer can edit this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry edited unsuccessful. Enquiry cannot be edited.");

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canDeleteEnquiry(User requestedUser, Enquiry enquiry) {
        if(enquiry.getEnquirer() != requestedUser){
            return PolicyResponse.deny("Access denied. Only the enquirer can delete this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry deleted unsuccessful. Enquiry cannot be deleted.");

        return PolicyResponse.allow();
    }

    @Override
    public PolicyResponse canReplyEnquiry(User requestedUser, Enquiry enquiry) {
        if(!enquiry.getBTOProject().isHandlingBy(requestedUser)){
            return PolicyResponse.deny("Access denied. Only HDB Manager/Officer handling this project can reply to this enquiry.");
        }
        if(!enquiry.canBeAltered()) return PolicyResponse.deny("Enquiry replied unsuccessful. Enquiry cannot be replied.");

        return PolicyResponse.allow();
    }
}
