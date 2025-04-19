package generator.report;

import java.util.List;
import model.Application;

public interface ReportGenerator {
    void generateReport(List<Application> applications);
}