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

public class ApplicationFilter implements Filter<Application>{
    private final List<MaritalStatus> maritalStatus;
    private final List<FlatType> flatTypes;

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

    @Override
    public Predicate<Application> getFilter() {
        List<Predicate<Application>> predicates = List.of(
            application -> maritalStatus.contains(application.getApplicant().getMaritalStatus()),
            application -> flatTypes.contains(application.getFlatType())
        );

        return predicates.stream().reduce(Predicate::and).orElse(_ -> true);
    }
}
