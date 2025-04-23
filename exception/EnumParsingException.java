package exception;

/**
 * Thrown when an custom {@code Enum} cannot be parsed from file data.
 * <p>
 * This exception is typically thrown when there is a misalignment in the data, such as incorrect
 * column order in a CSV file, or when the input data cannot be properly mapped to an {@link Enum} value.
 * It can also be thrown in cases of data corruption or other issues that prevent successful parsing.
 */
public class EnumParsingException extends RuntimeException{
    public EnumParsingException(String message) {
        super(message);
    }
}
