package service.interfaces;

import model.ServiceResponse;
import model.User;

public interface AuthService {
    public ServiceResponse<User> login(String NRIC, String password);
    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword);
}
