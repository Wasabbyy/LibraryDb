package org.example.librarydb.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;

    @Column(name = "publication_year", nullable = false)
    private int publicationYear;

    private boolean available;

    public Book() {}

    public Book(String title, String author, int publicationYear, boolean available) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.available = available;
    }

    // Getters and setters
}