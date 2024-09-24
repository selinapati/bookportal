package com.example.demo.service;

import java.util.List;

import com.example.demo.entity.User;

public interface UserService {
    void saveUser(User user);
    User getUserByEmail(String email);
    boolean checkPassword(User user, String rawPassword);
    List<User> getAllUsers();
    void updateUser(User user);
    void deleteUser(String email); // Add delete method
}
