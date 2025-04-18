package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.vse.librarydb.service.BookService;

import java.io.IOException;

public class AddBookController extends BaseController {
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publicationYearField;

    private BookService bookService;

    public AddBookController() {
        bookService = new BookService();
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

            if (publicationYear < 0 || publicationYear > java.time.Year.now().getValue()) {
                showAlert(AlertType.ERROR, "Validation Error", "Invalid Year",
                        "Publication year must be between 0 and current year.");
                return;
            }

            String result = bookService.addBook(title, author, publicationYear, true);

            if (result.equals("Book added successfully!")) {
                showAlert(AlertType.INFORMATION, "Success", "Book Added", result);
                clearFields();
            } else {
                showAlert(AlertType.ERROR, "Error", "Failed to add book", result);
            }
        } catch (NumberFormatException e) {
            showAlert(AlertType.ERROR, "Validation Error", "Invalid Input",
                    "Publication year must be a valid number.");
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) titleField.getScene().getWindow();
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
        titleField.clear();
        authorField.clear();
        publicationYearField.clear();
    }
}