package com.example.demo.unsuccessful;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;

import jakarta.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserDetailsServiceImplIntegrationTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
public void testSaveUserWithDuplicateEmail() {
    // Create and save the first user
    User firstUser = new User();
    firstUser.setEmail("duplicate@example.com");
    firstUser.setPassword("password123");
    Set<Role> roles = new HashSet<>();

    // Create a role and save it
    Role role = new Role();
    role.setName("ROLE_USER");
    roleRepository.save(role);
    roles.add(role);
    firstUser.setRoles(roles);
    userDetailsService.saveUser(firstUser); // Save first user

    // Attempt to save a second user with the same email
    User secondUser = new User();
    secondUser.setEmail("duplicate@example.com");
    secondUser.setPassword("password456");
    secondUser.setRoles(roles);

    // Attempt to save the second user
    userDetailsService.saveUser(secondUser); // Save second user without exception handling

    // Assert that the second user is saved successfully
    Optional<User> fetchedUser = userRepository.findByEmail("duplicate@example.com");
    assertTrue(fetchedUser.isPresent(), "User should be saved in the database even with duplicate email");

    // Clean up the database
    userRepository.delete(firstUser);
    roleRepository.delete(role);
}

    
   // Run before each test to ensure database is clean
    @BeforeEach
    public void setUp() {
        // You can clear the repository if needed
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUserWithoutRole() {
        // Create a user object without roles
        User userWithoutRole = new User();
        userWithoutRole.setEmail("norole@example.com");
        userWithoutRole.setPassword("password789");
        // Not setting any roles to simulate the condition of having no role
    
        // Attempt to save the user and expect an exception
        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class, () -> {
            userDetailsService.saveUser(userWithoutRole); // Save the user
        });
    
        // Optionally, you can assert the message of the exception
        assertNotNull(thrown.getMessage(), "Exception message should not be null");
    }
    
    
}
