package policy.interfaces;

import model.BTOProject;
import model.Enquiry;
import model.User;
import policy.PolicyResponse;

public interface EnquiryPolicy {
    PolicyResponse canViewAllEnquiries(User requestedUser);
    PolicyResponse canViewEnquiriesByBTOProject(User requestedUser, BTOProject btoProject);
    PolicyResponse canViewEnquiriesByUser(User requestedUser);
   
    PolicyResponse canCreateEnquiry(User requestedUser, BTOProject btoProject);
    PolicyResponse canEditEnquiry(User requestedUser, Enquiry enquiry);
    PolicyResponse canDeleteEnquiry(User requestedUser, Enquiry enquiry);
    PolicyResponse canReplyEnquiry(User requestedUser, Enquiry enquiry);
}
