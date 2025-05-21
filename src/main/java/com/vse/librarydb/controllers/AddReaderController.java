package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.service.ReaderService;
import java.io.IOException;

public class AddReaderController extends BaseController {
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    private final ReaderService readerService = new ReaderService();


    public AddReaderController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

    @FXML
    public void initialize() {
        // Set initial status
        initializeDatabaseStatus();
    }
    @FXML
    protected void onAddReaderButtonClick() {
        if (!readerService.isDatabaseAvailable()) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Unavailable",
                    "Cannot add reader while database is unavailable.");
            return;
        }

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        String result = readerService.addReader(firstName, lastName, email);
        handleAddResult(result);
    }

    private void handleAddResult(String result) {
        if (result.startsWith("Validation errors:")) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fix the following issues:", result);
        } else if (result.equals("Reader added successfully!")) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reader Added", result);
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to add reader", result);
        }
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

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    private void clearFields() {
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
    }
}