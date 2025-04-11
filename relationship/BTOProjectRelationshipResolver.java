package relationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import exception.RelationshipException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.FlatUnit;
import relationship.resolver.DeleteResolver;
import relationship.resolver.LoadResolver;
import relationship.resolver.SaveResolver;

public class BTOProjectRelationshipResolver implements LoadResolver, SaveResolver<BTOProject>, DeleteResolver<BTOProject>{
    @Override
    public void resolveLoad(DataManager dataManager) {
        List<BTOProject> btoProjects = dataManager.getAll(BTOProject.class);
        
        for(BTOProject btoProject:btoProjects){
            List<FlatUnit> result = dataManager.getByQuery(FlatUnit.class, 
                flatUnit -> flatUnit.getBTOProject() == btoProject
            );

            Map<FlatType, FlatUnit> flatUnits = new HashMap<>();
            for(FlatUnit flatUnit:result){
                flatUnits.put(flatUnit.getFlatType(), flatUnit);
            }
            btoProject.setFlatUnits(flatUnits);
        }
    }

    @Override
    public void resolveDelete(BTOProject btoProject, DataManager dataManager) {
        List<FlatUnit> flatUnits = btoProject.getFlatUnits();
        for(FlatUnit flatUnit:flatUnits){
            try {
                dataManager.delete(flatUnit);
            } catch (Exception e) {
                throw new RelationshipException(e.getMessage());
            }
        }
    }

    @Override
    public void resolveSave(BTOProject btoProject, DataManager dataManager) {
        List<FlatUnit> flatUnits = btoProject.getFlatUnits();
        for(FlatUnit flatUnit:flatUnits){
            try {
                dataManager.save(flatUnit);
            } catch (Exception e) {
                throw new RelationshipException(e.getMessage());
            }
        }
    }
    
}
