package com.vse.librarydb.service;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LoanService {
    private static final Logger logger = LogManager.getLogger(LoanService.class);
    private EntityManagerFactory emf;
    private Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public LoanService() {
        try {
            logger.info("Initializing LoanService...");
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

    public String addLoan(Reader reader, Book book, LocalDate loanDate, int rentPeriodDays) {
        if (!dbAvailable) {
            logger.warn("Attempt to add loan while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to add new loan for book ID: {} to reader ID: {}", book.getId(), reader.getId());
        EntityManager em = emf.createEntityManager();
        try {
            Loan loan = new Loan(reader, book, loanDate, rentPeriodDays);

            Set<ConstraintViolation<Loan>> violations = validator.validate(loan);
            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
                for (ConstraintViolation<Loan> violation : violations) {
                    errorMessage.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("\n");
                }
                logger.warn("Loan validation failed: {}", errorMessage);
                return errorMessage.toString();
            }

            book.setAvailable(false);

            em.getTransaction().begin();
            em.persist(loan);
            em.merge(book);
            em.getTransaction().commit();
            logger.info("Successfully added loan with ID: {}", loan.getId());
            return "Loan added successfully!";
        } catch (Exception e) {
            logger.error("Error adding loan for book ID: {} to reader ID: {}", book.getId(), reader.getId(), e);
            handleDatabaseError(e);
            return "An error occurred while creating the loan: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public String returnBook(Long loanId, String note) {
        if (!dbAvailable) {
            logger.warn("Attempt to return book while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to return book for loan ID: {}", loanId);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Loan loan = em.find(Loan.class, loanId);
            if (loan == null) {
                logger.warn("Loan not found with ID: {}", loanId);
                return "Loan not found with ID: " + loanId;
            }

            if (loan.getReturnDate() != null) {
                logger.warn("Book already returned for loan ID: {}", loanId);
                return "This book has already been returned";
            }

            loan.setReturnDate(LocalDate.now());
            LocalDate dueDate = loan.getLoanDate().plusDays(loan.getRentPeriodDays());
            boolean isDelayed = LocalDate.now().isAfter(dueDate);
            loan.setDelayed(isDelayed);

            Book book = loan.getBook();
            book.setAvailable(true);

            if (note != null && !note.trim().isEmpty()) {
                book.setNote(note.trim());
            }

            em.merge(loan);
            em.merge(book);
            em.getTransaction().commit();

            if (isDelayed) {
                logger.warn("Book returned late for loan ID: {}", loanId);
            } else {
                logger.info("Book returned on time for loan ID: {}", loanId);
            }
            return "Book returned successfully!";
        } catch (Exception e) {
            logger.error("Error returning book for loan ID: {}", loanId, e);
            handleDatabaseError(e);
            return "An error occurred while returning the book: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Loan> getAllLoans() {
        if (!dbAvailable) {
            logger.warn("Attempt to get all loans while database unavailable");
            return Collections.emptyList();
        }

        logger.debug("Fetching all loans");
        EntityManager em = emf.createEntityManager();
        try {
            List<Loan> loans = em.createQuery("SELECT l FROM Loan l", Loan.class).getResultList();
            logger.debug("Fetched {} loans", loans.size());
            return loans;
        } catch (Exception e) {
            logger.error("Error fetching all loans", e);
            handleDatabaseError(e);
            return Collections.emptyList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Loan> getLoansByBook(Book book) {
        if (!dbAvailable) {
            logger.warn("Attempt to get loans by book while database unavailable");
            return Collections.emptyList();
        }

        logger.debug("Fetching loans for book ID: {}", book.getId());
        EntityManager em = emf.createEntityManager();
        try {
            List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.book = :book", Loan.class)
                    .setParameter("book", book)
                    .getResultList();
            logger.debug("Fetched {} loans for book ID: {}", loans.size(), book.getId());
            return loans;
        } catch (Exception e) {
            logger.error("Error fetching loans for book ID: {}", book.getId(), e);
            handleDatabaseError(e);
            return Collections.emptyList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Loan> getLoansByReader(Reader reader) {
        if (!dbAvailable) {
            logger.warn("Attempt to get loans by reader while database unavailable");
            return Collections.emptyList();
        }

        logger.debug("Fetching loans for reader ID: {}", reader.getId());
        EntityManager em = emf.createEntityManager();
        try {
            List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.reader = :reader", Loan.class)
                    .setParameter("reader", reader)
                    .getResultList();
            logger.debug("Fetched {} loans for reader ID: {}", loans.size(), reader.getId());
            return loans;
        } catch (Exception e) {
            logger.error("Error fetching loans for reader ID: {}", reader.getId(), e);
            handleDatabaseError(e);
            return Collections.emptyList();
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

    public void close() {
        logger.info("Shutting down LoanService resources");
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}