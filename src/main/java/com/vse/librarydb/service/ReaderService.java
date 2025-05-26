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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ReaderService {
    private static final Logger logger = LogManager.getLogger(ReaderService.class);
    private EntityManagerFactory emf;
    public Validator validator;
    private boolean dbAvailable = true;
    private ScheduledExecutorService connectionMonitor;

    public ReaderService() {
        try {
            logger.info("Initializing ReaderService...");
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

    public String validateReader(String firstName, String lastName, String email) {
        logger.debug("Validating reader: firstName={}, lastName={}, email={}", firstName, lastName, email);
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
            logger.warn("Reader validation failed: {}", errorMessage);
            return errorMessage.toString();
        }
        logger.debug("Reader validation successful");
        return "Validation successful";
    }

    public String addReader(String firstName, String lastName, String email) {
        if (!dbAvailable) {
            logger.warn("Attempt to add reader while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to add new reader: {} {}, email: {}", firstName, lastName, email);
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
            logger.info("Successfully added reader with ID: {}", reader.getId());
            return "Reader added successfully!";
        } catch (Exception e) {
            logger.error("Error adding reader: {} {}, email: {}", firstName, lastName, email, e);
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
            logger.warn("Attempt to update reader while database unavailable");
            return "Database unavailable. Please try again later.";
        }

        logger.info("Attempting to update reader with ID: {}", reader.getId());
        EntityManager em = emf.createEntityManager();
        try {
            String validationResult = validateReader(reader.getFirstName(), reader.getLastName(), reader.getEmail());
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            Reader existingReader = em.find(Reader.class, reader.getId());
            if (existingReader != null) {
                logger.debug("Updating reader details from: {} to: {}", existingReader, reader);
                existingReader.setFirstName(reader.getFirstName());
                existingReader.setLastName(reader.getLastName());
                existingReader.setEmail(reader.getEmail());
                em.getTransaction().commit();
                logger.info("Successfully updated reader with ID: {}", reader.getId());
                return "Reader updated successfully!";
            } else {
                logger.warn("Reader not found with ID: {}", reader.getId());
                return "Reader not found!";
            }
        } catch (Exception e) {
            logger.error("Error updating reader with ID: {}", reader.getId(), e);
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
            logger.warn("Attempt to get all readers while database unavailable");
            return Collections.emptyList();
        }

        logger.debug("Fetching all readers");
        EntityManager em = emf.createEntityManager();
        try {
            List<Reader> readers = em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
            logger.debug("Fetched {} readers", readers.size());
            return readers;
        } catch (Exception e) {
            logger.error("Error fetching all readers", e);
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
            logger.warn("Attempt to get reader by ID while database unavailable");
            return null;
        }

        logger.debug("Fetching reader with ID: {}", id);
        EntityManager em = emf.createEntityManager();
        try {
            Reader reader = em.find(Reader.class, id);
            if (reader == null) {
                logger.debug("No reader found with ID: {}", id);
            } else {
                logger.debug("Found reader with ID: {} - {} {}", id, reader.getFirstName(), reader.getLastName());
            }
            return reader;
        } catch (Exception e) {
            logger.error("Error fetching reader with ID: {}", id, e);
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

    public boolean deleteReader(int readerId) {
        if (!dbAvailable) {
            logger.warn("Attempt to delete reader while database unavailable");
            return false;
        }

        logger.info("Attempting to delete reader with ID: {}", readerId);
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reader reader = em.find(Reader.class, (long) readerId);
            if (reader != null) {
                em.remove(reader);
                em.getTransaction().commit();
                logger.info("Successfully deleted reader with ID: {}", readerId);
                return true;
            } else {
                logger.warn("No reader found with ID: {} to delete", readerId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error deleting reader with ID: {}", readerId, e);
            handleDatabaseError(e);
            return false;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

}