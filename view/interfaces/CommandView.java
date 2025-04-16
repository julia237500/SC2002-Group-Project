package view.interfaces;

import java.util.Map;

import command.Command;

public interface CommandView {
    void showCommandSelection(String title, Map<Integer, Command> commands);
    int getCommandSelection();
}
