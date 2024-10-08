package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/")
public class HomeController {
    private final AuthenticationManager authenticationManager;

    public HomeController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private UserService userService; // Inject UserService

    @Autowired
    private UserRepository userRepository;

    // Method to show index page
    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/bookBorrow")
    public String bookBorrow() {
        return "bookBorrow";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password, Model model) {
        try {
            // Debugging output to check email and password
            // System.out.println("Email: " + email);
            // System.out.println("Password: " + password);

            // Create authentication token with email
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, password);

            // Authenticate the user
            Authentication authentication = authenticationManager.authenticate(authToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Redirect to the dashboard or home page
            return "redirect:/bookBorrow";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // Method to handle signup form submission
    @PostMapping("/signup")
    public String signup(@RequestParam String studentName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            Model model) {
        // Validate if password and confirmPassword match
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "signup"; // Return to signup page with an error message
        }

        // Additional validations such as checking if email already exists
        if (userService.findByEmail(email) != null) {
            model.addAttribute("errorMessage", "Email already exists!");
            return "signup"; // Return to signup page if email already exists
        }

        try {
            // Create new user instance and populate fields
            User newUser = new User();
            newUser.setStudentName(studentName);
            newUser.setEmail(email);
            newUser.setPassword(password); // Password should be encrypted in the service layer
            userService.saveUser(newUser); // Save user via service layer

            return "redirect:/login"; // Redirect to login page after successful signup
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error during signup: " + e.getMessage());
            return "signup"; // Return to signup page with an error message
        }
    }

    // Method to show the Add User form page
    @GetMapping("/addUserForm")
    public String showAddUserForm() {
        return "addUserForm"; // Returns the HTML form page for adding users
    }

    // Method to handle adding new users
    @PostMapping("/addUser")
    public String addUser(@RequestParam int studentId,
            @RequestParam String studentName,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String bookID,
            @RequestParam(required = false) String bookName,
            @RequestParam(required = false) String issuedDate,
            Model model) {
        try {
            // Check for existing user by email to prevent duplicates
            if (userService.findByEmail(email) != null) {
                model.addAttribute("errorMessage", "Email already exists!");
                return "addUserForm"; // Return to the form if user already exists
            }

            // Create a new User instance and populate fields
            User newUser = new User();
            newUser.setStudentId(studentId);
            newUser.setStudentName(studentName);
            newUser.setEmail(email);
            newUser.setPassword(password); // Password should be encoded in the service layer
            newUser.setBookID(bookID != null ? Integer.parseInt(bookID) : null);
            newUser.setBookName(bookName);
            newUser.setIssuedDate(issuedDate);

            // Save the user via service layer
            userService.saveUser(newUser);
            return "redirect:/users"; // Redirect to users list after successful addition
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding user: " + e.getMessage());
            return "addUserForm"; // Return to the form with an error message
        }
    }

    // Method to show the list of registered users
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers(); // Fetch all users from the service
        model.addAttribute("users", users); // Add users list to the model
        return "userList"; // Return the userList.html view
    }

    // Method to show the user-edit page for updating details
    @GetMapping("/edit")
    public String editUser(@RequestParam("email") String email, Model model) {
        User userToEdit = userService.findByEmail(email); // Use UserService to find the user

        if (userToEdit != null) {
            model.addAttribute("user", userToEdit); // Add the user to the model
            return "editUser"; // Return the editUser.html view
        } else {
            model.addAttribute("errorMessage", "User not found!");
            return "redirect:/users"; // Redirect back to users list if user not found
        }
    }

    // Method to handle the update form submission
    @PostMapping("/update")
    public String updateUser(@RequestParam("oldEmail") String oldEmail,
            @RequestParam("newBookID") int newBookID,
            @RequestParam("newBookName") String newBookName,
            @RequestParam("newIssuedDate") String newIssuedDate,
            Model model) {
        try {
            User userToUpdate = userService.findByEmail(oldEmail); // Find the user by old email

            if (userToUpdate != null) {
                userToUpdate.setBookID(newBookID); // Update book details
                userToUpdate.setBookName(newBookName);
                userToUpdate.setIssuedDate(newIssuedDate);
                userService.saveUser(userToUpdate); // Save updated user

                return "redirect:/users"; // Redirect to the updated users list
            } else {
                model.addAttribute("errorMessage", "User not found!");
                return "editUser"; // Return to the edit page with error
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
            return "editUser"; // Return to the edit page with error message
        }
    }

    // Method to handle deleting a user by email
    @PostMapping("/deleteUserByEmail")
    public String deleteUserByEmail(@RequestParam String email, Model model) {
        try {
            userService.deleteUserByEmail(email); // Call service to delete user
            return "redirect:/users"; // Redirect back to user list
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error deleting user: " + e.getMessage());
            return "userList"; // Return to the user list with error message
        }
    }

    // Method to handle deleting a user by ID (alternative method)
    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id); // Delete user by ID
            return "redirect:/users";
        } else {
            model.addAttribute("errorMessage", "User not found!");
            return "userList"; // Return to the user list with error message
        }
    }
}
