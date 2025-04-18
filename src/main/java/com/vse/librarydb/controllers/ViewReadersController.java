package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.model.Reader;
import com.vse.librarydb.service.ReaderService;

import java.io.IOException;
import java.util.List;

public class ViewReadersController extends BaseController {
    @FXML
    private ListView<String> readersListView;

    @FXML
    private Button backButton; // Define the backButton field

    private ReaderService readerService;

    public ViewReadersController() {
        readerService = new ReaderService();
    }



    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/view-data.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setWidth(1000);  // ← This should match or exceed VBox prefWidth
        stage.setHeight(800); // Use backButton to get the stage
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    public void initialize() {
        List<Reader> readers = readerService.getAllReaders();
        for (Reader reader : readers) {
            readersListView.getItems().add(reader.getName() + " (" + reader.getEmail() + ")");
        }

        readersListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Double-click to open details
                int selectedIndex = readersListView.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0) {
                    Reader selectedReader = readers.get(selectedIndex);
                    try {
                        openReaderDetails(selectedReader);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void openReaderDetails(Reader reader) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/reader-details.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        ReaderDetailsController controller = fxmlLoader.getController();
        controller.setReader(reader); // Pass the selected reader to the new controller
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setWidth(1000);  // ← This should match or exceed VBox prefWidth
        stage.setHeight(800);
        stage.setTitle("Reader Details");
        stage.show();
    }
}