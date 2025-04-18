package com.vse.librarydb.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;
import javafx.event.ActionEvent;

import java.io.IOException;

public class IntroController {
    @FXML
    private VBox root;

    private void loadScene(String fxmlFile) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlFile);
            throw e;
        }
    }

    @FXML
    protected void onAddReaderButtonClick() throws IOException {
        loadScene("add-reader.fxml");
    }

    @FXML
    protected void onAddBookButtonClick() throws IOException {
        loadScene("add-book-view.fxml");
    }

    @FXML
    protected void onAddLoanButtonClick() throws IOException {
        loadScene("add-loan-view.fxml");
    }

    @FXML
    protected void onViewDataButtonClick() throws IOException {
        loadScene("view-data.fxml");
    }

    @FXML
    protected void onReturnBooksButtonClick() throws IOException {
        loadScene("return-books-view.fxml");
    }
    // In your IntroController class
    @FXML
    private void onExitButtonClick(ActionEvent event) {
        Platform.exit();
    }
}