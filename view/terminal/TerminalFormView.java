package view.terminal;

import form.field.Field;
import view.interfaces.FormView;

public class TerminalFormView extends AbstractTerminalView implements FormView{
    public void show(String title){
        showTitle(title);
    }

    public String getInput(Field<?> field){
        showFieldPrompt(field);
        return sc.nextLine().trim();
    }

    private void showFieldPrompt(Field<?> field){
        System.out.print(field.getName());
        if(field.getConstraint() != null) System.out.print(String.format(" (%s)", field.getConstraint()));
        System.out.print(": ");
        if(field.getOriginalValue() != null) System.out.print(String.format("%s -> (Blank to keep original value) ", field.getOriginalValue()));
    }
}
