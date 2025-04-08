package service;

import config.ResponseStatus;
import model.ServiceResponse;
import model.User;
import repository.interfaces.UserRepository;
import service.interfaces.AuthService;

public class DefaultAuthService implements AuthService{
    private UserRepository userRepository;

    public DefaultAuthService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public ServiceResponse<User> login(String NRIC, String password){
        User user = null;
        try {
            user = userRepository.getByNRIC(NRIC);
        } catch (Exception e) {
            return new ServiceResponse<User>(ResponseStatus.ERROR, "Internal Error. " + e.getMessage());
        }
        
        if(user == null) return new ServiceResponse<User>(ResponseStatus.ERROR, "Invalid NRIC");
        if(!user.getPassword().equals(password)) return new ServiceResponse<User>(ResponseStatus.ERROR, "Incorrect Password");
        return new ServiceResponse<User>(ResponseStatus.SUCCESS, "Login Successful", user);
    }

    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            return new ServiceResponse<>(ResponseStatus.FAILURE, "Password is not the same as Confirm Password");
        }

        String oldPassword = user.getPassword();
        user.setPassword(password);

        try {
            userRepository.save(user);
        } catch (Exception e) {
            user.setPassword(oldPassword);
            return new ServiceResponse<>(ResponseStatus.ERROR, "Save to file fail. " + e.getMessage());
        }

        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Password changed successful");
    }
}