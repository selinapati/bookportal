package com.example.demo.controller;

import com.example.demo.entity.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/addBooks")
    public String addBooks() {
        return "addBooks"; // Make sure this view exists
    }

    @PostMapping("/addBooks")
    public String addBook(@RequestParam String booksId,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int availableCopies,
            @RequestParam(required = false) String imageUrl,
            Model model) {
        try {
            // Check if the book already exists
            if (bookService.findBooksById(booksId).isPresent()) {
                model.addAttribute("errorMessage", "Book with this ID already exists!");
                return "addBooks"; // Return to the form if the book exists
            } else {

                // Create a new Book instance and populate fields
                Book newBook = new Book();
                newBook.setBooksId(booksId); // Use setBooksId
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setAvailableCopies(availableCopies);
                newBook.setImageUrl(imageUrl != null ? imageUrl : "/img/book1.jpg"); // Default image if not provided

                // Save the book via the service layer
                bookService.saveBook(newBook);
                model.addAttribute("successMessage", "Book added successfully!");
                return "redirect:/admin"; // Redirect to the books list after successful addition
            }
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Error adding book: " + e.getMessage());
            return "addBooks"; // Return to the form with an error message
        }
    }

    @GetMapping("/allBooks")
    public String showAllBooks(Model model) {
        List<Book> books = bookService.getAllBooks(); // Fetch all books from the service
        model.addAttribute("books", books); // Add books to the model
        return "allBooks"; // Return the Thymeleaf view
    }

    // Show book list page
    @GetMapping("/bookBorrow")
    public String showBooks(Model model) {
        List<Book> bookBorrow = bookService.getAllBooks(); // Fetch the list of books
        model.addAttribute("bookBorrow", bookBorrow); // Add books to the model
        return "bookBorrow"; // Return the Thymeleaf view
    }

     @PostMapping("/borrowBook")
    public String borrowBook(@RequestParam String booksId, RedirectAttributes redirectAttributes) {
        boolean success = bookService.borrowBook(booksId);
        return "redirect:/bookBorrow"; // Redirect to the bookBorrow page
    }

    @PostMapping("/books/delete")
    public String deleteBookById(@RequestParam String booksId, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteBook(booksId);
            redirectAttributes.addFlashAttribute("successMessage", "Book deleted successfully!"); // Add success message
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting book: " + e.getMessage()); // Add error message
        }
        return "redirect:/allBooks"; // Redirect to the book list page
    }
}
