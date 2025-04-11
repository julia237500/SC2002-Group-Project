package relationship.resolver;

import manager.interfaces.DataManager;
import model.DataModel;

public interface SaveResolver<T extends DataModel> {
    public void resolveSave(T model, DataManager dataManager);
}
