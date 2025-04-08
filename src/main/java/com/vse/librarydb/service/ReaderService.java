package com.vse.librarydb.service;

import com.vse.librarydb.model.Reader;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ReaderService {
    private EntityManagerFactory emf;

    public ReaderService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
    }

    public void addReader(String firstName, String lastName, String email) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Reader reader = new Reader(firstName, lastName, email);
            em.persist(reader);
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
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