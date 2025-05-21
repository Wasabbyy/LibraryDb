package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.vse.librarydb.service.BookService;
import java.io.IOException;

public class AddBookController extends BaseController {
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField publicationYearField;
    private BookService bookService;

    public AddBookController() {
        bookService = new BookService();
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

    @FXML
    public void initialize() {
        initializeDatabaseStatus(); // Initialize the status label
    }

    @FXML
    protected void onAddBookButtonClick() {

        try {
            String title = titleField.getText();
            String author = authorField.getText();
            int publicationYear = Integer.parseInt(publicationYearField.getText());

            if (title.isEmpty() || author.isEmpty()) {
                showAlert(AlertType.ERROR, "Validation Error", "Missing Information",
                        "Title and author cannot be empty!");
                return;
            }

            String result = bookService.addBook(title, author, publicationYear, true);
            handleAddResult(result);
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Invalid Input",
                    "Publication year must be a valid number.");
        }
    }

    private void handleAddResult(String result) {
        if (result.equals("Book added successfully!")) {
            showAlert(AlertType.INFORMATION, "Success", "Book Added", result);
            clearFields();
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to add book", result);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) titleField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    @Override
    public void onDatabaseConnected() {
        super.onDatabaseConnected();
        // Optionally refresh any data if needed
    }

    @Override
    public void onDatabaseDisconnected() {
        super.onDatabaseDisconnected();
    }

    private void clearFields() {
        titleField.clear();
        authorField.clear();
        publicationYearField.clear();
    }
}