package generator.receipt;

import config.MaritalStatus;
import model.Application;

/**
 * The {@code TerminalReceiptGenerator} class provides a concrete implementation
 * of the {@link ReceiptGenerator} interface that outputs a formatted BTO application
 * receipt to the terminal.
 *
 * <p>This generator does not perform any checks on the booking status of the application;
 * it simply extracts and prints the application details in a readable format.</p>
 */
public class TerminalReceiptGenerator implements ReceiptGenerator{

    /**
     * Generates and prints a formatted receipt to the terminal using the details
     * from the specified {@link Application}.
     *
     * @param application the application for which the receipt is to be generated;
     * must not be {@code null}
     * @throws NullPointerException if the application or any of its required fields are {@code null}
     */
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

        String projectName = application.getBTOProject().getName();
        String flatType = application.getFlatType().getStoredString();
        String createdAt = application.getCreatedAt().toString();

        String receipt = """
                ======================================
                BTO APPLICATION RECEIPT
                ======================================
                Applicant Name   : %s
                Applicant NRIC   : %s
                Applicant Age    : %d
                Marital Status   : %s
                Project Name     : %s
                Flat Type        : %s
                Application Date : %s
                ======================================
                """.formatted(
                    applicantName,
                    applicantNRIC,
                    applicantAge,
                    applicantMaritalStatus,
                    projectName,
                    flatType,
                    createdAt
                );

        System.out.println(receipt);
    }
}
