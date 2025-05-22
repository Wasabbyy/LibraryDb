package com.vse.librarydb.controllers;

import com.vse.librarydb.database.DatabaseStatusMonitor;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.logging.Logger;

public class ReaderDetailsController extends BaseController {
    private static final Logger LOGGER = Logger.getLogger(ReaderDetailsController.class.getName());

    @FXML private Label readerNameLabel;
    @FXML private Label readerEmailLabel;
    @FXML private ListView<String> loansListView;
    @FXML private TextField nameTextField;
    @FXML private TextField emailTextField;
    @FXML private Button saveButton;
    @FXML private Label nameEditLabel;
    @FXML private Label emailEditLabel;
    @FXML private Label databaseStatusLabel;

    private final LoanService loanService = new LoanService();
    private final ReaderService readerService = new ReaderService();
    private Reader currentReader;

    public ReaderDetailsController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
        LOGGER.info("ReaderDetailsController initialized.");
    }

    @FXML
    public void initialize() {
        initializeDatabaseStatus();
        LOGGER.info("ReaderDetailsController initialized UI.");
    }

    public void setReader(Reader reader) {
        this.currentReader = reader;
        updateReaderDetails();
        loadLoans();
        LOGGER.info("Reader set: " + reader.getName());
    }

    private void updateReaderDetails() {
        readerNameLabel.setText(currentReader.getName());
        readerEmailLabel.setText(currentReader.getEmail());
    }

    private void loadLoans() {
        loansListView.getItems().clear();
        List<Loan> loans = loanService.getLoansByReader(currentReader);
        for (Loan loan : loans) {
            loansListView.getItems().add("Book: " + loan.getBook().getTitle() + ", Loaned on: " + loan.getLoanDate());
        }
        LOGGER.info("Loaded " + loans.size() + " loans for reader.");
    }

    @FXML
    private void onEdit() {
        if (!readerService.isDatabaseAvailable()) {
            LOGGER.warning("Edit attempted while database is unavailable.");
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Unavailable", "Cannot edit reader while database is unavailable.");
            return;
        }

        nameTextField.setText(currentReader.getName());
        emailTextField.setText(currentReader.getEmail());
        toggleEditMode(true);
        LOGGER.info("Edit mode enabled for reader: " + currentReader.getName());
    }

    @FXML
    private void onSave() {
        try {
            String fullName = nameTextField.getText().trim();
            String email = emailTextField.getText().trim();
            String[] nameParts = fullName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            String validationResult = readerService.validateReader(firstName, lastName, email);
            if (!"Validation successful".equals(validationResult)) {
                LOGGER.warning("Validation failed: " + validationResult);
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fix the following issues:", validationResult);
                return;
            }

            currentReader.setFirstName(firstName);
            currentReader.setLastName(lastName);
            currentReader.setEmail(email);

            String result = readerService.updateReader(currentReader);
            handleSaveResult(result);
        } catch (Exception e) {
            LOGGER.severe("Error during save: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred", e.getMessage());
        }
    }

    private void handleSaveResult(String result) {
        if ("Reader updated successfully!".equals(result)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reader Updated", result);
            updateReaderDetails();
            toggleEditMode(false);
            LOGGER.info("Reader updated successfully.");
        } else {
            LOGGER.warning("Failed to update reader: " + result);
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to update reader", result);
        }
    }

    private void toggleEditMode(boolean editMode) {
        readerNameLabel.setVisible(!editMode);
        readerEmailLabel.setVisible(!editMode);
        nameEditLabel.setVisible(editMode);
        nameTextField.setVisible(editMode);
        emailEditLabel.setVisible(editMode);
        emailTextField.setVisible(editMode);
        saveButton.setVisible(editMode);
    }

    @Override
    public void onDatabaseConnected() {
        databaseStatusLabel.setText("Database: Connected");
        databaseStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        LOGGER.info("Database connected.");
    }

    @Override
    public void onDatabaseDisconnected() {
        databaseStatusLabel.setText("Database: Disconnected");
        databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        LOGGER.warning("Database disconnected.");
    }
}
