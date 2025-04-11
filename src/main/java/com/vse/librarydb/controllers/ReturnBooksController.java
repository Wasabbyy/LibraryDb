package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReturnBooksController extends BaseController {
    @FXML
    private ListView<String> loanedBooksListView;

    @FXML
    private Button returnButton;

    private LoanService loanService;
    private List<Loan> loanedBooks;

    public ReturnBooksController() {
        loanService = new LoanService();
    }

    @FXML
    public void initialize() {
        // Fetch all loaned books (loans with null returnDate)
        loanedBooks = new ArrayList<>(loanService.getAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .toList());

        // Populate the ListView
        for (Loan loan : loanedBooks) {
            loanedBooksListView.getItems().add("Book: " + loan.getBook().getTitle() + " (Loaned on: " + loan.getLoanDate() + ")");
        }
    }

    @FXML
    protected void onReturnButtonClick() {
        int selectedIndex = loanedBooksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Loan selectedLoan = loanedBooks.get(selectedIndex);

            // Mark the book as returned
            loanService.returnBook(selectedLoan.getId());

            // Remove the returned book from the list
            loanedBooksListView.getItems().remove(selectedIndex);
            loanedBooks.remove(selectedIndex);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) returnButton.getScene().getWindow(); // Use returnButton to get the Stage
        super.onReturnToMenuButtonClick(stage);
    }
}