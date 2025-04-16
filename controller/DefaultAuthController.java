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

public class DefaultAuthController extends AbstractDefaultController implements AuthController{
    private final AuthService authService;
    private final FormController formController;
    private final SessionManager sessionManager;

    public DefaultAuthController(AuthService AuthService, MessageView messageView, FormController formController, SessionManager sessionManager) {
        super(messageView);

        this.authService = AuthService;
        this.formController = formController;
        this.sessionManager = sessionManager;
    }

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
