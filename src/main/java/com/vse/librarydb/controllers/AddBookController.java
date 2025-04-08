// AddBookController.java
package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.service.BookService;

import java.io.IOException;

public class AddBookController extends BaseController {
    @FXML
    private TextField titleField;
    @FXML
    private TextField authorField;
    @FXML
    private TextField publicationYearField;

    private BookService bookService;

    public AddBookController() {
        bookService = new BookService();
    }

    @FXML
    protected void onAddBookButtonClick() {
        String title = titleField.getText();
        String author = authorField.getText();
        int publicationYear = Integer.parseInt(publicationYearField.getText());
        bookService.addBook(title, author, publicationYear, true);
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) titleField.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }
}