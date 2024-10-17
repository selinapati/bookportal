package com.example.demo.service;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // Fetch all books
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Save a new book
    public Book saveBook(Book book) {
        return bookRepository.save(book);
    }

    // Find book by ID
    public Optional<Book> findBooksById(String booksId) {
        return bookRepository.findByBooksId(booksId);
    }

    // Delete a book by ID
    public void deleteBook(String booksId) {
        Optional<Book> bookOptional = findBooksById(booksId);
        if (bookOptional.isPresent()) {
            bookRepository.delete(bookOptional.get());
        } else {
            // Handle case where book is not found
            System.out.println("Book not found with ID: " + booksId);
        }
    }

    // Borrow a book
    public boolean borrowBook(String booksId) {
        Optional<Book> optionalBook = bookRepository.findByBooksId(booksId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1); // Decrease availability
                bookRepository.save(book); // Save the updated book to the database
                return true; // Borrowing successful
            }
        }
        return false; // Borrowing failed (book not found or no copies available)
    }

}
