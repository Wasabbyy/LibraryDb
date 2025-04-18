package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;

import java.io.IOException;

public class ViewDataController extends BaseController {

    @FXML
    private Button backButton; // A button in the FXML file to retrieve the stage

    @FXML
    protected void onViewReadersButtonClick() throws IOException {
        navigateTo("view-readers.fxml");
    }

    @FXML
    protected void onViewBooksButtonClick() throws IOException {
        navigateTo("view-books.fxml");
    }

    @FXML
    protected void onViewLoansButtonClick() throws IOException {
        navigateTo("view-loans.fxml");
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setWidth(1000);
        stage.setHeight(800);
        super.onReturnToMenuButtonClick(stage);
    }

    private void navigateTo(String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setWidth(1000);
        stage.setHeight(800);
        stage.setScene(scene);
        stage.show();
    }
}