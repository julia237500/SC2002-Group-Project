package view.terminal;

import java.util.List;

import model.BTOProject;
import model.User;
import view.interfaces.BTOProjectView;


/**
 * Terminal-based implementation of the {@link BTOProjectView} interface.
 *
 */
public class TerminalBTOProjectView extends AbstractTerminalView implements BTOProjectView{

    /**
     * Displays a list of BTO projects in the terminal.
     * Each project is shown with an index and its name.
     *
     * @param btoProjects the list of {@link BTOProject} instances to display.
     */
    public void showBTOProjects(List<BTOProject> btoProjects){
        showTitle("BTO Project List");

        for(int i=0; i<btoProjects.size(); i++){
            System.out.println(String.format("%d. %s", i+1, btoProjects.get(i).getName()));
        }
    }

    /**
     * Displays detailed information for a single BTO project.
     * <p>
     * Includes the project's string representation and the names of HDB officers assigned to it.
     * </p>
     *
     * @param btoProject the {@link BTOProject} instance to display.
     */
    public void showBTOProject(BTOProject btoProject){
        showTitle("BTO Project Detail");
        System.out.println(btoProject.toString());
        for(User user:btoProject.getHDBOfficers()){
            System.out.println(user.getName());
        }
    }
}
