// AddLoanController.java
package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
import com.vse.librarydb.service.BookService;

import java.io.IOException;
import java.time.LocalDate;

public class AddLoanController extends BaseController {
    @FXML
    private TextField readerIdField;
    @FXML
    private TextField bookIdField;
    @FXML
    private TextField loanDateField;
    @FXML
    private TextField returnDateField;

    private LoanService loanService;
    private ReaderService readerService;
    private BookService bookService;

    public AddLoanController() {
        loanService = new LoanService();
        readerService = new ReaderService();
        bookService = new BookService();
    }

    @FXML
    protected void onAddLoanButtonClick() {
        Long readerId = Long.parseLong(readerIdField.getText());
        Long bookId = Long.parseLong(bookIdField.getText());
        LocalDate loanDate = LocalDate.parse(loanDateField.getText());
        LocalDate returnDate = LocalDate.parse(returnDateField.getText());

        Reader reader = readerService.getReaderById(readerId);
        Book book = bookService.getBookById(bookId);

        loanService.addLoan(reader, book, loanDate, returnDate);
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) readerIdField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }
}