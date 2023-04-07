package com.example.firebaseregister;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class UserAccount {

    public UserAccount() {
    }
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }
    public String idToken;
    public String getEmailId() { return emailId; }
    public void setEmailId(String emailId) { this.emailId = emailId; }
    private String emailId;
    public String getPassword() { return password; }
    public void setPassword(String password) {this.password = password; }
    public String password;

    private String name;
    public void setName(String name) { this.name = name; }

    private String department;
    public void setDepartment(String department) { this.department = department;}
    private String grade;
    public void setGrade(String grade) {this.grade = grade;}
    private String birth;
    public void setBirth(String birth) {this.birth = birth;}
}