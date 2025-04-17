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


/**
 * Default implementation of the {@link AuthController} interface.
 * <p>
 * This controller handles authentication logic such as logging in and changing passwords.
 * It coordinates with the {@link AuthService} for business logic, the {@link FormController}
 * for collecting user input, and the {@link SessionManager} for session-related operations.
 */
public class DefaultAuthController implements AuthController{
    private AuthService authService;
    private MessageView messageView;
    private FormController formController;
    private SessionManager sessionManager;


    /**
     * Constructs a new {@code DefaultAuthController}.
     *
     * @param authService     the authentication service that handles login and password changes
     * @param messageView     the view used for displaying messages to the user
     * @param formController  the form controller for handling input forms
     * @param sessionManager  the session manager to access session information
     */
    public DefaultAuthController(AuthService AuthService, MessageView messageView, FormController formController, SessionManager sessionManager){
        this.authService = AuthService;
        this.messageView = messageView;
        this.formController = formController;
        this.sessionManager = sessionManager;
    }

    /**
     * Handles the login process by prompting the user to input credentials using a form.
     * Continues prompting until successful login.
     *
     * @return the authenticated {@link User} object
     */
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

    /**
     * Handles the process of changing the password for the currently logged-in user.
     *
     * @return {@code true} if the password change was successful; {@code false} otherwise
     */
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
