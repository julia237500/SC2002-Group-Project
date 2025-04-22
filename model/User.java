package model;

import config.MaritalStatus;
import config.UserRole;

/**
 * Represents a user in the system. This class stores all essential information
 * about a user, such as personal details, credentials, and their role within the system.
 * <p>
 * Each user is uniquely identified by their NRIC.
 * This class implements the {@link DataModel} interface to support
 * uniform data operations.
 * </p>
 */
public class User implements DataModel{
    @CSVField(index = 0) 
    private String name;

    /**
     * The National Registration Identity Card (NRIC) number of the user,
     * which serves as the unique primary key.
     */
    @CSVField(index = 1)
    private String NRIC;

    @CSVField(index = 2)
    private int age;

    @CSVField(index = 3)
    private MaritalStatus maritalStatus;
    
    @CSVField(index = 4)
    private String password;
    private String backupPassword;

    /**
     * The role of the user in the system (e.g., applicant, HDB officer, HDB manager).
     */
    @CSVField(index = 5)
    private UserRole userRole;

    @SuppressWarnings("unused")
    private User() {}

    /**
     * Constructs a new User with the provided attributes.
     *
     * @param name          The user's name.
     * @param NRIC          The user's unique NRIC.
     * @param age           The user's age.
     * @param maritalStatus The user's marital status.
     * @param password      The user's password.
     * @param userRole      The user's role within the system.
     */
    public User(String name, String NRIC, int age, MaritalStatus maritalStatus, String password, UserRole userRole){
        this.name = name;
        this.NRIC = NRIC;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
        this.userRole = userRole;
    }

    public String getName() {
        return name;
    }

    public String getNRIC() {
        return NRIC;
    }

    public int getAge() {
        return age;
    }

    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        backup();
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    /**
     * Returns the primary key of this data model, which is the NRIC.
     *
     * @return The NRIC of the user.
     *  getPK() returning NRIC in User
     * Unlike other model classes like Enquiry.java and FlatUnit.java, User.java does not need UUID
     * Users are real people, and their NRIC is already a unique, natural identifier (in a Singapore context).
     * No need to generate a synthetic UUID when the NRIC is:
     * - Already guaranteed to be unique,
     * - Constant (doesn’t change over time),
     * - Understandable and traceable (we can look up someone by NRIC easily).
     * So it makes sense for User.getPK() to return the NRIC.
     * getPK() returning UUID in other models (e.g. Enquiry, FlatUnit, etc.):
     * These entities don’t naturally come with a unique, guaranteed ID like an NRIC.
     * We need to generate a UUID for each new instance to ensure:
     * - No two records clash,
     * - Data integrity across operations like save, load, update,
     * - Easy reference in foreign keys (like in @CSVField(foreignKey = true) fields).
     */
    @Override
    public String getPK() {
        return NRIC;
    }

    @Override
    public void backup(){
        this.backupPassword = password;
    }

    @Override
    public void restore(){
        this.password = backupPassword;
    }
}
