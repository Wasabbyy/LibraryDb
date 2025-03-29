package org.example.librarydb;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.librarydb.service.ReaderService;
import org.example.librarydb.service.BookService;
import org.example.librarydb.service.LoanService;
import org.example.librarydb.model.Reader;
import org.example.librarydb.model.Book;

import java.time.LocalDate;

public class HelloController {
    @FXML
    private Label welcomeText;

    private ReaderService readerService;
    private BookService bookService;
    private LoanService loanService;

    public HelloController() {
        readerService = new ReaderService();
        bookService = new BookService();
        loanService = new LoanService();
    }

    @FXML
    protected void onHelloButtonClick() {
        readerService.addReader("John", "Doe", "john.doe@example.com");
        bookService.addBook("Effective Java", "Joshua Bloch", 2018, true);

        Reader reader = readerService.getAllReaders().get(0);
        Book book = bookService.getAllBooks().get(0);
        loanService.addLoan(reader, book, LocalDate.now(), LocalDate.now().plusDays(14));

        StringBuilder readersList = new StringBuilder("Readers:\n");
        for (Reader r : readerService.getAllReaders()) {
            readersList.append(r.getFirstName()).append(" ").append(r.getLastName()).append("\n");
        }
        welcomeText.setText(readersList.toString());
    }

    public void close() {
        readerService.close();
        bookService.close();
        loanService.close();
    }
}