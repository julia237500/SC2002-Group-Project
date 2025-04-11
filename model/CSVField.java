package model;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME) // Make sure the annotation is available at runtime
public @interface CSVField {
    int index(); // This will represent the position of the field in the CSV
    boolean foreignKey() default false;
}