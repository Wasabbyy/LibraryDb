package com.vse.librarydb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title cannot be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    @Size(max = 50, message = "Author name must not exceed 50 characters")
    private String author;

    @Min(value = 1000, message = "Publication year must be a valid year")
    @Max(value = 9999, message = "Publication year must be a valid year")
    @Column(name = "publication_year", nullable = false)
    private int publicationYear;

    @Size(max = 500, message = "Note must not exceed 500 characters")
    @Column(length = 500)
    private String note;

    private boolean available;

    public Book() {}

    public Book(String title, String author, int publicationYear, boolean available) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.available = available;
        this.note=null;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public boolean isAvailable() {
        return available;
    }

    // Setters (optional, if needed)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}