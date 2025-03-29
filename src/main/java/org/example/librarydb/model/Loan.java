package org.example.librarydb.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reader_id")
    private Reader reader;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private LocalDate loanDate;
    private LocalDate returnDate;

    public Loan() {}

    public Loan(Reader reader, Book book, LocalDate loanDate, LocalDate returnDate) {
        this.reader = reader;
        this.book = book;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    // Getters and setters
}