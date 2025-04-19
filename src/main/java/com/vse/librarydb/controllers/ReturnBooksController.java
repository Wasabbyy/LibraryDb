package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReturnBooksController extends BaseController {
    @FXML
    private ListView<String> loanedBooksListView;
    @FXML
    private Button returnButton;

    private final LoanService loanService = new LoanService();
    private List<Loan> loanedBooks;

    @FXML
    public void initialize() {
        loadLoanedBooks();

        // Add double-click to view note
        loanedBooksListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                viewNoteForSelectedBook();
            }
        });
    }

    private void loadLoanedBooks() {
        loanedBooks = new ArrayList<>(loanService.getAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .toList());

        loanedBooksListView.getItems().clear();
        for (Loan loan : loanedBooks) {
            String noteIndicator = loan.getBook().getNote() != null ? " (Has note)" : "";
            loanedBooksListView.getItems().add("Book: " + loan.getBook().getTitle() +
                    " (Loaned on: " + loan.getLoanDate() + ")" + noteIndicator);
        }
    }

    @FXML
    private void onReturnButtonClick() {
        int selectedIndex = loanedBooksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            showReturnDialog(loanedBooks.get(selectedIndex));
        }
    }

    private void showReturnDialog(Loan loan) {
        TextInputDialog dialog = new TextInputDialog(loan.getBook().getNote());
        dialog.setTitle("Book Return Note");
        dialog.setHeaderText("Enter note for: " + loan.getBook().getTitle());
        dialog.setContentText("Note:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(note -> {
            String resultMessage = loanService.returnBook(loan.getId(), note);
            showAlert("Return Status", resultMessage, Alert.AlertType.INFORMATION);
            loadLoanedBooks(); // Refresh the list
        });
    }

    private void viewNoteForSelectedBook() {
        int selectedIndex = loanedBooksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Loan loan = loanedBooks.get(selectedIndex);
            String note = loan.getBook().getNote();
            showAlert("Book Note",
                    note != null ? note : "No note available",
                    Alert.AlertType.INFORMATION);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) returnButton.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }
}