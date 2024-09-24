package com.example.demo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    // Method to show the login page
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        Model model) {
        System.out.println("Attempting to login with email: " + email);
        User user = userService.getUserByEmail(email); // Call to getUserByEmail
    
        if (user != null) {
            System.out.println("User found: " + user.getEmail());
            if (userService.checkPassword(user, password)) {
                return "redirect:/bookBorrow";
            } else {
                model.addAttribute("errorMessage", "Invalid email or password");
            }
        } else {
            model.addAttribute("errorMessage", "Invalid email or password");
        }
        return "login"; // Return to login page with error
    }
    



    @GetMapping("/addUserForm")
    public String showAddUserForm() {
        return "addUserForm"; // Returns the HTML form page
    }

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
            User newUser = new User();
            newUser.setStudentId(studentId);
            newUser.setStudentName(studentName);
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setBookID(bookID != null ? Integer.parseInt(bookID) : null);
            newUser.setBookName(bookName);
            newUser.setIssuedDate(issuedDate);

            userService.saveUser(newUser); // Save the user using the service
            return "redirect:/users"; // Redirect to the users list page
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Error adding user: " + e.getMessage());
            return "addUserForm"; // Return to the form with error
        }
    }

    // Method to show the signup page
    @GetMapping("/signup")
    public String signup() {
        return "signup";
    }

    // Method to handle signup
    @PostMapping("/signup")
    public String signup(
                         @RequestParam String studentName,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            return "signup"; // Return to the signup page with an error message
        }
    
        // Additional validations...
    
        try {
            User newUser = new User();
            newUser.setStudentName(studentName);
            newUser.setEmail(email);
            newUser.setPassword(password); // Password will be encrypted in the service
            userService.saveUser(newUser); // Save user
    
            return "redirect:/bookBorrow"; // Redirect to the bookBorrow page
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error during signup: " + e.getMessage());
            return "signup"; // Return to the signup page with an error message
        }
    }
    


    // Method to show the list of registered users
    @GetMapping("/users")
    public String showUsers(Model model) {
        List<User> users = userService.getAllUsers(); // Fetch all users from the service
        model.addAttribute("users", users); // Add the list of users to the model
        return "userList"; // Return the userList.html view
    }

    // Method to show the user-edit page for updating details
    @GetMapping("/edit")
    public String editUser(@RequestParam("email") String email, Model model) {
        User userToEdit = userService.getUserByEmail(email); // Use UserService to find the user

        if (userToEdit != null) {
            model.addAttribute("user", userToEdit); // Add the user to the model
            return "editUser"; // Return the editUser.html view
        } else {
            model.addAttribute("errorMessage", "User not found!");
            return "redirect:/users"; // Redirect back to users list if user not found
        }
    }

    @PostMapping("/deleteUser/{id}")
    public String deleteUser(@PathVariable Long id, Model model) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            model.addAttribute("errorMessage", "User not found!");
        }
        return "redirect:/users";
    }

    @PostMapping("/update")
    public String updateUser(
            @RequestParam("oldEmail") String oldEmail,
            @RequestParam("newBookID") int newBookID,
            @RequestParam("newBookName") String newBookName,
            @RequestParam("newIssuedDate") String newIssuedDate,
            Model model) {
        try {
            User userToUpdate = userService.getUserByEmail(oldEmail);

            if (userToUpdate != null) {
                userToUpdate.setBookID(newBookID);
                userToUpdate.setBookName(newBookName);
                userToUpdate.setIssuedDate(newIssuedDate);
                userService.updateUser(userToUpdate);

                return "redirect:/users";
            } else {
                model.addAttribute("errorMessage", "User not found!");
                return "editUser";
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error updating user: " + e.getMessage());
            return "editUser"; // Return to the edit page with error message
        }
    }
}
