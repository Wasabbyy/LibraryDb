package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class AddLoanController extends BaseController {
    @FXML private ComboBox<String> readerComboBox;
    @FXML private ComboBox<String> bookComboBox;
    @FXML private TextField daysField;
    private final Map<String, Integer> readerMap = new HashMap<>();
    private final Map<String, Integer> bookMap = new HashMap<>();
    private final ReaderService readerService = new ReaderService();
    private final BookService bookService = new BookService();
    private final LoanService loanService = new LoanService();




    public AddLoanController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

    @FXML
    public void initialize() {
        loadReaders();
        loadBooks();
        initializeDatabaseStatus();
    }

    private void loadReaders() {

        try {
            List<Reader> readers = readerService.getAllReaders();
            ObservableList<String> readerNames = FXCollections.observableArrayList();
            for (Reader reader : readers) {
                String fullName = reader.getName();
                readerNames.add(fullName);
                readerMap.put(fullName, reader.getId().intValue());
            }
            readerComboBox.setItems(readerNames);
        } catch (Exception e) {
            showError("Error loading readers: " + e.getMessage());
        }
    }

    private void loadBooks() {
        if (!bookService.isDatabaseAvailable()) {
            showError("Database unavailable. Cannot load books.");
            return;
        }

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
        if (!loanService.isDatabaseAvailable()) {
            showError("Database unavailable. Cannot create loan.");
            return;
        }

        try {
            validateFields();
            int rentPeriodDays = Integer.parseInt(daysField.getText());
            validateLoanDuration(rentPeriodDays);

            int readerId = readerMap.get(readerComboBox.getValue());
            int bookId = bookMap.get(bookComboBox.getValue());

            Reader reader = readerService.getReaderById((long)readerId);
            Book book = bookService.getBookById((long)bookId);

            String result = loanService.addLoan(reader, book, LocalDate.now(), rentPeriodDays);
            handleLoanResult(result);
        } catch (NumberFormatException e) {
            showError("Please enter a valid number for loan duration");
        } catch (Exception e) {
            showError("Error creating loan: " + e.getMessage());
        }
    }

    private void validateFields() throws Exception {
        if (readerComboBox.getValue() == null || bookComboBox.getValue() == null || daysField.getText().isEmpty()) {
            throw new Exception("Please fill all fields.");
        }
    }

    private void validateLoanDuration(int days) throws Exception {
        if (days <= 0) {
            throw new Exception("Loan duration must be at least 1 day");
        }
    }

    private void handleLoanResult(String result) {
        if (result.equals("Loan added successfully!")) {
            showSuccess("Loan created successfully!");
            clearFields();
        } else {
            showError(result);
        }
    }

    @Override
    public void onDatabaseConnected() {
        super.onDatabaseConnected();
        loadReaders();
        loadBooks();
    }

    @Override
    public void onDatabaseDisconnected() {
        super.onDatabaseDisconnected();
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) readerComboBox.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    private void clearFields() {
        readerComboBox.getSelectionModel().clearSelection();
        bookComboBox.getSelectionModel().clearSelection();
        daysField.clear();
    }

    private void showError(String message) {
        new Alert(Alert.AlertType.ERROR, message).show();
    }

    private void showSuccess(String message) {
        new Alert(Alert.AlertType.INFORMATION, message).show();
    }
}