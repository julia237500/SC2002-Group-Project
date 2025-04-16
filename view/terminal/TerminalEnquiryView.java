package view.terminal;

import config.EnquiryStatus;
import model.Enquiry;
import view.interfaces.EnquiryView;

public class TerminalEnquiryView extends AbstractTerminalView implements EnquiryView{

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
