package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.BookService;
import com.vse.librarydb.service.LoanService;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

public class BookDetailsController extends BaseController {

    @FXML
    private Label bookTitleLabel;
    @FXML
    private Label bookAuthorLabel;
    @FXML
    private Label bookYearLabel;
    @FXML
    private Label bookNoteLabel;
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
    private LoanService loanService;
    private Book currentBook;

    public BookDetailsController() {
        bookService = new BookService();
        loanService = new LoanService();
    }

    public void setBook(Book book) {
        this.currentBook = book;
        bookTitleLabel.setText(book.getTitle());
        bookAuthorLabel.setText(book.getAuthor());
        bookYearLabel.setText(String.valueOf(book.getPublicationYear()));
        bookNoteLabel.setText(book.getNote() != null ? book.getNote() : "No note available");

        // Clear and repopulate the loans list
        loansListView.getItems().clear();
        List<Loan> loans = loanService.getLoansByBook(book);
        for (Loan loan : loans) {
            loansListView.getItems().add("Reader: " + loan.getReader().getFirstName() + " " +
                    loan.getReader().getLastName() + ", Loaned on: " + loan.getLoanDate());
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
        try {
            String title = titleTextField.getText().trim();
            String author = authorTextField.getText().trim();
            int year = Integer.parseInt(yearTextField.getText().trim());

            // Validate inputs
            String validationResult = bookService.validateBook(title, author, year);
            if (!validationResult.equals("Validation successful")) {
                showAlert(AlertType.ERROR, "Validation Error", "Please fix the following issues:", validationResult);
                return;
            }

            // Update book
            currentBook.setTitle(title);
            currentBook.setAuthor(author);
            currentBook.setPublicationYear(year);

            // Save to database
            String saveResult = bookService.updateBook(currentBook);

            if (saveResult.equals("Book updated successfully!")) {
                showAlert(AlertType.INFORMATION, "Success", "Book Updated", saveResult);
                setBook(currentBook); // Refresh the display
                titleTextField.setVisible(false);
                authorTextField.setVisible(false);
                yearTextField.setVisible(false);
                saveButton.setVisible(false);
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to update book", saveResult);
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Invalid Input", "Publication Year must be a number",
                    "Please enter a valid year (e.g., 2023)");
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Error", "An unexpected error occurred",
                    e.getMessage());
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}