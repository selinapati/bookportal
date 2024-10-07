package com.example.demo.entity;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int studentId;
    private String studentName;
    private String email;
    private String password; // Add password field
    private int bookID;
    private String bookName;
    private String issuedDate; // Ensure this is used appropriately

    @ManyToMany(fetch = FetchType.EAGER) // Fetch roles eagerly for security
    @JoinTable(
        name = "userRoles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    // Default constructor
    public User() {
    }

    // Constructor
    public User(int studentId, String studentName, String email, String password, Set<Role> roles, int bookID,
            String bookName, String issuedDate) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.password = password; // Initialize password
        this.roles = roles;
        this.bookID = bookID;
        this.bookName = bookName;
        this.issuedDate = issuedDate; // Ensure this is initialized
    }

    // Getters and Setters
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
        return issuedDate; // Corrected from issuedDate() to return the field directly
    }

    public void setIssuedDate(String issuedDate) {
        this.issuedDate = issuedDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // Setter for password
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }


    public User orElse(Object object) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
