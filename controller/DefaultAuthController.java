package controller;

import java.util.Map;

import config.FormField;
import config.ResponseStatus;
import controller.interfaces.AuthController;
import controller.interfaces.FormController;
import form.ChangePasswordForm;
import form.FieldData;
import form.LoginForm;
import manager.interfaces.SessionManager;
import model.User;
import service.ServiceResponse;
import service.interfaces.AuthService;
import view.interfaces.MessageView;

public class DefaultAuthController implements AuthController{
    private AuthService authService;
    private MessageView messageView;
    private FormController formController;
    private SessionManager sessionManager;

    public DefaultAuthController(AuthService AuthService, MessageView messageView, FormController formController, SessionManager sessionManager){
        this.authService = AuthService;
        this.messageView = messageView;
        this.formController = formController;
        this.sessionManager = sessionManager;
    }

    public User handleLogin(){
        while(true){
            formController.setForm(new LoginForm());
            Map<FormField, FieldData<?>> data = formController.getFormData();

            String NRIC = (String) data.get(FormField.NRIC).getData();
            String password = (String) data.get(FormField.PASSWORD).getData();

            ServiceResponse<User> authResponse = authService.login(NRIC, password);
            if(authResponse.getResponseStatus() == ResponseStatus.SUCCESS){
                return authResponse.getData();
            }
            else{
                messageView.error(authResponse.getMessage());
            }
        }
    }

    public boolean changePassword(){
        User user = sessionManager.getUser();
        formController.setForm(new ChangePasswordForm());
        Map<FormField, FieldData<?>> data = formController.getFormData();

        String password = (String) data.get(FormField.PASSWORD).getData();
        String confirmPassword = (String) data.get(FormField.CONFIRM_PASSWORD).getData();

        ServiceResponse<?> authResponse = authService.changePassword(user, password, confirmPassword);
        
        if(authResponse.getResponseStatus() == ResponseStatus.SUCCESS){
            return true;
        }
        else{
            messageView.error(authResponse.getMessage());
            return false;
        }
    }
}
