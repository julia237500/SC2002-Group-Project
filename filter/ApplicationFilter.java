package filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import config.FlatType;
import config.FormField;
import config.MaritalStatus;
import form.FieldData;
import model.Application;

/**
 * {@code ApplicationFilter} implements the {@link Filter} interface for filtering 
 * {@link Application} objects based on user-selected criteria such as {@link MaritalStatus} 
 * and {@link FlatType}.
 * <p>This class helps decouple filtering logic from data processing by creating a 
 * reusable {@link Predicate} that can be applied to streams or collections of applications.</p>
 * <p>The filter criteria are dynamically built using form data submitted by the user, allowing
 * flexible and configurable filtering based on CLI selections.</p>
 */
public class ApplicationFilter implements Filter<Application>{
     /** Selected marital statuses to include in the filter. */
    private final List<MaritalStatus> maritalStatus;
    /** Selected flat types to include in the filter. */
    private final List<FlatType> flatTypes;

    /**
     * Constructs an {@code ApplicationFilter} using the provided lists of marital statuses and flat types.
     *
     * @param maritalStatus list of {@link MaritalStatus} values to include
     * @param flatTypes list of {@link FlatType} values to include
     */
    public ApplicationFilter(List<MaritalStatus> maritalStatus, List<FlatType> flatTypes){
        this.maritalStatus = maritalStatus;
        this.flatTypes = flatTypes;
    }

    public static ApplicationFilter fromFormData(Map<FormField, FieldData<?>> data){
        List<MaritalStatus> maritalStatus = new ArrayList<>();
        for(MaritalStatus status:MaritalStatus.values()){
            Boolean showMaritalStatus = (Boolean) data.get(status.getFilterFormField()).getData();
            if(showMaritalStatus != null && showMaritalStatus) maritalStatus.add(status);
        }

        List<FlatType> flatTypes = new ArrayList<>();
        for(FlatType flatType:FlatType.values()){
            Boolean showFlatType = (Boolean) data.get(flatType.getFilterFormField()).getData();
            if(showFlatType != null && showFlatType) flatTypes.add(flatType);
        }

        return new ApplicationFilter(maritalStatus, flatTypes);
    }

    /**
     * Returns a {@link Predicate} that checks if an {@link Application} satisfies
     * both marital status and flat type criteria.
     * 
     * <p>If no criteria are selected, the default predicate allows all applications.</p>
     *
     * @return a {@code Predicate<Application>} for filtering
     */
    @Override
    public Predicate<Application> getFilter() {
        List<Predicate<Application>> predicates = List.of(
            application -> maritalStatus.contains(application.getApplicant().getMaritalStatus()),
            application -> flatTypes.contains(application.getFlatType())
        );

        return predicates.stream().reduce(Predicate::and).orElse(_ -> true);
    }
}
