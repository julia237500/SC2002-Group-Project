package model;

import config.MaritalStatus;
import config.UserRole;

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

    @SuppressWarnings("unused")
    private User() {}

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
        backupPassword = this.password;
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    @Override
    public String getPK() {
        return NRIC;
    }

    public void backup(){
        this.backupPassword = password;
    }

    public void restore(){
        this.password = backupPassword;
    }
}
