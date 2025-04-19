package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;

import java.io.IOException;

public class BaseController {
    @FXML
    protected void onReturnToMenuButtonClick(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("intro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        stage.setWidth(1000);  // ← This should match or exceed VBox prefWidth
        stage.setHeight(600);
        stage.show();
    }
}