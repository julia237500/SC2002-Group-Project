package manager.interfaces;

import java.util.Map;

import command.Command;

public interface MenuManager {
    public void startDashboardLoop();
    void addCommands(String commandTitle, Map<Integer, Command> commands);
    void back();
    void stopDashboardLoop();
}
