package com.vse.librarydb.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "readers")
public class Reader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @OneToMany(mappedBy = "reader", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans;

    public Reader() {
    }

    public Reader(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return  email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public List<Loan> getLoans() {
        return loans;
    }
    public String getName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName + " (" + email + ")";
    }
}