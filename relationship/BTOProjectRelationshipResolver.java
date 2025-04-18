package relationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.RegistrationStatus;
import exception.RelationshipException;
import manager.interfaces.DataManager;
import model.BTOProject;
import model.Enquiry;
import model.FlatUnit;
import model.OfficerRegistration;
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

            List<OfficerRegistration> officerRegistrations = dataManager.getByQueries(OfficerRegistration.class, List.of(
                officerRegistration -> officerRegistration.getBTOProject() == btoProject,
                officerRegistration -> officerRegistration.getRegistrationStatus() == RegistrationStatus.SUCCESSFUL
            ));

            for(OfficerRegistration officerRegistration:officerRegistrations){
                btoProject.addHDBOfficer(officerRegistration.getHDBOfficer());
            }
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

        List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
            OfficerRegistration.class, 
            registration -> registration.getBTOProject() == btoProject
        );

        for(OfficerRegistration officerRegistration:officerRegistrations){
            try{
                dataManager.delete(officerRegistration);
            } catch (Exception e){
                throw new RelationshipException(e.getMessage());
            }
        }

        List<Enquiry> enquiries = dataManager.getByQuery(
            Enquiry.class, 
            enquiry -> enquiry.getBTOProject() == btoProject
        );

        for(Enquiry enquiry:enquiries){
            try{
                dataManager.delete(enquiry);
            } catch (Exception e){
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
