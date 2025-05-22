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

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookService {
    private static final Logger logger = Logger.getLogger(BookService.class.getName());
    private EntityManagerFactory emf;
    public Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public BookService() {
        try {
            emf = Persistence.createEntityManagerFactory("LibraryDBPU");
            ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
            this.validator = factory.getValidator();
            startConnectionMonitor();
        } catch (PersistenceException e) {
            logger.log(Level.SEVERE, "Failed to initialize database connection", e);
            dbAvailable = false;
            startConnectionMonitor();
        }
    }

    private void startConnectionMonitor() {
        if (connectionMonitor != null && !connectionMonitor.isShutdown()) {
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
                dbAvailable = false;
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

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
        if (!dbAvailable) {
            return "Database unavailable. Please try again later.";
        }

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
            return "Book added successfully!";
        } catch (Exception e) {
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
            return "Database unavailable. Please try again later.";
        }

        EntityManager em = emf.createEntityManager();
        try {
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
            return Collections.emptyList();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        } catch (Exception e) {
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
            return null;
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } catch (Exception e) {
            handleDatabaseError(e);
            return null;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private void handleDatabaseError(Exception e) {
        logger.log(Level.SEVERE, "Database error occurred", e);
        dbAvailable = false;
        startConnectionMonitor();
    }

    public boolean isDatabaseAvailable() {
        return dbAvailable;
    }
    public boolean deleteBook(int bookId) {
        if (!dbAvailable) {
            return false;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Book book = em.find(Book.class, (long) bookId);
            if (book != null) {
                em.remove(book);
                em.getTransaction().commit();
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            handleDatabaseError(e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void close() {
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}