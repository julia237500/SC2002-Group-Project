package relationship.resolver;

import manager.interfaces.DataManager;
import model.DataModel;


/**
 * Interface for resolving dependencies or relationships when a {@link DataModel}
 * instance is deleted.
 * 
 * <p>This is particularly useful in situations where deleting a model requires
 * cascading effects or cleanup actions, such as removing references from other
 * models or notifying dependent systems.</p>
 *
 * @param <T> the type of {@link DataModel} this resolver handles
 */
public interface DeleteResolver<T extends DataModel> {

     /**
      * Resolves any necessary relationship handling or cascading actions 
      * when a model is being deleted.
      *
      * @param model the data model being deleted
      * @param dataManager the data manager used to access or update related models
      */
     public void resolveDelete(T model, DataManager dataManager);
}

/**
 * There is a delete() method in DataManager.java
 * There is a resolveDelete() method here
 * The difference between the two is that delete() deletes a specific data model from storage.
 * delete() is the basic, direct deletion method. It is the final step — it removes the record from the in-memory list or persistent storage.
 * delete() is model-agnostic — it doesn't know or care what kind of model it is or what it’s related to.
 * it does not check or remove dependent entities, or handle cascading deletes or cleanup, or understand model relationships.
 * On the other hand, resolveDelete() handles the clean-up and cascade logic before the actual deletion happens. It knows about relationships.
 * resolveDelete() handles any additional deletions or updates required because we're deleting a certain model,
 * cleans up related data (e.g., deletes enquiries or officer registrations linked to the model), and
 * ensures there are no orphaned or inconsistent records.
 */