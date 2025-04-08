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
    public void initialize() {
        List<Reader> readers = readerService.getAllReaders();
        for (Reader reader : readers) {
            readersListView.getItems().add(reader.toString());
        }
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("/com/vse/librarydb/view-data.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow(); // Use backButton to get the stage
        stage.setScene(scene);
        stage.show();
    }
}