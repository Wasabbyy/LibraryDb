package com.vse.librarydb.service;

import com.vse.librarydb.model.Reader;

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

public class ReaderService {
    private static final Logger logger = Logger.getLogger(ReaderService.class.getName());
    private EntityManagerFactory emf;
    public Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public ReaderService() {
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

    public String validateReader(String firstName, String lastName, String email) {
        Reader reader = new Reader(firstName, lastName, email);
        Set<ConstraintViolation<Reader>> violations = validator.validate(reader);

        if (!violations.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
            for (ConstraintViolation<Reader> violation : violations) {
                errorMessage.append(violation.getPropertyPath())
                        .append(": ")
                        .append(violation.getMessage())
                        .append("\n");
            }
            return errorMessage.toString();
        }
        return "Validation successful";
    }

    public String addReader(String firstName, String lastName, String email) {
        if (!dbAvailable) {
            return "Database unavailable. Please try again later.";
        }

        EntityManager em = emf.createEntityManager();
        try {
            Reader reader = new Reader(firstName, lastName, email);

            String validationResult = validateReader(firstName, lastName, email);
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            em.persist(reader);
            em.getTransaction().commit();
            return "Reader added successfully!";
        } catch (Exception e) {
            handleDatabaseError(e);
            return "An error occurred while adding the reader: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public String updateReader(Reader reader) {
        if (!dbAvailable) {
            return "Database unavailable. Please try again later.";
        }

        EntityManager em = emf.createEntityManager();
        try {
            String validationResult = validateReader(reader.getFirstName(), reader.getLastName(), reader.getEmail());
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            Reader existingReader = em.find(Reader.class, reader.getId());
            if (existingReader != null) {
                existingReader.setFirstName(reader.getFirstName());
                existingReader.setLastName(reader.getLastName());
                existingReader.setEmail(reader.getEmail());
                em.getTransaction().commit();
                return "Reader updated successfully!";
            } else {
                return "Reader not found!";
            }
        } catch (Exception e) {
            handleDatabaseError(e);
            return "An error occurred while updating the reader: " + e.getMessage();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public List<Reader> getAllReaders() {
        if (!dbAvailable) {
            return Collections.emptyList();
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
        } catch (Exception e) {
            handleDatabaseError(e);
            return Collections.emptyList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public Reader getReaderById(Long id) {
        if (!dbAvailable) {
            return null;
        }

        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reader.class, id);
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
    public boolean deleteReader(int readerId) {
        if (!dbAvailable) {
            return false;
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reader reader = em.find(Reader.class, (long) readerId);
            if (reader != null) {
                em.remove(reader);
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