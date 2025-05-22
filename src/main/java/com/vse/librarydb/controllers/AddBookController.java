package com.vse.librarydb.controllers;

import com.vse.librarydb.database.DatabaseStatusMonitor;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.vse.librarydb.service.BookService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AddBookController extends BaseController {
    private static final Logger logger = Logger.getLogger(AddBookController.class.getName());

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publicationYearField;
    private BookService bookService;

    public AddBookController() {
        bookService = new BookService();
        DatabaseStatusMonitor.getInstance().addListener(this);
        logger.info("AddBookController initialized.");
    }

    @FXML
    public void initialize() {
        initializeDatabaseStatus();
    }

    @FXML
    protected void onAddBookButtonClick() {
        logger.info("Add Book button clicked.");
        try {
            String title = titleField.getText();
            String author = authorField.getText();
            int publicationYear = Integer.parseInt(publicationYearField.getText());

            logger.fine("Input: Title=" + title + ", Author=" + author + ", Year=" + publicationYear);

            if (title.isEmpty() || author.isEmpty()) {
                logger.warning("Validation failed: Title or Author is empty.");
                showAlert(AlertType.ERROR, "Validation Error", "Missing Information",
                        "Title and author cannot be empty!");
                return;
            }

            String result = bookService.addBook(title, author, publicationYear, true);
            logger.info("BookService result: " + result);
            handleAddResult(result);
        } catch (NumberFormatException e) {
            logger.log(Level.SEVERE, "Invalid input for publication year.", e);
            showAlert(AlertType.ERROR, "Validation Error", "Invalid Input",
                    "Publication year must be a valid number.");
        }
    }

    private void handleAddResult(String result) {
        if (result.equals("Book added successfully!")) {
            showAlert(AlertType.INFORMATION, "Success", "Book Added", result);
            logger.info("Book added successfully.");
            clearFields();
        } else {
            logger.warning("Failed to add book: " + result);
            showAlert(AlertType.ERROR, "Error", "Failed to add book", result);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) titleField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
        logger.info("Returning to main menu from AddBookController.");
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        publicationYearField.clear();
    }
}
