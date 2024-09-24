package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
// @Table (name="users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private int studentId;
    private String studentName;
    private String email;
    private String password;  // Add password field
    private int bookID;
    private String bookName;
    private String issuedDate;
    private String duration;

    // Constructors, getters, and setters

    public User() {}

    public User(int studentId, String studentName, String email, String password, int bookID, String bookName, String issuedDate, String duration) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.password = password;  // Initialize password
        this.bookID = bookID;
        this.bookName = bookName;
        this.issuedDate = issuedDate;
        this.duration = duration;
    }

    // Getters and setters...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;  // Getter for password
    }

    public void setPassword(String password) {
        this.password = password;  // Setter for password
    }

    public int getBookID() {
        return bookID;
    }

    public void setBookID(int bookID) {
        this.bookID = bookID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
