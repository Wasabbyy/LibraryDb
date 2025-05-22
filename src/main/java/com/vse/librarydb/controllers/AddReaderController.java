package com.vse.librarydb.controllers;

import com.vse.librarydb.database.DatabaseStatusMonitor;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.service.ReaderService;

import java.io.IOException;
import java.util.logging.Logger;

public class AddReaderController extends BaseController {
    private static final Logger logger = Logger.getLogger(AddReaderController.class.getName());

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    private final ReaderService readerService = new ReaderService();

    public AddReaderController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
        logger.info("AddReaderController initialized.");
    }

    @FXML
    public void initialize() {
        initializeDatabaseStatus();
    }

    @FXML
    protected void onAddReaderButtonClick() {
        logger.info("Add Reader button clicked.");

        if (!readerService.isDatabaseAvailable()) {
            logger.warning("Database unavailable - cannot add reader.");
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Unavailable",
                    "Cannot add reader while database is unavailable.");
            return;
        }

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        logger.fine("Input: FirstName=" + firstName + ", LastName=" + lastName + ", Email=" + email);

        String result = readerService.addReader(firstName, lastName, email);
        logger.info("ReaderService result: " + result);
        handleAddResult(result);
    }

    private void handleAddResult(String result) {
        if (result.startsWith("Validation errors:")) {
            logger.warning("Validation errors: " + result);
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fix the following issues:", result);
        } else if (result.equals("Reader added successfully!")) {
            logger.info("Reader added successfully.");
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reader Added", result);
            clearFields();
        } else {
            logger.severe("Failed to add reader: " + result);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reader", result);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
        logger.info("Returning to main menu from AddReaderController.");
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
    }
}
