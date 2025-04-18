package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;
import com.vse.librarydb.service.LoanService;

import java.io.IOException;

public class AddReaderController extends BaseController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;

    private ReaderService readerService;
    private BookService bookService;
    private LoanService loanService;

    public AddReaderController() {
        readerService = new ReaderService();
        bookService = new BookService();
        loanService = new LoanService();
    }

    @FXML
    protected void onAddReaderButtonClick() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        String result = readerService.addReader(firstName, lastName, email);

        if (result.startsWith("Validation errors:")) {
            // Show error alert
            showAlert(AlertType.ERROR, "Validation Error", "Please fix the following issues:", result);
        } else if (result.equals("Reader added successfully!")) {
            // Show success alert and clear fields
            showAlert(AlertType.INFORMATION, "Success", "Reader Added", result);
            clearFields();
        } else {
            // Show generic error
            showAlert(AlertType.ERROR, "Error", "Failed to add reader", result);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
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
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
    }
}