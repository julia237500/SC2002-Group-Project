import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import config.UserRole;
import exception.DataSavingException;
import manager.CSVDataManager;
import model.User;

public class Test {
    public static void main(String[] args) {
        CSVDataManager csvDataManager = new CSVDataManager();

        List<Predicate<User>> query = new ArrayList<>();
        query.add(user -> user.getUserRole() == UserRole.HDB_MANAGER);

        List<User> users = csvDataManager.getByQuery(User.class, query);
        for(User user:users){
            System.out.println(user.getName());
        }

        User user = csvDataManager.getByPK(User.class, "T8765432F");
        user.setPassword("abcdef");

        try {
            csvDataManager.save(User.class);
        } catch (DataSavingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
