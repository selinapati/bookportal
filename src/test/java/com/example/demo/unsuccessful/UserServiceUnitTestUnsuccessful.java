package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserDetailsServiceImpl;

public class UserServiceUnitTestUnsuccessful {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserDetailsServiceImpl userService;

    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setPassword("plainPassword");
    }

    @Test
    public void testCreateUserFailure() {
        // Simulate a DataIntegrityViolationException when saving a user
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any(User.class));

        // Assert that the exception is thrown
        assertThrows(DataIntegrityViolationException.class, () -> {
            userService.saveUser(user);
        });

        // Verify that save was called only once and failed
        verify(userRepository, times(1)).save(user);
    }
}
