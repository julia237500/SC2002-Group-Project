package form;


/**
 * A generic wrapper class that holds a single value of type {@code T}.
 * 
 * <p>This class is typically used to encapsulate the parsed result of a form field,
 * allowing consistent handling of field data across different field types.</p>
 * 
 * @param <T> The type of the data being wrapped.
 */
public class FieldData<T> {

    /**
     * The data value wrapped by this object.
     */
    private T data;

    /**
     * Constructs a {@code FieldData} object with the given data.
     *
     * @param data The data to wrap.
     */
    public FieldData(T data){
        this.data = data;
    }

    /**
     * Returns the wrapped data value.
     *
     * @return The data of type {@code T}.
     */
    public T getData() {
        return data;
    }
}

/**
 * Why Wrap the Value in FieldData?
 * In our code, FieldData<T> serves as a wrapper class that holds a value of type T (the data). 
 * Instead of just returning or directly working with the raw value (T), 
 * it is wrapped in an object that provides a structured way to represent and work with that value. 
 * Here's why wrapping might be beneficial:
 * 1. Consistency and Encapsulation:
 * - Wrapping the value helps provide a consistent way of handling data across different field types (like String, LocalDate, etc.).
 * - It encapsulates the data and potentially additional metadata, such as validation results, error messages, or original vs modified values, in the future.
 * 2. Extensibility:
 * - With a wrapper class, we can easily add extra functionality later without breaking our existing code. 
 * 3. Data Processing and Maintainability:
 * - The wrapper makes it easier to handle the data in a unified way, 
 * - especially when working with collections of form fields. 
 * - We don't just deal with raw values but with a structured object that could contain more information, improving maintainability.
 * 4. Separation of Concerns:
 * - By using a wrapper, we keep the logic for parsing, validating, and interacting with the data separate from the data itself. 
 * - For instance, FieldData doesn't care about how the value is parsed or validated, it only holds the result.
 * - It allows us to separate concerns like formatting, validation, and the actual data.
 */
