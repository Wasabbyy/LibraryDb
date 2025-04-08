// AddReaderController.java
package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;
import com.vse.librarydb.service.LoanService;

import java.io.IOException;

public class AddReaderController extends BaseController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;

    private ReaderService readerService;
    private BookService bookService;
    private LoanService loanService;

    public AddReaderController() {
        readerService = new ReaderService();
        bookService = new BookService();
        loanService = new LoanService();
    }

    @FXML
    protected void onAddReaderButtonClick() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        readerService.addReader(firstName, lastName, email);
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }
}