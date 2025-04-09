package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.LoanService;

import java.util.List;

public class ReaderDetailsController {

    @FXML
    private Label readerNameLabel;

    @FXML
    private Label readerEmailLabel;

    @FXML
    private ListView<String> loansListView;

    private LoanService loanService;

    public ReaderDetailsController() {
        loanService = new LoanService();
    }

    public void setReader(Reader reader) {
        readerNameLabel.setText(reader.getName());
        readerEmailLabel.setText(reader.getEmail());

        // Fetch loans for the reader
        List<Loan> loans = loanService.getLoansByReader(reader);
        for (Loan loan : loans) {
            loansListView.getItems().add("Book: " + loan.getBook().getTitle() + ", Loaned on: " + loan.getLoanDate());
        }
    }
}