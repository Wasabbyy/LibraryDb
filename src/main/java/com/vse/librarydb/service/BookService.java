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

    // Add validation method
    public String validateBook(String title, String author, int publicationYear) {
        Book book = new Book(title, author, publicationYear, true);
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
        return "Validation successful";
    }

    public String addBook(String title, String author, int publicationYear, boolean available) {
        EntityManager em = emf.createEntityManager();
        try {
            Book book = new Book(title, author, publicationYear, available);

            // Validate before adding
            String validationResult = validateBook(title, author, publicationYear);
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

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

    // Update to return String
    public String updateBook(Book book) {
        EntityManager em = emf.createEntityManager();
        try {
            // Validate before updating
            String validationResult = validateBook(book.getTitle(), book.getAuthor(), book.getPublicationYear());
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            Book existingBook = em.find(Book.class, book.getId());
            if (existingBook != null) {
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublicationYear(book.getPublicationYear());
                em.getTransaction().commit();
                return "Book updated successfully!";
            } else {
                return "Book not found!";
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return "An error occurred while updating the book: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    public List<Book> getAllBooks() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } finally {
            em.close();
        }
    }

    public Book getBookById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}