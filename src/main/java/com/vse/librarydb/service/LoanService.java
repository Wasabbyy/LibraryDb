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

    public void addLoan(Reader reader, Book book, LocalDate loanDate, LocalDate returnDate) {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Loan loan = new Loan(reader, book, loanDate, returnDate);
        em.persist(loan);
        em.getTransaction().commit();
        em.close();
    }

    public List<Loan> getAllLoans() {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l", Loan.class).getResultList();
        em.close();
        return loans;
    }

    public void close() {
        emf.close();
    }
}