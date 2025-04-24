package model;

import config.MaritalStatus;
import config.UserRole;
import manager.CSVDataManager;

/**
 * Represents a user in the system. This class stores all essential information
 * about a user, such as personal details, credentials, and their role within the system.
 */
public class User implements DataModel{
    @CSVField(index = 0) 
    private String name;

    @CSVField(index = 1)
    private String NRIC;

    @CSVField(index = 2)
    private int age;

    @CSVField(index = 3)
    private MaritalStatus maritalStatus;
    
    @CSVField(index = 4)
    private String password;
    private String backupPassword;

    @CSVField(index = 5)
    private UserRole userRole;

    /**
     * Default no-argument constructor used exclusively for reflective instantiation.
     * This constructor is necessary for classes like {@link CSVDataManager} 
     * to create model objects via reflection.
     */
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

    /**
     * Retrieve the name of user.
     * @return the name of user
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieve the NRIC of user.
     * @return the NRIC of user
     */
    public String getNRIC() {
        return NRIC;
    }

    /**
     * Retrieve the age of user.
     * @return the age of user
     */
    public int getAge() {
        return age;
    }

    /**
     * Retrieve the marital status of user.
     * @return the marital status of user
     * 
     * @see MaritalStatus
     */
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    
    /**
     * Retrieve the password of user.
     * @return the password of user
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set the password of user.
     * The current state is backup and can be revert by {@link #restore()}.
     * @param password new password
     */
    public void setPassword(String password) {
        backup();
        this.password = password;
    }

    /**
     * Retrieve the role of user.
     * @return the role of user
     * 
     * @see UserRole
     */
    public UserRole getUserRole() {
        return userRole;
    }

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
