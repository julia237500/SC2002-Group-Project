package exception;

public class UserParsingException extends RuntimeException{
    public UserParsingException(String message) {
        super(message);
    }
}
