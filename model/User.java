package model;

import config.MaritalStatus;
import config.UserRole;

public class User {
    private String name;
    private String NRIC;
    private int age;
    private MaritalStatus maritalStatus; 
    private String password;
    private UserRole userRole;

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
        this.password = password;
    }

    public UserRole getUserRole() {
        return userRole;
    }
}
