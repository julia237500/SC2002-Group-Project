package form;

import form.field.Field;

/**
 * A generic wrapper class that holds a single value of type {@code T}, representing
 * parsed and validated data from a form input.
 * <p>
 * This class is used to encapsulate the result of processing a {@link Field},
 * enabling consistent handling of form field values regardless of their type.
 * </p>
 * <p>
 * Although full type safety is not always guaranteed when used with generic wildcards
 * (e.g., {@code Map<FormField, FieldData<?>>}), this wrapper provides several benefits:
 * </p>
 * <ul>
 *   <li><b>Semantic clarity</b>: Explicitly represents form data, not just raw values.</li>
 *   <li><b>Extensibility</b>: Can be easily extended without breaking existing code.</li>
 *   <li><b>Consistency</b>: Provides a uniform interface for handling different types of input data.</li>
 *   <li><b>Alignment with HTML form design</b>: Separates the form field definition from the input data, 
 *       similar to how input values are submitted in an HTML form.</li>
 * </ul>
 *
 * @param <T> The type of the parsed value contained in this wrapper.
 * 
 * @see Field
 */
public class FieldData<T> {
    /**
     * The data value wrapped by this object.
     */
    private final T data;

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