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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoanService {
    private static final Logger logger = Logger.getLogger(LoanService.class.getName());
    private EntityManagerFactory emf;
    private Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public LoanService() {
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

    public String addLoan(Reader reader, Book book, LocalDate loanDate, int rentPeriodDays) {
        if (!dbAvailable) {
            return "Database unavailable. Please try again later.";
        }

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
                return errorMessage.toString();
            }

            book.setAvailable(false);

            em.getTransaction().begin();
            em.persist(loan);
            em.merge(book);
            em.getTransaction().commit();

            return "Loan added successfully!";
        } catch (Exception e) {
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
            return "Database unavailable. Please try again later.";
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Loan loan = em.find(Loan.class, loanId);
            if (loan == null) {
                return "Loan not found with ID: " + loanId;
            }

            if (loan.getReturnDate() != null) {
                return "This book has already been returned";
            }

            loan.setReturnDate(LocalDate.now());
            LocalDate dueDate = loan.getLoanDate().plusDays(loan.getRentPeriodDays());
            loan.setDelayed(LocalDate.now().isAfter(dueDate));

            Book book = loan.getBook();
            book.setAvailable(true);

            if (note != null && !note.trim().isEmpty()) {
                book.setNote(note.trim());
            }

            em.merge(loan);
            em.merge(book);
            em.getTransaction().commit();

            return "Book returned successfully!";
        } catch (Exception e) {
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
            return Collections.emptyList();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Loan l", Loan.class).getResultList();
        } catch (Exception e) {
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
            return Collections.emptyList();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Loan l WHERE l.book = :book", Loan.class)
                    .setParameter("book", book)
                    .getResultList();
        } catch (Exception e) {
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
            return Collections.emptyList();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT l FROM Loan l WHERE l.reader = :reader", Loan.class)
                    .setParameter("reader", reader)
                    .getResultList();
        } catch (Exception e) {
            handleDatabaseError(e);
            return Collections.emptyList();
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

    public void close() {
        if (connectionMonitor != null) {
            connectionMonitor.shutdown();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}