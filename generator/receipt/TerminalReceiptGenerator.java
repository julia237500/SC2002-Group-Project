package generator.receipt;

import config.MaritalStatus;
import model.Application;

public class TerminalReceiptGenerator implements ReceiptGenerator{

    @Override
    public void generateReceipt(Application application) {
        // Generate receipt based on application
        // Dont need to check if it is booked or not
        // Just print the details
        // You can decide the format
        String applicantName = application.getApplicant().getName();
        String applicantNRIC = application.getApplicant().getNRIC();
        Integer applicantAge = application.getApplicant().getAge();
        MaritalStatus applicantMaritalStatus = application.getApplicant().getMaritalStatus();

        String projectName = application.getBtoProject().getName();
        String flatType = application.getFlatType().toString();
        String createdAt = application.getCreatedAt().toString();

        String receipt = """
                ======================================
                BTO APPLICATION RECEIPT
                ======================================
                Applicant Name: %s
                Applicant NRIC: %s
                Applicant Age: %d
                Marital Status: %s
                Project Name:   %s
                Flat Type:      %s
                Application Date: %s
                ======================================
                """.formatted(applicantName,applicantNRIC,applicantAge,applicantMaritalStatus,projectName,flatType,createdAt);
        System.out.println(receipt);

    }
}
