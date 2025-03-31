package view.interfaces;

public interface MessageView {
    void info(String message);
    void success(String message);
    void error(String message);
    void failure(String message);
}
