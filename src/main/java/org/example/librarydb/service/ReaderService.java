package org.example.librarydb.service;

import org.example.librarydb.model.Reader;

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
        em.getTransaction().begin();
        Reader reader = new Reader(firstName, lastName, email);
        em.persist(reader);
        em.getTransaction().commit();
        em.close();
    }

    public List<Reader> getAllReaders() {
        EntityManager em = emf.createEntityManager();
        List<Reader> readers = em.createQuery("SELECT r FROM Reader r", Reader.class).getResultList();
        em.close();
        return readers;
    }

    public void close() {
        emf.close();
    }
    // ReaderService.java
    public Reader getReaderById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Reader.class, id);
        } finally {
            em.close();
        }
    }
}