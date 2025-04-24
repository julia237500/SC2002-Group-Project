package model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as part of a CSV-serializable data model.
 * <p>
 * This annotation is used to map fields in a model class to specific
 * positions (indices) in a CSV file, allowing dynamic object instantiation.
 * </p>
 * <p>
 * The annotation is retained at runtime to allow reflective access,
 * typically used during dynamic CSV parsing and object instantiation.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CSVField {
    /**
     * The index (column) of the field in the CSV.
     * 
     * @return the index of the field in the CSV
     */
    int index(); 

    /**
     * Indicates whether this field represents the actual object associated with a foreign key.
     * <p>
     * If {@code true}, the field is not just a primitive key (e.g., ID),
     * but the resolved reference to another {@code DataModel} object.
     * This allows the deserializer to inject the actual object during instantiation.
     * </p>
     * Default is {@code false}.
     *
     * @return {@code true} if this field is a resolved foreign object, {@code false} otherwise
     */
    boolean foreignKey() default false;
}