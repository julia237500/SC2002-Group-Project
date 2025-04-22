package exception;

/**
         * throws a custom exception when string parsing fails.
         * Use a custom exception, because the built-in ones like
         * 'throw new IllegalArgumentException("Invalid status")' is too generic, loses semantic meaning
         * Must use the 'new' keyword because exceptions are objects
         * and like any other object in Java, you must instantiate them with 'new' before use.
         * 'throw' expects an instance – you need a concrete object.
         * 'new' enables customisation by letting us pass a detailed error message ("Cannot parse...") at creation time.
         */
public class EnumParsingException extends RuntimeException{
    public EnumParsingException(String message) {
        super(message);
    }
}
