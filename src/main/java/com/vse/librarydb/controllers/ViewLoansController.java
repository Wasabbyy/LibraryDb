package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.model.Loan;
import com.vse.librarydb.service.LoanService;

import java.io.IOException;
import java.util.List;

public class ViewLoansController extends BaseController {
    @FXML
    private ListView<String> loansListView;

    @FXML
    private Button backButton;

    private LoanService loanService;

    public ViewLoansController() {
        loanService = new LoanService();
    }

    @FXML
    public void initialize() {
        List<Loan> loans = loanService.getAllLoans();
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
        stage.setScene(scene);
        stage.show();
    }
}