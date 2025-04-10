package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;

import java.util.List;

public class ReaderDetailsController {

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
        if (saveButton == null) {
            System.out.println("saveButton is null");
        }
        nameTextField.setVisible(true);
        emailTextField.setVisible(true);
        saveButton.setVisible(true);
    }

    @FXML
    private void onSave() {
        // Update reader details
        String[] nameParts = nameTextField.getText().split(" ", 2);
        if (nameParts.length == 2) {
            currentReader.setFirstName(nameParts[0]);
            currentReader.setLastName(nameParts[1]);
        }
        currentReader.setEmail(emailTextField.getText());

        // Save to database
        readerService.updateReader(currentReader);

        // Update labels and hide text fields
        readerNameLabel.setText(currentReader.getName());
        readerEmailLabel.setText(currentReader.getEmail());
        nameTextField.setVisible(false);
        emailTextField.setVisible(false);
        saveButton.setVisible(false);
    }
}