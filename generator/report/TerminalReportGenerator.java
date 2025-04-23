package generator.report;

import java.util.List;

import config.MaritalStatus;
import model.Application;

/**
 * {@code TerminalReportGenerator} is a concrete implementation of the {@link ReportGenerator}
 * interface that outputs a formatted report of BTO applications to the terminal.
 *
 * <p>This class gives a quick summary
 * of application details for auditing, monitoring, or decision-making purposes.
 * Each application is displayed with personal details of the applicant, project information,
 * and application timestamp.</p>
 */
public class TerminalReportGenerator implements ReportGenerator {

    /**
     * Generates a formatted terminal report for a list of BTO applications.
     * 
     * <p>Each application's details are displayed in a human-readable format,
     * with a unique index and clearly labeled fields.</p>
     *
     * @param applications the list of {@link Application} objects to report on;
     * must not be {@code null}
     * @throws NullPointerException if {@code applications} is {@code null}
     */
    @Override
    public void generateReport(List<Application> applications) {
        System.out.println("========= APPLICATION REPORT =========");
        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            String applicantName          = application.getApplicant().getName();
            String applicantNRIC          = application.getApplicant().getNRIC();
            int    applicantAge           = application.getApplicant().getAge();
            MaritalStatus maritalStatus   = application.getApplicant().getMaritalStatus();
            String projectName            = application.getBTOProject().getName();
            String flatType               = application.getFlatType().toString();
            String createdAt              = application.getCreatedAt().toString();

            String report = """
                ======================================
                Index             : %d
                ======================================
                Applicant Name    : %s
                Applicant NRIC    : %s
                Applicant Age     : %d
                Marital Status    : %s
                Project Name      : %s
                Flat Type         : %s
                Application Date  : %s
                """.formatted(
                    i + 1,                   // index
                    applicantName,
                    applicantNRIC,
                    applicantAge,
                    maritalStatus,
                    projectName,
                    flatType,
                    createdAt
                );

            System.out.print(report);
        }
        System.out.println("======================================");
    }
}
