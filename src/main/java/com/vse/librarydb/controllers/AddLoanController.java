package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class AddLoanController extends BaseController {
    @FXML
    private TextField readerIdField;
    @FXML
    private TextField bookIdField;
    @FXML
    private TextField loanDateField;
    @FXML
    private TextField rentPeriodField;

    private LoanService loanService;
    private ReaderService readerService;
    private BookService bookService;

    public AddLoanController() {
        loanService = new LoanService();
        readerService = new ReaderService();
        bookService = new BookService();
    }

    @FXML
    protected void onAddLoanButtonClick() {
        try {
            Long readerId = Long.parseLong(readerIdField.getText());
            Long bookId = Long.parseLong(bookIdField.getText());
            LocalDate loanDate = LocalDate.parse(loanDateField.getText());
            int rentPeriodDays = Integer.parseInt(rentPeriodField.getText());

            if (rentPeriodDays <= 0) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Rent Period",
                        "Rent period must be a positive number of days.");
                return;
            }

            Reader reader = readerService.getReaderById(readerId);
            Book book = bookService.getBookById(bookId);

            if (reader == null) {
                showAlert(AlertType.ERROR, "Error", "Reader Not Found",
                        "No reader found with ID: " + readerId);
                return;
            }

            if (book == null) {
                showAlert(AlertType.ERROR, "Error", "Book Not Found",
                        "No book found with ID: " + bookId);
                return;
            }

            if (!book.isAvailable()) {
                showAlert(AlertType.ERROR, "Error", "Book Not Available",
                        "This book is currently not available for loan.");
                return;
            }

            String result = loanService.addLoan(reader, book, loanDate, rentPeriodDays);

            if (result.equals("Loan added successfully!")) {
                showAlert(AlertType.INFORMATION, "Success", "Loan Created", result);
                clearFields();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to create loan", result);
            }

        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Invalid Input",
                    "Reader ID, Book ID and Rent Period must be valid numbers.");
        } catch (DateTimeParseException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Invalid Date Format",
                    "Please enter the date in YYYY-MM-DD format.");
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) readerIdField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void clearFields() {
        readerIdField.clear();
        bookIdField.clear();
        loanDateField.clear();
        rentPeriodField.clear();
    }
}