package org.example.librarydb.service;

import org.example.librarydb.model.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class BookService {
    private EntityManagerFactory emf;

    public BookService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
    }

    public void addBook(String title, String author, int publicationYear, boolean available) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Book book = new Book(title, author, publicationYear, available);
        em.persist(book);
        em.getTransaction().commit();
        em.close();
    }

    public List<Book> getAllBooks() {
        EntityManager em = emf.createEntityManager();
        List<Book> books = em.createQuery("SELECT b FROM Book b", Book.class).getResultList();
        em.close();
        return books;
    }

    public void close() {
        emf.close();
    }
    // BookService.java
    public Book getBookById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }
}