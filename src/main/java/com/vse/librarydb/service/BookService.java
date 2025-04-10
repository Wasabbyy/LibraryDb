package com.vse.librarydb.service;

import com.vse.librarydb.model.Book;

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
    public void updateBook(Book book) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            // Fetch the existing book from the database
            Book existingBook = em.find(Book.class, book.getId());
            if (existingBook != null) {
                // Update only the fields that are being changed
                existingBook.setTitle(book.getTitle());
                existingBook.setAuthor(book.getAuthor());
                existingBook.setPublicationYear(book.getPublicationYear());
                // The loans relationship remains intact as the book's ID is unchanged
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}