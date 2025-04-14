package controller.interfaces;

import model.BTOProject;
import model.Enquiry;

public interface EnquiryController {
    void addEnquiry(BTOProject btoProject);
    void editEnquiry(Enquiry enquiry);
    void deleteEnquiry(Enquiry enquiry);
    void replyEnquiry(Enquiry enquiry);
    void showAllEnquiries();
    void showEnquiriesByUser();
    void showEnquiriesByBTOProject(BTOProject btoProject);
    void showEnquiry(Enquiry enquiry);
    void showEnquiryDetail(Enquiry enquiry);
}
