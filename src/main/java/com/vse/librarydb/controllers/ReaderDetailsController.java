package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import javafx.scene.control.Alert.AlertType;

import java.util.List;

public class ReaderDetailsController extends BaseController {

    @FXML
    private Label readerNameLabel;
    @FXML
    private Label readerEmailLabel;
    @FXML
    private ListView<String> loansListView;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button saveButton;

    private LoanService loanService;
    private ReaderService readerService;
    private Reader currentReader;

    public ReaderDetailsController() {
        loanService = new LoanService();
        readerService = new ReaderService();
    }

    public void setReader(Reader reader) {
        this.currentReader = reader;
        readerNameLabel.setText(reader.getName());
        readerEmailLabel.setText(reader.getEmail());

        // Fetch loans for the reader
        List<Loan> loans = loanService.getLoansByReader(reader);
        loansListView.getItems().clear();
        for (Loan loan : loans) {
            loansListView.getItems().add("Book: " + loan.getBook().getTitle() + ", Loaned on: " + loan.getLoanDate());
        }
    }

    @FXML
    private void onEdit() {
        // Set current values in the text fields when editing starts
        nameTextField.setText(currentReader.getName());
        emailTextField.setText(currentReader.getEmail());

        nameTextField.setVisible(true);
        emailTextField.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    private void onSave() {
        String fullName = nameTextField.getText().trim();
        String email = emailTextField.getText().trim();

        // Split the name into first and last name
        String[] nameParts = fullName.split(" ", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : "";

        // Validate the inputs
        String validationResult = readerService.validateReader(firstName, lastName, email);

        if (!validationResult.equals("Validation successful")) {
            showAlert(AlertType.ERROR, "Validation Error", "Please fix the following issues:", validationResult);
            return;
        }

        // Update reader details if validation passed
        currentReader.setFirstName(firstName);
        currentReader.setLastName(lastName);
        currentReader.setEmail(email);

        // Save to database
        String saveResult = readerService.updateReader(currentReader);

        if (saveResult.equals("Reader updated successfully!")) {
            showAlert(AlertType.INFORMATION, "Success", "Reader Updated", saveResult);
            readerNameLabel.setText(currentReader.getName());
            readerEmailLabel.setText(currentReader.getEmail());
            nameTextField.setVisible(false);
            emailTextField.setVisible(false);
            saveButton.setVisible(false);
        } else {
            showAlert(AlertType.ERROR, "Error", "Failed to update reader", saveResult);
        }
    }

    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}