package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;

import java.util.List;

public class BookDetailsController {

    @FXML
    private Label bookTitleLabel;

    @FXML
    private Label bookAuthorLabel;

    @FXML
    private Label bookYearLabel;

    @FXML
    private ListView<String> loansListView;

    private LoanService loanService;

    public BookDetailsController() {
        loanService = new LoanService();
    }

    public void setBook(Book book) {
        bookTitleLabel.setText(book.getTitle());
        bookAuthorLabel.setText(book.getAuthor());
        bookYearLabel.setText(String.valueOf(book.getPublicationYear()));

        // Fetch loans for the book
        List<Loan> loans = loanService.getLoansByBook(book);
        for (Loan loan : loans) {
            loansListView.getItems().add("Reader: " + loan.getReader().getName() + ", Loaned on: " + loan.getLoanDate());
        }
    }
}