package com.vse.librarydb.controllers;

import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.database.DatabaseStatusMonitor;
import com.vse.librarydb.model.Book;
import com.vse.librarydb.service.BookService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ViewBooksController extends BaseController {
    private static final Logger LOGGER = Logger.getLogger(ViewBooksController.class.getName());

    @FXML private ListView<String> booksListView;
    @FXML private Button backButton;
    @FXML private TextField searchField;
    @FXML private Button deleteBookButton;

    private final BookService bookService = new BookService();
    private List<Book> allBooks;

    public ViewBooksController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

    @FXML
    public void initialize() {
        loadBooks();
        initializeDatabaseStatus();
        deleteBookButton.setOnAction(event -> onDeleteBook());

        booksListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedIndex = booksListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && !allBooks.isEmpty()) {
                    Book book = allBooks.get(selectedIndex);
                    try {
                        openBookDetails(book);
                    } catch (IOException e) {
                        LOGGER.severe("Error opening book details: " + e.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open book details", e.getMessage());
                    }
                }
            }
        });

        LOGGER.info("ViewBooksController initialized.");
    }

    private void loadBooks() {
        allBooks = bookService.getAllBooks();
        refreshBookList(allBooks);
        LOGGER.info("Loaded " + allBooks.size() + " books.");
    }

    @FXML
    private void onSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Book> filtered = allBooks.stream()
                .filter(book -> book.getTitle().toLowerCase().contains(searchTerm)
                        || book.getAuthor().toLowerCase().contains(searchTerm)
                        || String.valueOf(book.getPublicationYear()).contains(searchTerm))
                .collect(Collectors.toList());
        refreshBookList(filtered);
        LOGGER.info("Search performed with term: " + searchTerm);
    }

    private void refreshBookList(List<Book> books) {
        booksListView.getItems().clear();
        for (Book book : books) {
            String availability = book.isAvailable() ? "Available" : "Not Available";
            booksListView.getItems().add(book.getTitle() + " by " + book.getAuthor() + " (" + book.getPublicationYear() + ") - " + availability);
        }
    }

    private void openBookDetails(Book book) throws IOException {
        FXMLLoader loader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/book-details.fxml"));
        Scene scene = new Scene(loader.load());
        BookDetailsController controller = loader.getController();
        controller.setBook(book);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setTitle("Book Details");
        stage.show();
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        DatabaseStatusMonitor.getInstance().removeListener(this);
        FXMLLoader loader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/view-data.fxml"));
        Scene scene = new Scene(loader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
    }

    @FXML
    private void onDeleteBook() {
        int selectedIndex = booksListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && !allBooks.isEmpty()) {
            Book selectedBook = allBooks.get(selectedIndex);
            boolean confirm = showConfirmationDialog(
                    "Delete Book", "Are you sure you want to delete this book?",
                    "This will permanently delete '" + selectedBook.getTitle() + "' from the database."
            );

            if (confirm) {
                boolean success = bookService.deleteBook(selectedBook.getId().intValue());
                if (success) {
                    allBooks.remove(selectedIndex);
                    refreshBookList(allBooks);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Book Deleted", "The book has been successfully deleted.");
                    LOGGER.info("Deleted book: " + selectedBook.getTitle());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Delete Failed", "Failed to delete the book. It might be currently loaned.");
                    LOGGER.warning("Failed to delete book: " + selectedBook.getTitle());
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Book Selected", "Please select a book in the list.");
        }
    }
}
