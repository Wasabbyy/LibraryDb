package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ViewLoansController extends BaseController {
    @FXML
    private ListView<String> loansListView;
    @FXML
    private Button backButton;
    @FXML
    private TextField bookSearchField;
    @FXML
    private TextField readerSearchField;

    private LoanService loanService;
    private List<Loan> allLoans;

    public ViewLoansController() {
        loanService = new LoanService();
    }

    @FXML
    public void initialize() {
        allLoans = loanService.getAllLoans();
        refreshLoanList(allLoans);
    }

    @FXML
    private void onBookSearch() {
        String searchTerm = bookSearchField.getText().toLowerCase();
        List<Loan> filteredLoans = allLoans.stream()
                .filter(loan -> loan.getBook().getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        refreshLoanList(filteredLoans);
    }

    @FXML
    private void onReaderSearch() {
        String searchTerm = readerSearchField.getText().toLowerCase();
        List<Loan> filteredLoans = allLoans.stream()
                .filter(loan -> loan.getReader().getName().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        refreshLoanList(filteredLoans);
    }

    private void refreshLoanList(List<Loan> loans) {
        loansListView.getItems().clear();
        for (Loan loan : loans) {
            String loanDetails = "Reader: " + loan.getReader().toString() +
                    ", Book: " + loan.getBook().getTitle() +
                    ", Loan Date: " + loan.getLoanDate() +
                    ", Return Date: " + (loan.getReturnDate() != null ? loan.getReturnDate() : "Not Returned") +
                    ", Delayed: " + (loan.isDelayed() ? "Yes" : "No");
            loansListView.getItems().add(loanDetails);
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/view-data.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.show();
    }
}