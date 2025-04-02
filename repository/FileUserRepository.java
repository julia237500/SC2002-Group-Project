package repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.RepositoryException;
import model.User;
import parser.UserParser;
import repository.interfaces.UserRepository;
import util.CSVFileReader;
import util.CSVFileWriter;

public class FileUserRepository implements UserRepository{
    private final static String FILE_PATH = "./data/UserList.csv";

    private final Map<String, User> users = new HashMap<>();

    public FileUserRepository(){
        try{
            List<List<String>> storedUsersData = CSVFileReader.readFile(FILE_PATH);
            
            for(List<String> userData:storedUsersData){
                User user = UserParser.parseUser(userData);
                users.put(user.getNRIC(), user);
            }
        }
        catch(Exception e){
            System.err.println("Fatal: Fail to read user, TERMINATING. Error:" + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void save(User user) {
        try {
            saveAll();
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }
    }

    private void saveAll() throws IOException{
        List<List<String>> lines = new ArrayList<>(); 
        for(User user:users.values()){
            lines.add(UserParser.toListOfString(user));
        }
        CSVFileWriter.writeFile(FILE_PATH, lines);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getByNRIC(String NRIC) {
        return users.get(NRIC);
    }
}
