package view.terminal;

import config.FlatType;
import model.BTOProject;
import model.User;
import view.interfaces.BTOProjectView;

public class TerminalBTOProjectView extends AbstractTerminalView implements BTOProjectView{
    public void showBTOProjectDetailRestricted(BTOProject btoProject, FlatType[] flatTypesToShow){
        showTitle("BTO Project Detail");
        System.out.println("""
                Name                  : %s
                Neighborhood          : %s
                Application Period    : %s - %s
                ----------------------------------------

                %s
                
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getNeighborhood(), 
                    btoProject.getOpeningDate(), 
                    btoProject.getClosingDate(),
                    getFlatDetailString(btoProject, flatTypesToShow)
                ));
    }

    private String getFlatDetailString(BTOProject btoProject, FlatType[] flatTypesToShow){
        StringBuilder sb = new StringBuilder();

        for(FlatType flatType:flatTypesToShow){
            sb.append("Number of %s : %d\n".formatted(
                flatType.getStoredString(), 
                btoProject.getFlatNum(flatType))
            );

            sb.append("Price of %s  : %d\n".formatted(
                flatType.getStoredString(), 
                btoProject.getFlatPrice(flatType))
            );
        }
        return sb.toString();
    }

    public void showBTOProjectDetailFull(BTOProject btoProject){
        showTitle("BTO Project Detail");
        System.out.println("""
                Name                  : %s
                Neighborhood          : %s
                Application Period    : %s - %s
                ----------------------------------------

                %s
                ----------------------------------------
                Manager               : %s
                Number of Officers    : %d / %d
                Officers              : %s
                Visibility            : %s
                
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getNeighborhood(), 
                    btoProject.getOpeningDate(), 
                    btoProject.getClosingDate(),
                    getFlatDetailString(btoProject, FlatType.values()),
                    btoProject.getHDBManager().getName(),
                    btoProject.getHDBOfficers().size(),
                    btoProject.getHDBOfficerLimit(),
                    getHDBOFficerString(btoProject),
                    btoProject.isVisible() ? "Visible" : "Hidden"
                ));
    }

    private String getHDBOFficerString(BTOProject btoProject){
        StringBuilder sb = new StringBuilder();

        for(User officer:btoProject.getHDBOfficers()){
            sb.append(officer.getName()).append(", ");
        }

        if(sb.length() > 0){
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}
