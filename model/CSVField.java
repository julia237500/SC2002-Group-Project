package model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to mark a field as part of a CSV-serializable data model.
 * <p>
 * This annotation is used to map fields in a model class to specific
 * positions (indices) in a CSV file.
 * </p>
 *
 * <p>
 * The annotation is retained at runtime to allow reflective access,
 * typically used during dynamic CSV parsing and object instantiation.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME) // Makes sure the annotation is available at runtime
public @interface CSVField {
    int index(); // This will represent the position of the field in the CSV
    /**
     * Specifies whether this field is a foreign key reference to another object.
     *
     * @return {@code true} if this field is a foreign key, {@code false} otherwise
     */
    boolean foreignKey() default false;
}