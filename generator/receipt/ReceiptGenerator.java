package generator.receipt;

import model.Application;

/**
 * The {@code ReceiptGenerator} interface defines a contract for generating receipts
 * based on a given {@link Application}.
 * 
 * <p>Implementations of this interface are responsible for the formatting and output
 * of a receipt</p>
 */
public interface ReceiptGenerator {
    /**
     * Generates a receipt for the specified {@link Application}.
     *
     * @param application the application for which the receipt is to be generated
     */
    void generateReceipt(Application application);
}
