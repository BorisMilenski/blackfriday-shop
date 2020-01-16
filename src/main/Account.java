package main;

import java.io.Serializable;

public class Account implements Serializable {
    private String username;
    private String password;
    private boolean isEmployee;

    public Account(String username, String password, boolean isEmployee) {
        this.username = username;
        this.password = password;
        this.isEmployee = isEmployee;
    }

    public Account() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEmployee() {
        return isEmployee;
    }

    public void setEmployee(boolean employee) {
        isEmployee = employee;
    }
}
