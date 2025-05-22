package com.vse.librarydb.controllers;

import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ReturnBooksController extends BaseController {
    private static final Logger LOGGER = Logger.getLogger(ReturnBooksController.class.getName());

    @FXML private ListView<String> loanedBooksListView;
    @FXML private Button returnButton;
    @FXML private TextField searchField;

    private final LoanService loanService = new LoanService();
    private List<Loan> loanedBooks;
    private List<Loan> allLoanedBooks;

    @FXML
    public void initialize() {
        loadAllLoanedBooks();
        refreshLoanedBooksList(allLoanedBooks);
        initializeDatabaseStatus();

        loanedBooksListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                viewNoteForSelectedBook();
            }
        });

        LOGGER.info("ReturnBooksController initialized.");
    }

    private void loadAllLoanedBooks() {
        allLoanedBooks = loanService.getAllLoans().stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
        loanedBooks = new ArrayList<>(allLoanedBooks);
        LOGGER.info("Loaded " + loanedBooks.size() + " unreturned books.");
    }

    private void refreshLoanedBooksList(List<Loan> loans) {
        loanedBooksListView.getItems().clear();
        for (Loan loan : loans) {
            String note = loan.getBook().getNote() != null ? " (Has note)" : "";
            loanedBooksListView.getItems().add("Reader: " + loan.getReader().getName() +
                    " | Book: " + loan.getBook().getTitle() +
                    " | Loaned on: " + loan.getLoanDate() + note);
        }
    }

    @FXML
    private void onSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Loan> filtered = allLoanedBooks.stream()
                .filter(loan -> loan.getReader().getName().toLowerCase().contains(searchTerm)
                        || loan.getBook().getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        loanedBooks = new ArrayList<>(filtered);
        refreshLoanedBooksList(loanedBooks);
        LOGGER.info("Search performed with term: " + searchTerm);
    }

    @FXML
    private void onReturnButtonClick() {
        int selectedIndex = loanedBooksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            showReturnDialog(loanedBooks.get(selectedIndex));
        } else {
            LOGGER.warning("No book selected to return.");
        }
    }

    private void showReturnDialog(Loan loan) {
        TextInputDialog dialog = new TextInputDialog(loan.getBook().getNote());
        dialog.setTitle("Book Return Note");
        dialog.setHeaderText("Enter note for: " + loan.getBook().getTitle());
        dialog.setContentText("Note:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(note -> {
            String message = loanService.returnBook(loan.getId(), note);
            showAlert("Return Status", message, Alert.AlertType.INFORMATION);
            loadAllLoanedBooks();
            onSearch();
            LOGGER.info("Returned book: " + loan.getBook().getTitle());
        });
    }

    private void viewNoteForSelectedBook() {
        int selectedIndex = loanedBooksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Loan loan = loanedBooks.get(selectedIndex);
            String note = loan.getBook().getNote();
            showAlert("Book Note", note != null ? note : "No note available", Alert.AlertType.INFORMATION);
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
