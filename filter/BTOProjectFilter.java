package filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import config.FlatType;
import config.FormField;
import form.FieldData;
import model.BTOProject;

public class BTOProjectFilter implements Filter<BTOProject>{
    private String neighborhood;
    private List<FlatType> flatTypes;

    public BTOProjectFilter(String neighborhood, List<FlatType> flatTypes){
        this.neighborhood = neighborhood;
        this.flatTypes = flatTypes;
    }

    public static BTOProjectFilter fromFormData(Map<FormField, FieldData<?>> data){
        String neighborhood = (String) data.get(FormField.NEIGHBORHOOD).getData();

        List<FlatType> flatTypes = new ArrayList<>();
        for(FlatType flatType:FlatType.values()){
            Boolean showFlatType = (Boolean) data.get(flatType.getFilterFormField()).getData();
            if(showFlatType != null && showFlatType) flatTypes.add(flatType);
        }

        return new BTOProjectFilter(neighborhood, flatTypes);
    }

    @Override
    public Predicate<BTOProject> getFilter() {
        List<Predicate<BTOProject>> predicates = List.of(
            btoProject -> btoProject.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase().trim()),
            btoProject -> flatTypes.stream().anyMatch(flatType -> btoProject.hasAvailableFlats(flatType))
        );

        return predicates.stream().reduce(Predicate::and).orElse(_ -> true);
    }
    
}
