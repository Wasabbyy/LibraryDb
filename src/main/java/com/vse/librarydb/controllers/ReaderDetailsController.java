package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.List;

public class ReaderDetailsController extends BaseController {
    @FXML private Label readerNameLabel;
    @FXML private Label readerEmailLabel;
    @FXML private ListView<String> loansListView;
    @FXML private TextField nameTextField;
    @FXML private TextField emailTextField;
    @FXML private Button saveButton;
    @FXML private Label nameEditLabel;
    @FXML private Label emailEditLabel;
    @FXML private Label databaseStatusLabel;
    private LoanService loanService;
    private ReaderService readerService;
    private Reader currentReader;

    public ReaderDetailsController() {
        loanService = new LoanService();
        readerService = new ReaderService();
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

    @FXML
    public void initialize() {
        // Set initial status
        initializeDatabaseStatus();
    }

    public void setReader(Reader reader) {
        this.currentReader = reader;
        updateReaderDetails();
        loadLoans();
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
    }

    @FXML
    private void onEdit() {
        if (!readerService.isDatabaseAvailable()) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Database Unavailable",
                    "Cannot edit reader while database is unavailable.");
            return;
        }

        nameTextField.setText(currentReader.getName());
        emailTextField.setText(currentReader.getEmail());
        toggleEditMode(true);
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
            if (!validationResult.equals("Validation successful")) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fix the following issues:", validationResult);
                return;
            }

            currentReader.setFirstName(firstName);
            currentReader.setLastName(lastName);
            currentReader.setEmail(email);

            String saveResult = readerService.updateReader(currentReader);
            handleSaveResult(saveResult);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "An unexpected error occurred", e.getMessage());
        }
    }

    private void handleSaveResult(String result) {
        if (result.equals("Reader updated successfully!")) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Reader Updated", result);
            updateReaderDetails();
            toggleEditMode(false);
        } else {
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
    }

    @Override
    public void onDatabaseDisconnected() {
        databaseStatusLabel.setText("Database: Disconnected");
        databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
    }

}