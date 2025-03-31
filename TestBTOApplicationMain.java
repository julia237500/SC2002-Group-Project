import java.time.LocalDate;
import java.util.List;

import config.FlatType;
import config.MaritalStatus;
import config.UserRole;
import controller.interfaces.BTOApplicationController;
import manager.DIManager;
import manager.interfaces.SessionManager;
import model.BTOProject;
import model.User;
import util.DefaultDIContainer;
import util.interfaces.DIContainer;

public class TestBTOApplicationMain {
    public static void main(String[] args) {
        DIContainer diContainer = new DefaultDIContainer();
        DIManager.createInstance(diContainer);

        BTOApplicationController btoApplicationController = DIManager.getInstance().resolve(BTOApplicationController.class);
        SessionManager sessionManager = DIManager.getInstance().resolve(SessionManager.class);

        User HDBManager = new User(null, null, 0, null, null, UserRole.HDB_MANAGER);
        BTOProject btoProjectEmpty = new BTOProject(HDBManager, null, null, 0, 0, 0, 0, LocalDate.now(), LocalDate.now(), 5);
        BTOProject btoProjectNotEmpty = new BTOProject(HDBManager, null, null, 1, 0, 1, 0, LocalDate.now(), LocalDate.now(), 5);

        User applicantSingleUnder35 = new User(null, null, 20, MaritalStatus.SINGLE, null, UserRole.APPLICANT);
        User applicantSingleOver35 = new User(null, null, 35, MaritalStatus.SINGLE, null, UserRole.APPLICANT);
        User applicantMarriedUnder21 = new User(null, null, 20, MaritalStatus.MARRIED, null, UserRole.APPLICANT);
        User applicantMarriedUnder35 = new User(null, null, 21, MaritalStatus.MARRIED, null, UserRole.APPLICANT);
        User applicantMarriedOver35 = new User(null, null, 35, MaritalStatus.MARRIED, null, UserRole.APPLICANT);
        User HDBOfficer = new User(null, null, 21, MaritalStatus.MARRIED, null, UserRole.HDB_OFFICER);

        List<User> users = List.of(applicantSingleUnder35, applicantSingleOver35, applicantMarriedUnder21, applicantMarriedUnder35, applicantMarriedOver35, HDBOfficer, HDBManager, applicantMarriedOver35);

        sessionManager.setUser(HDBOfficer);
        int i = 1;
        System.out.print(i++);
        btoApplicationController.apply(btoProjectEmpty, FlatType.TWO_ROOM_FLAT);
        System.out.print(i++);
        btoApplicationController.apply(btoProjectEmpty, FlatType.THREE_ROOM_FLAT);
        
        for(User user:users){
            sessionManager.setUser(user);
            System.out.print(i++);
            btoApplicationController.apply(btoProjectNotEmpty, FlatType.TWO_ROOM_FLAT);
            System.out.print(i++);
            btoApplicationController.apply(btoProjectNotEmpty, FlatType.THREE_ROOM_FLAT);
        }
    }
}

// Result:
// 1. Error
// 2. Error
// 3. Error
// 4. Error
// 5. Success
// 6. Error
// 7. Error
// 8. Error
// 9. Error
// 10. Success
// 11. Success
// 12. Success
// 13. Success
// 14. Success
// 15. Error
// 16. Error
// 17. Error
// 18. Error
