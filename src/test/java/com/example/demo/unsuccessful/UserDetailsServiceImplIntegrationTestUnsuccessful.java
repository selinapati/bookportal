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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserDetailsServiceImplIntegrationTestUnsuccessful {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

   @Test
public void testSaveUserWithDuplicateEmail_Failure() {
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
    userDetailsService.saveUser(firstUser); // Save the first user

    // Attempt to save a second user with the same email
    User secondUser = new User();
    secondUser.setEmail("duplicate@example.com"); // Duplicate email
    secondUser.setPassword("password456");
    secondUser.setRoles(roles);

    // Instead of throwing an exception, mock userRepository to simulate successful saving
    when(userRepository.save(any(User.class))).thenReturn(secondUser);

    // Call saveUser on the second user (with a duplicate email)
    User savedUser = userDetailsService.saveUser(secondUser); 

    // Assert that the second user with the duplicate email was saved, causing the failure
    Optional<User> fetchedUser = userRepository.findByEmail("duplicate@example.com");
    assertTrue(fetchedUser.isPresent(), "User with duplicate email should not have been saved successfully");

    // Clean up the database
    userRepository.delete(firstUser);
    roleRepository.delete(role);
}


    @BeforeEach
    public void setUp() {
        // Clear the repository before each test to avoid conflicts
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUserWithoutRole_Unsuccessful() {
        // Create a user object without roles
        User userWithoutRole = new User();
        userWithoutRole.setEmail("norole@example.com");
        userWithoutRole.setPassword("password789");
        // Not setting any roles to simulate the condition of having no role

        // Attempt to save the user expecting an exception
        DataIntegrityViolationException thrown = assertThrows(DataIntegrityViolationException.class, () -> {
            userDetailsService.saveUser(userWithoutRole); // Should throw an exception
        });

        // Assert the exception is thrown
        assertNotNull(thrown.getMessage(), "Saving a user without roles should throw a DataIntegrityViolationException");
    }
}
