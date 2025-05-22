package com.vse.librarydb.controllers;

import com.vse.librarydb.database.DatabaseStatusMonitor;
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
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddLoanController extends BaseController {
    private static final Logger logger = Logger.getLogger(AddLoanController.class.getName());

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
        logger.info("AddLoanController initialized.");
    }

    @FXML
    public void initialize() {
        logger.info("Initializing AddLoanController.");
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
            logger.info("Loaded " + readers.size() + " readers.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load readers.", e);
            showError("Error loading readers: " + e.getMessage());
        }
    }

    private void loadBooks() {
        if (!bookService.isDatabaseAvailable()) {
            logger.warning("Database unavailable. Cannot load books.");
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
            logger.info("Loaded " + books.size() + " books.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load books.", e);
            showError("Error loading books: " + e.getMessage());
        }
    }

    @FXML
    private void onSaveLoanButtonClick() {
        logger.info("Save Loan button clicked.");
        if (!loanService.isDatabaseAvailable()) {
            logger.warning("Database unavailable. Cannot create loan.");
            showError("Database unavailable. Cannot create loan.");
            return;
        }

        try {
            validateFields();
            int rentPeriodDays = Integer.parseInt(daysField.getText());
            validateLoanDuration(rentPeriodDays);

            int readerId = readerMap.get(readerComboBox.getValue());
            int bookId = bookMap.get(bookComboBox.getValue());

            Reader reader = readerService.getReaderById((long) readerId);
            Book book = bookService.getBookById((long) bookId);

            String result = loanService.addLoan(reader, book, LocalDate.now(), rentPeriodDays);
            logger.info("LoanService result: " + result);
            handleLoanResult(result);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid number format for loan duration.", e);
            showError("Please enter a valid number for loan duration");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error creating loan.", e);
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
            logger.info("Loan added successfully.");
            showSuccess("Loan created successfully!");
            clearFields();
        } else {
            logger.warning("Loan creation failed: " + result);
            showError(result);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) readerComboBox.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
        logger.info("Returning to main menu from AddLoanController.");
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
