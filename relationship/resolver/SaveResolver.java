package relationship.resolver;

import manager.interfaces.DataManager;
import model.DataModel;


/**
 * An interface for resolving save-time relationship logic for a specific model.
 * 
 * @param <T> The type of DataModel to resolve during save operations.
 */
public interface SaveResolver<T extends DataModel> {

    /**
     * Resolves any associated save logic or relationships for the given model before or during its persistence.
     * 
     * @param model        The model object being saved.
     * @param dataManager  The data manager used to access or update related models.
     */
    public void resolveSave(T model, DataManager dataManager);
}
