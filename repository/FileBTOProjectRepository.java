package repository;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import exception.RepositoryException;
import model.BTOProject;
import model.User;
import parser.BTOProjectParser;
import repository.interfaces.BTOProjectRepository;
import repository.interfaces.UserRepository;
import util.CSVFileReader;

public class FileBTOProjectRepository implements BTOProjectRepository{
    private final static String FILE_PATH = "./data/ProjectList.csv";

    private final Map<String, BTOProject> btoProjects = new HashMap<>();
    private final Map<User, Set<BTOProject>> managerToProjects = new HashMap<>();
    private final Map<User, Set<BTOProject>> officerToProjects = new HashMap<>();

    public FileBTOProjectRepository(UserRepository userRepository){
        try{
            List<List<String>> storedProjectData = CSVFileReader.readFile(FILE_PATH);
            
            for(List<String> projectData:storedProjectData){
                BTOProject btoProject = BTOProjectParser.parseBTOProject(projectData, userRepository);
                btoProjects.put(btoProject.getName(), btoProject);
                updateRelationMaps(btoProject);
            }
        }
        catch(Exception e){
            System.err.println("Fatal: Fail to read BTO Project, TERMINATING. Error: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    public void save(BTOProject btoProject) {
        if(!btoProjects.containsKey(btoProject.getName())){
            btoProjects.put(btoProject.getName(), btoProject);
        }

        try {
            saveAll();
        } catch (Exception e) {
            btoProjects.remove(btoProject.getName());
            throw new RepositoryException(e.getMessage());
        }

        updateRelationMaps(btoProject);
    }

    private void updateRelationMaps(BTOProject btoProject){
        Set<BTOProject> managerProjects = getSetByHDBManager(btoProject.getHDBManager());
        managerProjects.add(btoProject);

        for(User HDBOfficer:btoProject.getHDBOfficers()){
            Set<BTOProject> officerProjects = getSetByHDBManager(HDBOfficer);
            officerProjects.add(btoProject);
        }
    }

    private void saveAll(){
        
    }

    @Override
    public BTOProject getByName(String name) {
        return btoProjects.get(name);
    }

    @Override
    public List<BTOProject> getByHDBManager(User HDBManager) {
        return List.copyOf(getSetByHDBManager(HDBManager));
    }

    private Set<BTOProject> getSetByHDBManager(User HDBManager){
        if(!managerToProjects.containsKey(HDBManager)) managerToProjects.put(HDBManager, new HashSet<>());
        return managerToProjects.get(HDBManager);
    }

    @Override
    public List<BTOProject> getByHDBOfficer(User HDBOfficer) {
        return List.copyOf(getSetByHDBOfficer(HDBOfficer));
    }

    private Set<BTOProject> getSetByHDBOfficer(User HDBOfficer){
        if(!officerToProjects.containsKey(HDBOfficer)) officerToProjects.put(HDBOfficer, new HashSet<>());
        return officerToProjects.get(HDBOfficer);
    }

    @Override
    public List<BTOProject> getAll() {
        return List.copyOf(btoProjects.values());
    }

    @Override
    public void delete(BTOProject btoProject) {
        btoProjects.remove(btoProject.getName());

        try {
            btoProjects.put(btoProject.getName(), btoProject);
            saveAll();
        } catch (Exception e) {
            throw new RepositoryException(e.getMessage());
        }

        cascadeRelationMaps(btoProject);
    }

    private void cascadeRelationMaps(BTOProject btoProject){
        Set<BTOProject> managerProjects = getSetByHDBManager(btoProject.getHDBManager());
        managerProjects.remove(btoProject);

        for(User HDBOfficer:btoProject.getHDBOfficers()){
            Set<BTOProject> officerProjects = getSetByHDBManager(HDBOfficer);
            officerProjects.remove(btoProject);
        }
    }
}
