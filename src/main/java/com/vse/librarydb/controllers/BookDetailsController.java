package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.BookService;
import com.vse.librarydb.service.LoanService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class BookDetailsController extends BaseController {
    @FXML private Label bookTitleLabel;
    @FXML private Label bookAuthorLabel;
    @FXML private Label bookYearLabel;
    @FXML private Label bookNoteLabel;
    @FXML private ListView<String> loansListView;
    @FXML private TextField titleTextField;
    @FXML private TextField authorTextField;
    @FXML private TextField yearTextField;
    @FXML private Button saveButton;
    @FXML private Label titleEditLabel;
    @FXML private Label authorEditLabel;
    @FXML private Label yearEditLabel;
    private BookService bookService;
    private LoanService loanService;
    private Book currentBook;

    public BookDetailsController() {
        bookService = new BookService();
        loanService = new LoanService();
        DatabaseStatusMonitor.getInstance().addListener(this);
    }
    @FXML
    public void initialize() {
        initializeDatabaseStatus(); // Initialize the status label
    }

    public void setBook(Book book) {
        this.currentBook = book;
        updateBookDetails();
        loadLoans();
    }

    private void updateBookDetails() {
        bookTitleLabel.setText("Title: " + currentBook.getTitle());
        bookAuthorLabel.setText("Author: " + currentBook.getAuthor());
        bookYearLabel.setText("Year: " + currentBook.getPublicationYear());
        bookNoteLabel.setText("Note: " + (currentBook.getNote() != null ? currentBook.getNote() : "No note available"));
    }

    private void loadLoans() {

        loansListView.getItems().clear();
        List<Loan> loans = loanService.getLoansByBook(currentBook);
        for (Loan loan : loans) {
            loansListView.getItems().add("Reader: " + loan.getReader().getFirstName() + " " +
                    loan.getReader().getLastName() + ", Loaned on: " + loan.getLoanDate());
        }
    }

    @FXML
    private void onEdit() {
        if (!bookService.isDatabaseAvailable()) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Unavailable",
                    "Cannot edit book while database is unavailable.");
            return;
        }

        titleTextField.setText(currentBook.getTitle());
        authorTextField.setText(currentBook.getAuthor());
        yearTextField.setText(String.valueOf(currentBook.getPublicationYear()));
        toggleEditMode(true);
    }

    @FXML
    private void onSave() {
        try {
            String title = titleTextField.getText().trim();
            String author = authorTextField.getText().trim();
            int year = Integer.parseInt(yearTextField.getText().trim());

            String validationResult = bookService.validateBook(title, author, year);
            if (!validationResult.equals("Validation successful")) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fix the following issues:", validationResult);
                return;
            }

            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setPublicationYear(year);

            String saveResult = bookService.updateBook(currentBook);
            handleSaveResult(saveResult);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Publication Year must be a number",
                    "Please enter a valid year (e.g., 2023)");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred", e.getMessage());
        }
    }

    private void handleSaveResult(String result) {
        if (result.equals("Book updated successfully!")) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book Updated", result);
            updateBookDetails();
            toggleEditMode(false);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update book", result);
        }
    }

    private void toggleEditMode(boolean editMode) {
        bookTitleLabel.setVisible(!editMode);
        bookAuthorLabel.setVisible(!editMode);
        bookYearLabel.setVisible(!editMode);
        titleEditLabel.setVisible(editMode);
        titleTextField.setVisible(editMode);
        authorEditLabel.setVisible(editMode);
        authorTextField.setVisible(editMode);
        yearEditLabel.setVisible(editMode);
        yearTextField.setVisible(editMode);
        saveButton.setVisible(editMode);
    }

    @Override
    public void onDatabaseConnected() {
        super.onDatabaseConnected();
        if (currentBook != null) {
            loadLoans();
        }
    }

    @Override
    public void onDatabaseDisconnected() {
        super.onDatabaseDisconnected();
    }
}