package com.example.demo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        // Add roles here, e.g., user.setRoles(Arrays.asList(new Role("ROLE_USER")));
    }

    // test2
    @Test
    public void testSaveUser() {
        // The real password being passed to passwordEncoder is "password123"
        when(passwordEncoder.encode("password123")).thenReturn("encryptedPassword");
    
        // Mock the behavior of userRepository
        when(userRepository.save(any(User.class))).thenReturn(user);
    
        // Call the saveUser method
        User savedUser = userDetailsService.saveUser(user);
    
        // Verify that passwordEncoder was called with the correct argument
        verify(passwordEncoder).encode("password123");
    
        // Verify that the user was saved
        verify(userRepository).save(user);
    
        // Assert that the saved user's password was correctly encrypted
        assertEquals("encryptedPassword", savedUser.getPassword());
    }
    
// test3
    @Test
    public void testLoadUserByUsername_UserFound() {
        // Mock a User object
        User user = new User();
        user.setEmail("test@example.com");  // Correct syntax
        user.setPassword("password123");    // Correct syntax
    
        // Mock roles for the user
        Set<Role> roles = new HashSet<>();
        Role role = new Role();
        role.setName("ROLE_USER");          // Correct syntax
        roles.add(role);
        user.setRoles(roles);               // Ensure roles are not null
    
        // Mock the behavior of the userRepository
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
    
        // Call the method under test
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");
    
        // Verify that the userDetails returned has the correct email and roles
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
    
        // Verify that roles are correctly converted to GrantedAuthorities
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER")));
    }
    

    // test4
    @Test
    public void testLoadUserByUsername_UserNotFound() {
        // Mock the behavior of userRepository
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // Expect UsernameNotFoundException and capture it
        UsernameNotFoundException thrown = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("unknown@example.com");
        });

        // Assert the message of the exception
        assertEquals("User not found with email: unknown@example.com", thrown.getMessage());
    }
}
