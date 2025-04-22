package view.terminal;

import config.FlatType;
import model.BTOProject;
import model.User;
import view.interfaces.BTOProjectView;


/**
 * Terminal-based implementation of the {@link BTOProjectView} interface.
 *
 */
public class TerminalBTOProjectView extends AbstractTerminalView implements BTOProjectView{
    public void showBTOProjectDetailRestricted(BTOProject btoProject){
        showTitle("BTO Project Detail");
        System.out.println("""
                Name                  : %s
                Neighborhood          : %s
                Application Period    : %s - %s
                -------------------------------------------

                %s
                -------------------------------------------
                Manager               : %s
                Officers              : %s
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getNeighborhood(), 
                    btoProject.getOpeningDate(), 
                    btoProject.getClosingDate(),
                    getFlatDetailString(btoProject),
                    btoProject.getHDBManager().getName(),
                    getHDBOFficerString(btoProject)
                ));
    }

    private String getFlatDetailString(BTOProject btoProject){
        StringBuilder sb = new StringBuilder();

        sb.append("All Flats: \n\n");

        for(FlatType flatType:FlatType.values()){
            sb.append("%s\n".formatted(
               flatType.getStoredString() 
            ));

            sb.append("Number      : %d\n".formatted(
                btoProject.getFlatNum(flatType))
            );

            sb.append("Price       : %d\n".formatted(
                btoProject.getFlatPrice(flatType))
            );

            sb.append("Eligible if : \n%s\n".formatted(
                flatType.getEligibilityDetail()
            ));
        }
        return sb.toString();
    }

    /**
     * Displays detailed information for a single BTO project.
     * <p>
     * Includes the project's string representation and the names of HDB officers assigned to it.
     * </p>
     *
     * @param btoProject the {@link BTOProject} instance to display.
     */
    public void showBTOProjectDetailFull(BTOProject btoProject){
        showTitle("BTO Project Detail");
        System.out.println("""
                Name                  : %s
                Neighborhood          : %s
                Application Period    : %s - %s
                -------------------------------------------

                %s
                -------------------------------------------
                Manager               : %s
                Number of Officers    : %d / %d
                Officers              : %s
                Visibility            : %s
                """.formatted(
                    btoProject.getName(), 
                    btoProject.getNeighborhood(), 
                    btoProject.getOpeningDate(), 
                    btoProject.getClosingDate(),
                    getFlatDetailString(btoProject),
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

        if(sb.length() == 0){
            return "No officers in charge";
        }

        sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}
