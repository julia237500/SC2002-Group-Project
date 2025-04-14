package service.interfaces;

import java.util.List;

import model.BTOProject;
import model.Enquiry;
import model.User;
import service.ServiceResponse;

public interface EnquiryService {
    ServiceResponse<?> addEnquiry(User requestedUser, BTOProject btoProject, String subject, String enquiryString);
    ServiceResponse<?> editEnquiry(User requestedUser, Enquiry enquiry, String subject, String enquiryString);
    ServiceResponse<?> deleteEnquiry(User requestedUser, Enquiry enquiry);
    ServiceResponse<?> replyEnquiry(User requestedUser, Enquiry enquiry, String replyString);
    ServiceResponse<List<Enquiry>> getAllEnquiries(User requestedUser);
    ServiceResponse<List<Enquiry>> getEnquiriesByUser(User requestedUser);
    ServiceResponse<List<Enquiry>> getEnquiriesByBTOProject(User requestedUser, BTOProject btoProject);
}
