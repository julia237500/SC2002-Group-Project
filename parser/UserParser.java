package parser;

import java.util.HashMap;
import java.util.List;

import config.MaritalStatus;
import config.UserRole;
import exception.UserParsingException;
import model.User;

public class UserParser {
    private static final int USER_DATA_SIZE = 6;

    public static HashMap<String, User> parseUser(List<List<String>> lines){
        HashMap<String, User> users = new HashMap<>();

        for(List<String> line:lines){
            if(line.size() != USER_DATA_SIZE){
                throw new UserParsingException(String.format("Data size of %d is expected, but data size of %d is passed. Data: %s", USER_DATA_SIZE, line.size(), line.toString()));
            }

            String name = line.get(0);
            String NRIC = line.get(1);

            int age = 0;
            try {
                age = Integer.parseInt(line.get(2));
            } catch (Exception e) {
                throw new UserParsingException(String.format("Invalid age. Data: %s", line.get(2)));
            }

            MaritalStatus maritalStatus = null;
            switch (line.get(3)) {
                case "Single":
                    maritalStatus = MaritalStatus.SINGLE;
                    break;
                case "Married":
                    maritalStatus = MaritalStatus.MARRIED;
                    break;
                default:
                    throw new UserParsingException(String.format("Invalid marital status. Data: %s", line.get(3)));
            }

            String password = line.get(4);

            UserRole userRole = null;
            switch (line.get(5)) {
                case "Applicant":
                    userRole = UserRole.APPLICANT;
                    break;
                case "HDB Officer":
                    userRole = UserRole.HDB_OFFICER;
                    break;
                case "HDB Manager":
                    userRole = UserRole.HDB_MANAGER;
                    break;
                default:
                    throw new UserParsingException(String.format("Invalid user role. Data: %s", line.get(5)));
            }

            User user = new User(name, NRIC, age, maritalStatus, password, userRole);
            users.put(NRIC, user);
        }

        return users;
    }
}
