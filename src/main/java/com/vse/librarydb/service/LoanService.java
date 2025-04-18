package com.vse.librarydb.service;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class LoanService {
    private EntityManagerFactory emf;
    private Validator validator;

    public LoanService() {
        emf = Persistence.createEntityManagerFactory("LibraryDBPU");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    public String addLoan(Reader reader, Book book, LocalDate loanDate, int rentPeriodDays) {
        EntityManager em = emf.createEntityManager();
        try {
            // Create a new Loan - initially not delayed and no return date
            Loan loan = new Loan(reader, book, loanDate, rentPeriodDays);

            // Validate the Loan
            Set<ConstraintViolation<Loan>> violations = validator.validate(loan);
            if (!violations.isEmpty()) {
                StringBuilder errorMessage = new StringBuilder("Validation errors:\n");
                for (ConstraintViolation<Loan> violation : violations) {
                    errorMessage.append(violation.getPropertyPath())
                            .append(": ")
                            .append(violation.getMessage())
                            .append("\n");
                }
                return errorMessage.toString();
            }

            // Mark book as unavailable
            book.setAvailable(false);

            // Persist the loan
            em.getTransaction().begin();
            em.persist(loan);
            em.merge(book); // Update book status
            em.getTransaction().commit();

            return "Loan added successfully!";
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return "An error occurred while creating the loan: " + e.getMessage();
        } finally {
            em.close();
        }
    }


    public String returnBook(Long loanId) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Find the loan by ID
            Loan loan = em.find(Loan.class, loanId);
            if (loan == null) {
                return "Loan not found with ID: " + loanId;
            }

            if (loan.getReturnDate() != null) {
                return "This book has already been returned";
            }

            // Set the return date to today's date
            loan.setReturnDate(LocalDate.now());

            // Check if the book is returned late
            LocalDate dueDate = loan.getLoanDate().plusDays(loan.getRentPeriodDays());
            loan.setDelayed(LocalDate.now().isAfter(dueDate));

            // Mark the book as available
            Book book = loan.getBook();
            book.setAvailable(true);

            em.merge(loan);
            em.merge(book);
            em.getTransaction().commit();

            return "Book returned successfully!";
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return "An error occurred while returning the book: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    public List<Loan> getAllLoans() {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l", Loan.class).getResultList();
        em.close();
        return loans;
    }public List<Loan> getLoansByBook(Book book) {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.book = :book", Loan.class)
                .setParameter("book", book)
                .getResultList();
        em.close();
        return loans;
    }public List<Loan> getLoansByReader(Reader reader) {
        EntityManager em = emf.createEntityManager();
        List<Loan> loans = em.createQuery("SELECT l FROM Loan l WHERE l.reader = :reader", Loan.class)
                .setParameter("reader", reader)
                .getResultList();
        em.close();
        return loans;
    }


    public void close() {
        emf.close();
    }
}