package parser;

import java.util.ArrayList;
import java.util.List;

import config.MaritalStatus;
import config.UserRole;
import exception.ModelParsingException;
import model.User;

public class UserParser {
    private static final int USER_DATA_SIZE = 6;

    public static User parseUser(List<String> line){
        if(line.size() != USER_DATA_SIZE){
            throw new ModelParsingException(String.format("Data size of %d is expected, but data size of %d is passed. Data: %s", USER_DATA_SIZE, line.size(), line.toString()));
        }

        String name = line.get(0);
        String NRIC = line.get(1);

        int age = 0;
        try {
            age = Integer.parseInt(line.get(2));
        } catch (Exception e) {
            throw new ModelParsingException(String.format("Invalid age: %s", line.get(2)));
        }

        MaritalStatus maritalStatus = null;
        try {
            maritalStatus = MaritalStatus.parseMaritalStatus(line.get(3));
        } catch (Exception e) {
            throw new ModelParsingException(e.getMessage());
        }

        String password = line.get(4);

        UserRole userRole = null;
        try {
            userRole = UserRole.parseUserRole(line.get(5));
        } catch (Exception e) {
            throw new ModelParsingException(e.getMessage());
        }

        return new User(name, NRIC, age, maritalStatus, password, userRole);
    }

    public static List<String> toListOfString(User user){
        List<String> list = new ArrayList<>();

        list.add(user.getName());
        list.add(user.getNRIC());
        list.add(Integer.toString(user.getAge()));
        list.add(user.getMaritalStatus().getStoredString());
        list.add(user.getPassword());
        list.add(user.getUserRole().getStoredString());

        return list;
    }
}
