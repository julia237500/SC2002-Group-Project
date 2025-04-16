package manager.interfaces;

import java.util.Map;
import java.util.function.Supplier;

import command.Command;

public interface MenuManager {
    public void startDashboardLoop();
    void addCommands(String commandTitle,  Supplier<Map<Integer, Command>> commandGenerator);
    void back();
    void stopDashboardLoop();
}
