package view.terminal;

import config.EnquiryStatus;
import model.Enquiry;
import view.interfaces.EnquiryView;

/**
 * Terminal-based implementation of the {@link EnquiryView} interface.
 * <p>
 * This view displays the details of an enquiry, including the associated BTO project, the enquirer,
 * the subject, the enquiry message, and the reply (if available).
 * </p>
 */
public class TerminalEnquiryView extends AbstractTerminalView implements EnquiryView{


    /**
     * Displays the details of an enquiry, including the BTO project, enquirer, subject, enquiry content,
     * and the reply (if the enquiry status is REPLIED).
     * 
     * @param enquiry the {@link Enquiry} object containing the details to be displayed
     */
    @Override
    public void showEnquiryDetail(Enquiry enquiry) {
        showTitle("Enquiry");
        System.out.println("""
                BTO Project: %s
                Enquirer   : %s
                Subject    : %s
                Enquiry    : %s
                Reply      : %s
                """.formatted(
                    enquiry.getBTOProject().getName(),
                    enquiry.getEnquirer().getName(),
                    enquiry.getSubject(),
                    enquiry.getEnquiry(),
                    enquiry.getEnquiryStatus() == EnquiryStatus.REPLIED ? enquiry.getReply() : ""
                ));
    }
    
}
