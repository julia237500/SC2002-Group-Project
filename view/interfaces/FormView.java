package view.interfaces;

import form.field.Field;

public interface FormView {
    void show(String title);
    String getInput(Field<?> field);
}
