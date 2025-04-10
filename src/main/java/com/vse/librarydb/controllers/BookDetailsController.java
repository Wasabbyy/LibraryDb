package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.BookService;
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

    @FXML
    private TextField titleTextField;

    @FXML
    private TextField authorTextField;

    @FXML
    private TextField yearTextField;

    @FXML
    private Button saveButton;

    private BookService bookService;
    private LoanService loanService; // Declare LoanService
    private Book currentBook;

    public BookDetailsController() {
        bookService = new BookService();
        loanService = new LoanService(); // Initialize LoanService
    }

    public void setBook(Book book) {
        this.currentBook = book;
        bookTitleLabel.setText(book.getTitle());
        bookAuthorLabel.setText(book.getAuthor());
        bookYearLabel.setText(String.valueOf(book.getPublicationYear()));

        // Clear and repopulate the loans list
        loansListView.getItems().clear();
        List<Loan> loans = loanService.getLoansByBook(book); // Use loanService
        for (Loan loan : loans) {
            loansListView.getItems().add("Reader: " + loan.getReader().getFirstName() + loan.getReader().getLastName()+ ", Loaned on: " + loan.getLoanDate());
            System.out.println("Reader: " + loan.getReader().getFirstName() + loan.getReader().getLastName()+ ", Loaned on: " + loan.getLoanDate());
        }
    }

    @FXML
    private void onEdit() {
        titleTextField.setText(currentBook.getTitle());
        authorTextField.setText(currentBook.getAuthor());
        yearTextField.setText(String.valueOf(currentBook.getPublicationYear()));
        titleTextField.setVisible(true);
        authorTextField.setVisible(true);
        yearTextField.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    private void onSave() {
        currentBook.setTitle(titleTextField.getText());
        currentBook.setAuthor(authorTextField.getText());
        currentBook.setPublicationYear(Integer.parseInt(yearTextField.getText()));

        bookService.updateBook(currentBook);

        // Refresh the UI and loans
        setBook(currentBook);

        titleTextField.setVisible(false);
        authorTextField.setVisible(false);
        yearTextField.setVisible(false);
        saveButton.setVisible(false);
    }
}