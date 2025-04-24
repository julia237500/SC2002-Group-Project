package relationship;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import config.FlatType;
import config.RegistrationStatus;
import exception.DataModelException;
import exception.RelationshipException;
import manager.interfaces.DataManager;
import model.Application;
import model.BTOProject;
import model.Enquiry;
import model.FlatUnit;
import model.OfficerRegistration;
import relationship.resolver.DeleteResolver;
import relationship.resolver.LoadResolver;
import relationship.resolver.SaveResolver;

/**
 * Handles the resolution of relationships for BTOProject objects.
 * This includes loading related models, saving related models, and deleting related models.
 * 
 * Implements the {@link LoadResolver}, {@link SaveResolver}, and {@link DeleteResolver} interfaces
 * to resolve relationships between BTOProject and its related entities like FlatUnit, OfficerRegistration, and Enquiry.
 */
public class BTOProjectRelationshipResolver implements LoadResolver, SaveResolver<BTOProject>, DeleteResolver<BTOProject>{

    /**
     * Resolves the loading of related models when loading BTOProject data.
     * This includes resolving FlatUnit associations, successful OfficerRegistration entries,
     * and adding related HDB officers to the BTOProject.
     * 
     * @param dataManager The DataManager responsible for managing the persistence layer.
     */
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
                try {
                    btoProject.addHDBOfficer(officerRegistration.getHDBOfficer());
                } catch (DataModelException e) {
                    throw new RelationshipException(e.getMessage());
                }
            }
        }
    }


    /**
     * Resolves the deletion of related models when deleting a BTOProject.
     * This includes deleting associated FlatUnits, OfficerRegistrations, and Enquiries.
     * 
     * @param btoProject The BTOProject object to delete related models for.
     * @param dataManager The DataManager responsible for managing the persistence layer.
     * @throws RelationshipException If an error occurs while deleting related models.
     */
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

        final List<OfficerRegistration> officerRegistrations = dataManager.getByQuery(
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

        final List<Enquiry> enquiries = dataManager.getByQuery(
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

        final List<Application> applications = dataManager.getByQuery(
            Application.class, 
            application -> application.getBTOProject() == btoProject
        );

        for(Application application:applications){
            try{
                dataManager.delete(application);
            } catch (Exception e){
                throw new RelationshipException(e.getMessage());
            }
        }
    }

    /**
     * Resolves the saving of related models when saving a BTOProject.
     * This includes saving all associated FlatUnits related to the BTOProject.
     * 
     * @param btoProject The BTOProject object to save related models for.
     * @param dataManager The DataManager responsible for managing the persistence layer.
     * @throws RelationshipException If an error occurs while saving related models.
     */
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
