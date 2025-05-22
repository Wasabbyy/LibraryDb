package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.ReaderService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ViewReadersController extends BaseController {
    @FXML
    private ListView<String> readersListView;
    @FXML
    private Button backButton;
    @FXML
    private TextField searchField;

    @FXML
    private Button deleteReaderButton;

    private ReaderService readerService;
    private List<Reader> allReaders;

    public ViewReadersController() {
        readerService = new ReaderService();
    }

    @FXML
    public void initialize() {
        allReaders = readerService.getAllReaders();
        refreshReaderList(allReaders);
        initializeDatabaseStatus();
        deleteReaderButton.setOnAction(event -> onDeleteReader());

        readersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                int selectedIndex = readersListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Reader selectedReader = allReaders.get(selectedIndex);
                    try {
                        openReaderDetails(selectedReader);
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
        List<Reader> filteredReaders = allReaders.stream()
                .filter(reader -> reader.getName().toLowerCase().contains(searchTerm) ||
                        reader.getEmail().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
        refreshReaderList(filteredReaders);
    }

    private void refreshReaderList(List<Reader> readers) {
        readersListView.getItems().clear();
        for (Reader reader : readers) {
            readersListView.getItems().add(reader.getName() + " (" + reader.getEmail() + ")");
        }
    }

    private void openReaderDetails(Reader reader) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/reader-details.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ReaderDetailsController controller = fxmlLoader.getController();
        controller.setReader(reader);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setTitle("Reader Details");
        stage.show();
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
    @FXML
    private void onDeleteReader() {
        int selectedIndex = readersListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            Reader selectedReader = allReaders.get(selectedIndex);

            boolean confirm = showConfirmationDialog(
                    "Delete Reader",
                    "Are you sure you want to delete this reader?",
                    "This will permanently delete " + selectedReader.getName() + " from the database."
            );

            if (confirm) {
                boolean success = readerService.deleteReader(selectedReader.getId().intValue());
                if (success) {
                    allReaders.remove(selectedIndex);
                    refreshReaderList(allReaders);
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reader Deleted",
                            "The reader has been successfully deleted.");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Delete Failed",
                            "Failed to delete the reader from the database.");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "No Reader Selected",
                    "Please select a reader in the list.");
        }
    }
}