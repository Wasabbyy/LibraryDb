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
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntroController {
    private static final Logger logger = Logger.getLogger(IntroController.class.getName());

    @FXML
    private VBox root;

    private void loadScene(String fxmlFile) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource(fxmlFile));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            logger.info("Loaded scene: " + fxmlFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load FXML file: " + fxmlFile, e);
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

    @FXML
    private void onExitButtonClick(ActionEvent event) {
        logger.info("Application exit initiated.");
        Platform.exit();
    }
}
