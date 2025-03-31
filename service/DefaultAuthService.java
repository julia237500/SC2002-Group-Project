package service;

import java.util.HashMap;
import java.util.List;

import config.ResponseStatus;
import model.ServiceResponse;
import model.User;
import parser.UserParser;
import service.interfaces.AuthService;
import util.CSVFileReaderUtil;

public class DefaultAuthService implements AuthService{
    private static HashMap<String, User> users = null;

    public DefaultAuthService(){
        if(users == null) readUser();
    }

    private static void readUser(){
        try{
            List<List<String>> userData = CSVFileReaderUtil.readFile("./data/UserList.csv");
            users = UserParser.parseUser(userData);
        }
        catch(Exception e){
            System.err.println("Fatal: Fail to read user, TERMINATING. Error:" + e.getMessage());
            System.exit(1);
        }
    }

    public ServiceResponse<User> login(String NRIC, String password){
        User user = users.get(NRIC);
        
        if(user == null) return new ServiceResponse<User>(ResponseStatus.ERROR, "Invalid NRIC", null);
        if(!user.getPassword().equals(password)) return new ServiceResponse<User>(ResponseStatus.ERROR, "Incorrect Password", null);
        return new ServiceResponse<User>(ResponseStatus.SUCCESS, "Login Successful", user);
    }

    public ServiceResponse<?> changePassword(User user, String password, String confirmPassword){
        if(!password.equals(confirmPassword)){
            return new ServiceResponse<>(ResponseStatus.FAILURE, "Password is not the same as Confirm Password");
        }

        user.setPassword(password);
        return new ServiceResponse<>(ResponseStatus.SUCCESS, "Password changed successful");
    }
}