package com.vse.librarydb.service;

import com.vse.librarydb.model.Reader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.List;
import java.util.Set;

public class ReaderService {
    private EntityManagerFactory emf;
    public Validator validator;

    public ReaderService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    // Add this new validation method
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
        EntityManager em = emf.createEntityManager();
        try {
            Reader reader = new Reader(firstName, lastName, email);

            // Validate the Reader object
            String validationResult = validateReader(firstName, lastName, email);
            if (!validationResult.equals("Validation successful")) {
                return validationResult;
            }

            em.getTransaction().begin();
            em.persist(reader);
            em.getTransaction().commit();
            return "Reader added successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while adding the reader: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    // Update this method to return String
    public String updateReader(Reader reader) {
        EntityManager em = emf.createEntityManager();
        try {
            // Validate before updating
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
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return "An error occurred while updating the reader: " + e.getMessage();
        } finally {
            em.close();
        }
    }
    public List<Reader> getAllReaders() {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public Reader getReaderById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reader.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
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