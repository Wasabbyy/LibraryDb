package com.vse.librarydb.controllers;

import com.vse.librarydb.LibraryApp;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewDataController extends BaseController {
    @FXML private Button backButton;
    @FXML private Button viewReadersButton;
    @FXML private Button viewBooksButton;
    @FXML private Button viewLoansButton;

    @FXML
    public void initialize() {
        initializeDatabaseStatus(); // Initialize the status label
    }
    public ViewDataController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
    }

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
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) backButton.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    @Override
    public void onDatabaseConnected() {
        super.onDatabaseConnected();
        // No data to refresh in this controller
    }

    @Override
    public void onDatabaseDisconnected() {
        super.onDatabaseDisconnected();
    }

    private void navigateTo(String fxmlFile) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        stage.show();
    }
}