package view.terminal;

import model.Application;
import view.interfaces.ApplicationView;

public class TerminalApplicationView extends AbstractTerminalView implements ApplicationView {

    @Override
    public void showApplicationDetail(Application application) {
        showTitle("Application Detail");

        System.out.println("""
                BTO Project       : %s
                Flat Type         : %s
                Applicant Name    : %s
                Applicant NRIC    : %s
                Application Status: %s
                Withdrawal Status : %s
                """.formatted(
                    application.getBTOProject().getName(),
                    application.getFlatType().getStoredString(),
                    application.getApplicant().getName(),
                    application.getApplicant().getNRIC(),
                    application.getApplicationStatus().getStoredString(),
                    application.getWithdrawalStatus().getStoredString()
                ));
    }
    
}
