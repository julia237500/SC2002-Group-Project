package generator.report;

import java.util.List;

import config.MaritalStatus;
import model.Application;

public class TerminalReportGenerator implements ReportGenerator {

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