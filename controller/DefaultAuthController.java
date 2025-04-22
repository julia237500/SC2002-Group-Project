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
public class DefaultAuthController extends AbstractDefaultController implements AuthController{
    private final AuthService authService;
    private final FormController formController;
    private final SessionManager sessionManager;


    /**
     * Constructs a new {@code DefaultAuthController}.
     *
     * @param authService     the authentication service that handles login and password changes
     * @param messageView     the view used for displaying messages to the user
     * @param formController  the form controller for handling input forms
     * @param sessionManager  the session manager to access session information
     */
    public DefaultAuthController(AuthService AuthService, MessageView messageView, FormController formController, SessionManager sessionManager) {
        super(messageView);

        this.authService = AuthService;
        this.formController = formController;
        this.sessionManager = sessionManager;
    }


    /**
     * Handles the login process by prompting the user to input credentials using a form.
     * Continues prompting until successful login.
     *
     * @return the authenticated {@link User} object
     */
    public void handleLogin(){
        while(true){
            formController.setForm(new LoginForm());
            final Map<FormField, FieldData<?>> data = formController.getFormData();

            final String NRIC = (String) data.get(FormField.NRIC).getData();
            final String password = (String) data.get(FormField.PASSWORD).getData();

            final ServiceResponse<User> serviceResponse = authService.login(NRIC, password);

            if(serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS){
                final User user = serviceResponse.getData();

                sessionManager.setUser(user);
                messageView.success("Login successful! Welcome back, %s!".formatted(user.getName()));

                break;
            }
            else{
                defaultShowServiceResponse(serviceResponse);
            }
        }
    }

    /**
     * Handles the process of changing the password for the currently logged-in user.
     *
     * @return {@code true} if the password change was successful; {@code false} otherwise
     */
    public boolean changePassword(){
        final User user = sessionManager.getUser();
        formController.setForm(new ChangePasswordForm());
        final Map<FormField, FieldData<?>> data = formController.getFormData();

        final String password = (String) data.get(FormField.PASSWORD).getData();
        final String confirmPassword = (String) data.get(FormField.CONFIRM_PASSWORD).getData();

        final ServiceResponse<?> serviceResponse = authService.changePassword(user, password, confirmPassword);
        
        defaultShowServiceResponse(serviceResponse);
        return serviceResponse.getResponseStatus() == ResponseStatus.SUCCESS;
    }
}
