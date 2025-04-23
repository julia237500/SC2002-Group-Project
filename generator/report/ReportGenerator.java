package generator.report;

import java.util.List;
import model.Application;

/**
 * The {@code ReportGenerator} interface defines the contract for generating reports
 * based on a list of BTO {@link Application} objects.
 */
public interface ReportGenerator {

    /**
     * Generates a report based on the provided list of applications.
     *
     * <p>This method should handle any formatting and output responsibilities required to
     * represent the applications meaningfully</p>
     * @param applications the list of {@link Application} objects to include in the report;
     * must not be {@code null}
     */
    void generateReport(List<Application> applications);
}
