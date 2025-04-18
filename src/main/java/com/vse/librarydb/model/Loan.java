package com.vse.librarydb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull(message = "Loan date cannot be null")
    private LocalDate loanDate;

    private LocalDate returnDate;

    @Min(value = 1, message = "Rent period must be at least 1 day")
    private int rentPeriodDays;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean delayed;

    // Default constructor
    public Loan() {}

    // Constructor for creating new loans (without returnDate and delayed status)
    public Loan(Reader reader, Book book, LocalDate loanDate, int rentPeriodDays) {
        this.reader = reader;
        this.book = book;
        this.loanDate = loanDate;
        this.rentPeriodDays = rentPeriodDays;
        this.returnDate = null; // Initially null until book is returned
        this.delayed = false; // Initially not delayed
    }

    // Full constructor (if needed)
    public Loan(Reader reader, Book book, LocalDate loanDate, LocalDate returnDate, boolean delayed, int rentPeriodDays) {
        this.reader = reader;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
        this.delayed = delayed;
        this.rentPeriodDays = rentPeriodDays;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public int getRentPeriodDays() {
        return rentPeriodDays;
    }

    public void setRentPeriodDays(int rentPeriodDays) {
        this.rentPeriodDays = rentPeriodDays;
    }
}