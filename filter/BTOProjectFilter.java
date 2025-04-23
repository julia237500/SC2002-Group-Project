package filter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import config.FlatType;
import config.FormField;
import form.FieldData;
import model.BTOProject;

/**
 * A filter class for {@link BTOProject} objects based on user-selected criteria
 * such as neighborhood and available flat types.
 *
 * <p>This class implements the {@link Filter} interface and provides logic to
 * dynamically generate filtering predicates using form input data.</p>
 *
 * <p>It enables modular and flexible filtering of BTO projects within the system,
 * supporting operations like searching by neighborhood and filtering by flat type availability.</p>
 */
public class BTOProjectFilter implements Filter<BTOProject>{
    private String neighborhood;
    private List<FlatType> flatTypes;

    /**
     * Constructs a {@code BTOProjectFilter} with the specified neighborhood and flat types.
     *
     * @param neighborhood the name of the neighborhood to filter by
     * @param flatTypes    the list of {@link FlatType}s to filter by
     */
    public BTOProjectFilter(String neighborhood, List<FlatType> flatTypes){
        this.neighborhood = neighborhood;
        this.flatTypes = flatTypes;
    }

    /**
     * Creates a {@code BTOProjectFilter} from form data inputs.
     * @param data a map of form field identifiers to form field data
     * @return a {@code BTOProjectFilter} constructed based on user input
     */
    public static BTOProjectFilter fromFormData(Map<FormField, FieldData<?>> data){
        String neighborhood = (String) data.get(FormField.NEIGHBORHOOD).getData();

        List<FlatType> flatTypes = new ArrayList<>();
        for(FlatType flatType:FlatType.values()){
            Boolean showFlatType = (Boolean) data.get(flatType.getFilterFormField()).getData();
            if(showFlatType != null && showFlatType) flatTypes.add(flatType);
        }

        return new BTOProjectFilter(neighborhood, flatTypes);
    }

    /**
     * Returns a {@link Predicate} to filter {@link BTOProject} objects
     * based on the specified neighborhood and flat type availability.
     *
     * @return a predicate that evaluates whether a BTO project matches the filter conditions
     */
    @Override
    public Predicate<BTOProject> getFilter() {
        List<Predicate<BTOProject>> predicates = List.of(
            btoProject -> btoProject.getNeighborhood().toLowerCase().contains(neighborhood.toLowerCase().trim()),
            btoProject -> flatTypes.stream().anyMatch(flatType -> btoProject.hasAvailableFlats(flatType))
        );

        return predicates.stream().reduce(Predicate::and).orElse(_ -> true);
    }
    
}
