package relationship;

import exception.DataSavingException;
import exception.RelationshipException;
import manager.interfaces.DataManager;
import model.Application;
import relationship.resolver.SaveResolver;

public class ApplicationRelationshipResolver implements SaveResolver<Application>{
    @Override
    public void resolveSave(Application application, DataManager dataManager) {
        try{
            dataManager.save(application.getBtoProject());
        } catch (DataSavingException e) {
            throw new RelationshipException("Failed to save BTOProject: " + e.getMessage());
        }
    }
}
