package controller;

import java.util.List;
import java.util.Map;

import command.Command;
import config.FormField;
import config.ResponseStatus;
import controller.interfaces.EnquiryController;
import controller.interfaces.FormController;
import factory.EnquiryCommandFactory;
import form.EnquiryForm;
import form.FieldData;
import form.ReplyForm;
import manager.interfaces.MenuManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.Enquiry;
import model.User;
import service.ServiceResponse;
import service.interfaces.EnquiryService;
import view.interfaces.EnquiryView;
import view.interfaces.MessageView;

public class DefaultEnquiryController extends AbstractDefaultController implements EnquiryController{
    private EnquiryService enquiryService;
    private EnquiryView enquiryView;
    private FormController formController;
    private MenuManager menuManager;
    private SessionManager sessionManager;

    public DefaultEnquiryController(EnquiryService enquiryService, EnquiryView enquiryView, FormController formController, MenuManager menuManager, SessionManager sessionManager, MessageView messageView){
        super(messageView);

        this.enquiryService = enquiryService;
        this.enquiryView = enquiryView;
        this.formController = formController;
        this.menuManager = menuManager;
        this.sessionManager = sessionManager;
    }

    @Override
    public void addEnquiry(BTOProject btoProject) {
        User user = sessionManager.getUser();
        
        formController.setForm(new EnquiryForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String subject = (String) data.get(FormField.SUBJECT).getData();
        String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.addEnquiry(user, btoProject, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void editEnquiry(Enquiry enquiry) {
        User user = sessionManager.getUser();

        formController.setForm(new EnquiryForm(enquiry));
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String subject = (String) data.get(FormField.SUBJECT).getData();
        String enquiryString = (String) data.get(FormField.ENQUIRY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.editEnquiry(user, enquiry, subject, enquiryString);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        User user = sessionManager.getUser();
        ServiceResponse<?> serviceResponse = enquiryService.deleteEnquiry(user, enquiry);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void replyEnquiry(Enquiry enquiry){
        User user = sessionManager.getUser();

        formController.setForm(new ReplyForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();
        String replyString = (String) data.get(FormField.REPLY).getData();

        ServiceResponse<?> serviceResponse = enquiryService.replyEnquiry(user, enquiry, replyString);
        defaultShowServiceResponse(serviceResponse);
    }

    @Override
    public void showAllEnquiries() {
        User user = sessionManager.getUser();

        ServiceResponse<List<Enquiry>> serviceResponse = enquiryService.getAllEnquiries(user);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<Enquiry> enquiries = serviceResponse.getData();
        if(enquiries.isEmpty()){
            messageView.info("Enquiries not found.");
            return;
        }
        
        Map<Integer, Command> commands = EnquiryCommandFactory.getShowEnquiriesCommands(enquiries);
        menuManager.addCommands("Enquiries", commands);
    }

    @Override
    public void showEnquiriesByUser() {
        User user = sessionManager.getUser();

        ServiceResponse<List<Enquiry>> serviceResponse = enquiryService.getEnquiriesByUser(user);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
            return;
        }

        List<Enquiry> enquiries = serviceResponse.getData();
        if(enquiries.isEmpty()){
            messageView.info("Enquiries not found.");
            return;
        }
        
        Map<Integer, Command> commands = EnquiryCommandFactory.getShowEnquiriesCommands(enquiries);
        menuManager.addCommands("Enquiries", commands);
    }

    @Override
    public void showEnquiriesByBTOProject(BTOProject btoProject) {
        User user = sessionManager.getUser();

        ServiceResponse<List<Enquiry>> serviceResponse = enquiryService.getEnquiriesByBTOProject(user, btoProject);

        if(serviceResponse.getResponseStatus() != ResponseStatus.SUCCESS){
            messageView.error(serviceResponse.getMessage());
        