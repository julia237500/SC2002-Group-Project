package view.terminal;

import java.util.List;

import model.BTOProject;
import view.interfaces.BTOProjectView;

public class TerminalBTOProjectView extends AbstractTerminalView implements BTOProjectView{
    public void showBTOProjects(List<BTOProject> btoProjects){
        showTitle("BTO Project List");

        for(int i=0; i<btoProjects.size(); i++){
            System.out.println(String.format("%d. %s", i+1, btoProjects.get(i).getName()));
        }
    }

    public void showBTOProject(BTOProject btoProject){
        showTitle("BTO Project Detail");
        System.out.println(btoProject.toString());
    }
}
