package view.interfaces;

import model.Enquiry;


/**
 * Represents the view interface responsible for displaying the details of an enquiry.
 * 
 * <p>This interface allows different implementations 
 * (e.g., terminal, GUI, web) to define how an {@link Enquiry} should be displayed 
 * without being tightly coupled to the business logic.</p>
 */
public interface EnquiryView {

     /**
     * Displays the detailed information of the specified enquiry.
     *
     * @param enquiry The {@link Enquiry} object containing the information to be displayed.
     */
     void showEnquiryDetail(Enquiry enquiry);
}
