package com.vse.librarydb.service;

import com.vse.librarydb.model.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

public class BookService {
    private EntityManagerFactory emf;
    public Validator validator;

    public BookService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }


    public String addBook(String title, String author, int publicationYear, boolean available) {
        EntityManager em = emf.createEntityManager();
        try {
            // Create a new Book
            Book book = new Book(title, author, publicationYear, available);

            // Validate the Book
            Set<ConstraintViolation<Book>> violations = validator.validate(book);
            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
                for (ConstraintViolation<Book> violation : violations) {
                    errorMessage.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("\n");
                }
                return errorMessage.toString();
            }

            // Persist the book
            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();

            return "Book added successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while adding the book: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    public List<Book> getAllBooks() {
        EntityManager em = emf.createEntityManager();
        List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        em.close();
        return books;
    }

    public void close() {
        emf.close();
    }
    // BookService.java
    public Book getBookById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }
    public void updateBook(Book book) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Fetch the existing book from the database
            Book existingBook = em.find(Book.class, book.getId());
            if (existingBook != null) {
                // Update only the fields that are being changed
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublicationYear(book.getPublicationYear());
                // The loans relationship remains intact as the book's ID is unchanged
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}