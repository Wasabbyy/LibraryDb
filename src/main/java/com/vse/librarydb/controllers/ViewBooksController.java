package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.BookService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ViewBooksController extends BaseController {
    @FXML
    private ListView<String> booksListView;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;

    private BookService bookService;
    private List<Book> allBooks;

    public ViewBooksController() {
        bookService = new BookService();
    }

    @FXML
    public void initialize() {
        allBooks = bookService.getAllBooks();
        refreshBookList(allBooks);

        booksListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedIndex = booksListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Book selectedBook = allBooks.get(selectedIndex);
                    try {
                        openBookDetails(selectedBook);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @FXML
    private void onSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Book> filteredBooks = allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(searchTerm) ||
                        book.getAuthor().toLowerCase().contains(searchTerm) ||
                        String.valueOf(book.getPublicationYear()).contains(searchTerm))
                .collect(Collectors.toList());
        refreshBookList(filteredBooks);
    }

    private void refreshBookList(List<Book> books) {
        booksListView.getItems().clear();
        for (Book book : books) {
            String availability = book.isAvailable() ? "Available" : "Not Available";
            booksListView.getItems().add(book.getTitle() + " by " + book.getAuthor() +
                    " (" + book.getPublicationYear() + ") - " + availability);
        }
    }

    private void openBookDetails(Book book) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/book-details.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        BookDetailsController controller = fxmlLoader.getController();
        controller.setBook(book);
        Stage stage = new Stage();
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setScene(scene);
        stage.setTitle("Book Details");
        stage.show();
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/view-data.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
    }
}