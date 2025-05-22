package com.vse.librarydb.controllers;

import com.vse.librarydb.LibraryApp;
import com.vse.librarydb.database.DatabaseStatusMonitor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.logging.Logger;

public class ViewDataController extends BaseController {
    private static final Logger logger = Logger.getLogger(ViewDataController.class.getName());

    @FXML private Button backButton;
    @FXML private Button viewReadersButton;
    @FXML private Button viewBooksButton;
    @FXML private Button viewLoansButton;

    @FXML
    public void initialize() {
        logger.info("Initializing ViewDataController");
        initializeDatabaseStatus();
    }

    public ViewDataController() {
        DatabaseStatusMonitor.getInstance().addListener(this);
        logger.info("ViewDataController registered as database listener.");
    }

    @FXML
    protected void onViewReadersButtonClick() throws IOException {
        logger.info("View Readers button clicked.");
        navigateTo("view-readers.fxml");
    }

    @FXML
    protected void onViewBooksButtonClick() throws IOException {
        logger.info("View Books button clicked.");
        navigateTo("view-books.fxml");
    }

    @FXML
    protected void onViewLoansButtonClick() throws IOException {
        logger.info("View Loans button clicked.");
        navigateTo("view-loans.fxml");
    }

    @FXML
    protected void onReturnToMenuButtonClick() throws IOException {
        logger.info("Returning to main menu.");
        DatabaseStatusMonitor.getInstance().removeListener(this);
        Stage stage = (Stage) backButton.getScene().getWindow();
        super.onReturnToMenuButtonClick(stage);
    }

    @Override
    public void onDatabaseConnected() {
        logger.info("Database connected.");
        super.onDatabaseConnected();
    }

    @Override
    public void onDatabaseDisconnected() {
        logger.warning("Database disconnected.");
        super.onDatabaseDisconnected();
    }

    private void navigateTo(String fxmlFile) throws IOException {
        logger.info("Navigating to " + fxmlFile);
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        stage.show();
    }
}
