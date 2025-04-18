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

    public String addReader(String firstName, String lastName, String email) {
        EntityManager em = emf.createEntityManager();
        try {
            // Create a Reader object
            Reader reader = new Reader(firstName, lastName, email);

            // Validate the Reader object
            Set<ConstraintViolation<Reader>> violations = validator.validate(reader);
            if (!violations.isEmpty()) {
                // Collect error messages
                StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
                for (ConstraintViolation<Reader> violation : violations) {
                    errorMessage.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("\n");
                }
                return errorMessage.toString(); // Return errors to the caller
            }

            // Persist the Reader if valid
            em.getTransaction().begin();
            em.persist(reader);
            em.getTransaction().commit();
            return "Reader added successfully!";
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while adding the reader.";
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

    public void updateReader(Reader reader) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reader existingReader = em.find(Reader.class, reader.getId());
            if (existingReader != null) {
                existingReader.setFirstName(reader.getFirstName());
                existingReader.setLastName(reader.getLastName());
                existingReader.setEmail(reader.getEmail());
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
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