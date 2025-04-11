package com.vse.librarydb.service;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.time.LocalDate;
import java.util.List;

public class LoanService {
    private EntityManagerFactory emf;

    public LoanService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
    }

    public void addLoan(Reader reader, Book book, LocalDate loanDate) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Set the book as unavailable
        book.setAvailable(false);
        em.merge(book);

        // Create a new loan with a null return date
        Loan loan = new Loan(reader, book, loanDate, null);
        em.persist(loan);

        em.getTransaction().commit();
        em.close();
    }

    public void returnBook(Long loanId) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        // Find the loan by ID
        Loan loan = em.find(Loan.class, loanId);
        if (loan != null && loan.getReturnDate() == null) {
            // Set the return date to today's date
            loan.setReturnDate(LocalDate.now());
            em.merge(loan);

            // Mark the book as available
            Book book = loan.getBook();
            book.setAvailable(true);
            em.merge(book);
        }

        em.getTransaction().commit();
        em.close();
    }

    public List<Loan> getAllLoans() {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l", Loan.class).getResultList();
        em.close();
        return loans;
    }

    public List<Loan> getLoansByReader(Reader reader) {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.reader = :reader", Loan.class)
                .setParameter("reader", reader)
                .getResultList();
        em.close();
        return loans;
    }

    public List<Loan> getLoansByBook(Book book) {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.book = :book", Loan.class)
                .setParameter("book", book)
                .getResultList();
        em.close();
        return loans;
    }

    public void close() {
        emf.close();
    }
}