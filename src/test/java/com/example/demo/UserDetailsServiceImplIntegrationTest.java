package com.example.demo;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;

@SpringBootTest
public class UserDetailsServiceImplIntegrationTest {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // test 1
    @Test
    public void testSaveUserIntegration() {
        // Create a user object
        User user = new User();
        user.setEmail("newuser@example.com");
        user.setPassword("rawpassword");  // Password before encoding

        // Call the saveUser method (this method should handle password encoding)
        User savedUser = userDetailsService.saveUser(user);

        // Assert that the user was saved successfully
        assertNotNull(savedUser);
        assertNotNull(savedUser.getId());  // Ensure the user ID is generated

        // Verify that the password was encoded properly
        assertTrue(passwordEncoder.matches("rawpassword", savedUser.getPassword()));

        // Clean up the test data from the database
        userRepository.delete(savedUser);
    }

    // test2
    @Test
    public void testLoadUserByUsernameIntegration() {
        // Create and save a test role
        Role role = new Role();
        role.setName("ROLE_USER");
        roleRepository.save(role);  // Save the role first
    
        // Create and save a test user with the saved role
        User user = new User();
        user.setEmail("testlogin@example.com");
        user.setPassword("password123");
    
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);
    
        userRepository.save(user);  // Now save the user
    
        // Load the user via the UserDetailsService
        UserDetails userDetails = userDetailsService.loadUserByUsername("testlogin@example.com");
    
        // Assert that user was loaded and roles are correct
        assertNotNull(userDetails);
        assertEquals("testlogin@example.com", userDetails.getUsername());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    
        // Clean up the database
        userRepository.delete(user);
        roleRepository.delete(role);  // Clean up the role as well
    }
    
// test 3
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Attempt to load a non-existent user
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("nonexistent@example.com");
        });

        // Verify the exception message
        String expectedMessage = "User not found with email: nonexistent@example.com";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


//    test 4
    @Test
    public void testSaveUserWithRoleIntegration() {
        // Create a role and save it to the database
        Role role = new Role();
        role.setName("ROLE_ADMIN");
        roleRepository.save(role);  // Save the role before associating it with the user

        // Create a user object and associate the saved role
        User user = new User();
        user.setEmail("userrole@example.com");
        user.setPassword("userrolepassword");

        Set<Role> roles = new HashSet<>();
        roles.add(role);  // Add the saved role to the user's roles
        user.setRoles(roles);

        // Call the saveUser method
        User savedUser = userDetailsService.saveUser(user);

        // Verify that the user was saved and the role was correctly set
        assertNotNull(savedUser);
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(r -> r.getName().equals("ROLE_ADMIN")));

        // Clean up the database
        userRepository.delete(savedUser);
        roleRepository.delete(role);  // Clean up the role as well
    }
}


