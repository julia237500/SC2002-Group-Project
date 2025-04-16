package service.interfaces;

import model.User;
import service.ServiceResponse;

public interface AuthService {
    public ServiceResponse<User> login(String NRIC, String password);
    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword);
}
