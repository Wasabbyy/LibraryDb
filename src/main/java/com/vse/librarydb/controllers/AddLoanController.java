package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AddLoanController extends BaseController{

    @FXML
    private ComboBox<String> readerComboBox;

    @FXML
    private ComboBox<String> bookComboBox;
    @FXML
    private TextField daysField;

    private final Map<String, Integer> readerMap = new HashMap<>();
    private final Map<String, Integer> bookMap = new HashMap<>();

    private final ReaderService readerService = new ReaderService();
    private final BookService bookService = new BookService();
    private final LoanService loanService = new LoanService();

    @FXML
    public void initialize() {
        loadReaders();
        loadBooks();
    }

    private void loadReaders() {
        try {
            List<Reader> readers = readerService.getAllReaders();
            ObservableList<String> readerNames = FXCollections.observableArrayList();

            for (Reader reader : readers) {
                String fullName = reader.getName(); // Uses firstName + " " + lastName
                readerNames.add(fullName);
                readerMap.put(fullName, reader.getId().intValue());
            }

            readerComboBox.setItems(readerNames);
        } catch (Exception e) {
            showError("Error loading readers: " + e.getMessage());
        }
    }

    private void loadBooks() {
        try {
            List<Book> books = bookService.getAllBooks();
            ObservableList<String> bookTitles = FXCollections.observableArrayList();

            for (Book book : books) {
                String title = book.getTitle();
                bookTitles.add(title);
                bookMap.put(title, book.getId().intValue());
            }

            bookComboBox.setItems(bookTitles);
        } catch (Exception e) {
            showError("Error loading books: " + e.getMessage());
        }
    }

    @FXML
    private void onSaveLoanButtonClick() {
        String selectedReader = readerComboBox.getValue();
        String selectedBook = bookComboBox.getValue();
        String daysText = daysField.getText();

        if (selectedReader == null || selectedBook == null || daysText.isEmpty()) {
            showError("Please fill all fields.");
            return;
        }

        try {
            // Parse loan duration
            int rentPeriodDays = Integer.parseInt(daysText);
            if (rentPeriodDays <= 0) {
                showError("Loan duration must be at least 1 day");
                return;
            }

            // Get the IDs from the maps
            int readerId = readerMap.get(selectedReader);
            int bookId = bookMap.get(selectedBook);

            // Get the full objects from services
            Reader reader = readerService.getReaderById((long)readerId);
            Book book = bookService.getBookById((long)bookId);

            // Create the loan using LoanService
            String result = loanService.addLoan(
                    reader,
                    book,
                    LocalDate.now(),
                    rentPeriodDays
            );

            if (result.equals("Loan added successfully!")) {
                showSuccess("Loan created successfully!");
                // Clear fields after successful save
                readerComboBox.getSelectionModel().clearSelection();
                bookComboBox.getSelectionModel().clearSelection();
                daysField.clear();
            } else {
                showError(result);
            }
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for loan duration");
        } catch (Exception e) {
            showError("Error creating loan: " + e.getMessage());
        }
    }
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }
    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        // Use any of your existing UI components to get the stage
        Stage stage = (Stage) readerComboBox.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }
}