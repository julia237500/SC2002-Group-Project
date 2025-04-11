package relationship.resolver;

import manager.interfaces.DataManager;
import model.DataModel;

public interface DeleteResolver<T extends DataModel> {
     public void resolveDelete(T model, DataManager dataManager);
}
