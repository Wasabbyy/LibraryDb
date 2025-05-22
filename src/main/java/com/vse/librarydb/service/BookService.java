package com.vse.librarydb.service;

import com.vse.librarydb.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BookService {
    private static final Logger logger = LogManager.getLogger(BookService.class);
    private EntityManagerFactory emf;
    public Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public BookService() {
        try {
            logger.info("Initializing BookService...");
            emf = Persistence.createEntityManagerFactory("LibraryDBPU");
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.validator = factory.getValidator();
            logger.debug("Validator and EntityManagerFactory initialized successfully");
            startConnectionMonitor();
        } catch (PersistenceException e) {
            logger.error("Failed to initialize database connection", e);
            dbAvailable = false;
            startConnectionMonitor();
        }
    }


    private void startConnectionMonitor() {
        logger.debug("Starting database connection monitor");
        if (connectionMonitor != null && !connectionMonitor.isShutdown()) {
            logger.debug("Shutting down existing connection monitor");
            connectionMonitor.shutdown();
        }

        connectionMonitor = Executors.newSingleThreadScheduledExecutor();
        connectionMonitor.scheduleAtFixedRate(() -> {
            try {
                EntityManager em = emf.createEntityManager();
                em.close();
                if (!dbAvailable) {
                    logger.info("Database connection restored");
                    dbAvailable = true;
                }
            } catch (Exception e) {
                if (dbAvailable) {
                    logger.warn("Database connection lost", e);
                }
                dbAvailable = false;
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public String validateBook(String title, String author, int publicationYear) {
        logger.debug("Validating book: title={}, author={}, year={}", title, author, publicationYear);
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
            logger.warn("Book validation failed: {}", errorMessage);
            return errorMessage.toString();
        }
        logger.debug("Book validation successful");
        return "Validation successful";
    }

    public String addBook(String title, String author, int publicationYear, boolean available) {
        if (!dbAvailable) {
            logger.warn("Attempt to add book while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to add new book: {} by {} ({})", title, author, publicationYear);
        EntityManager em = emf.createEntityManager();
        try {
            Book book = new Book(title, author, publicationYear, available);

            String validationResult = validateBook(title, author, publicationYear);
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            em.persist(book);
            em.getTransaction().commit();
            logger.info("Successfully added book with ID: {}", book.getId());
            return "Book added successfully!";
        } catch (Exception e) {
            logger.error("Error adding book: {} by {} ({})", title, author, publicationYear, e);
            handleDatabaseError(e);
            return "An error occurred while adding the book: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public String updateBook(Book book) {
        if (!dbAvailable) {
            logger.warn("Attempt to update book while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to update book with ID: {}", book.getId());
        EntityManager em = emf.createEntityManager();
        try {
            String validationResult = validateBook(book.getTitle(), book.getAuthor(), book.getPublicationYear());
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            Book existingBook = em.find(Book.class, book.getId());
            if (existingBook != null) {
                logger.debug("Updating book details from: {} to: {}", existingBook, book);
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublicationYear(book.getPublicationYear());
                em.getTransaction().commit();
                logger.info("Successfully updated book with ID: {}", book.getId());
                return "Book updated successfully!";
            } else {
                logger.warn("Book not found with ID: {}", book.getId());
                return "Book not found!";
            }
        } catch (Exception e) {
            logger.error("Error updating book with ID: {}", book.getId(), e);
            handleDatabaseError(e);
            return "An error occurred while updating the book: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Book> getAllBooks() {
        if (!dbAvailable) {
            logger.warn("Attempt to get all books while database unavailable");
            return Collections.emptyList();
        }

        logger.debug("Fetching all books");
        EntityManager em = emf.createEntityManager();
        try {
            List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
            logger.debug("Fetched {} books", books.size());
            return books;
        } catch (Exception e) {
            logger.error("Error fetching all books", e);
            handleDatabaseError(e);
            return Collections.emptyList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Book getBookById(Long id) {
        if (!dbAvailable) {
            logger.warn("Attempt to get book by ID while database unavailable");
            return null;
        }

        logger.debug("Fetching book with ID: {}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Book book = em.find(Book.class, id);
            if (book == null) {
                logger.debug("No book found with ID: {}", id);
            } else {
                logger.debug("Found book with ID: {} - {} by {}", id, book.getTitle(), book.getAuthor());
            }
            return book;
        } catch (Exception e) {
            logger.error("Error fetching book with ID: {}", id, e);
            handleDatabaseError(e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void handleDatabaseError(Exception e) {
        logger.error("Database error occurred", e);
        dbAvailable = false;
        startConnectionMonitor();
    }

    public boolean isDatabaseAvailable() {
        return dbAvailable;
    }

    public boolean deleteBook(int bookId) {
        if (!dbAvailable) {
            logger.warn("Attempt to delete book while database unavailable");
            return false;
        }

        logger.info("Attempting to delete book with ID: {}", bookId);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, (long) bookId);
            if (book != null) {
                em.remove(book);
                em.getTransaction().commit();
                logger.info("Successfully deleted book with ID: {}", bookId);
                return true;
            } else {
                logger.warn("No book found with ID: {} to delete", bookId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deleting book with ID: {}", bookId, e);
            handleDatabaseError(e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void close() {
        logger.info("Shutting down BookService resources");
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}