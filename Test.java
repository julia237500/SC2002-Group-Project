import controller.interfaces.EnquiryController;
import manager.DIManager;
import manager.interfaces.DataManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.Enquiry;
import model.User;
import util.DefaultDIContainer;

public class Test {
    public static void main(String[] args) {
        DIManager.createInstance(new DefaultDIContainer());
        DIManager diManager = DIManager.getInstance();

        DataManager dataManager = diManager.resolve(DataManager.class);
        SessionManager sessionManager = diManager.resolve(SessionManager.class);
        EnquiryController enquiryController = diManager.resolve(EnquiryController.class);
        
        BTOProject btoProject = dataManager.getByPK(BTOProject.class, "Acacia Breeze");

        User applicant = dataManager.getByPK(User.class, "S3456789E");
        User officer = dataManager.getByPK(User.class, "T1234567J");
        User manager = dataManager.getByPK(User.class, "S5678901G");

        Enquiry enquiry1 = dataManager.getByPK(Enquiry.class, "4f6ffd0b-f728-4048-96e7-6f7857e057c4");
        Enquiry enquiry2 = dataManager.getByPK(Enquiry.class, "edaaa961-56c0-4b5d-b35e-8366a410a836");
        
        sessionManager.setUser(applicant);
        // enquiryController.addEnquiry(btoProject);
        // enquiryController.deleteEnquiry(enquiry1);
        // enquiryController.deleteEnquiry(enquiry2);
        // enquiryController.replyEnquiry(enquiry1);
        // enquiryController.replyEnquiry(enquiry2);

        sessionManager.setUser(officer);
        // enquiryController.addEnquiry(btoProject);
        // enquiryController.replyEnquiry(enquiry1);
        // enquiryController.replyEnquiry(enquiry2);

        sessionManager.setUser(manager);
        // enquiryController.addEnquiry(btoProject);
        // enquiryController.replyEnquiry(enquiry1);
        // enquiryController.replyEnquiry(enquiry