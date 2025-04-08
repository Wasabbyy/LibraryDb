package org.example.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import org.example.librarydb.LibraryApp;
import org.example.librarydb.model.Book;
import org.example.librarydb.service.BookService;

import java.io.IOException;
import java.util.List;

public class ViewBooksController extends BaseController {
    @FXML
    private ListView<String> booksListView;

    @FXML
    private Button backButton;

    private BookService bookService;

    public ViewBooksController() {
        bookService = new BookService();
    }

    @FXML
    public void initialize() {
        List<Book> books = bookService.getAllBooks();
        for (Book book : books) {
            booksListView.getItems().add(book.getTitle() + " by " + book.getAuthor() + " (" + book.getPublicationYear() + ")");
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/org/example/librarydb/view-data.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}