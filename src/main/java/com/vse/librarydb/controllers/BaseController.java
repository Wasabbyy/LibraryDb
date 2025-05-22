package com.vse.librarydb.controllers;

import com.vse.librarydb.database.DatabaseStatusMonitor;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BaseController implements DatabaseStatusMonitor.DatabaseStatusListener {
    private static final Logger logger = Logger.getLogger(BaseController.class.getName());

    @FXML
    protected Label databaseStatusLabel;

    @FXML
    protected void onReturnToMenuButtonClick(Stage stage) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("intro-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 320, 240);
            stage.setScene(scene);
            stage.setWidth(1000);
            stage.setHeight(600);
            stage.show();
            logger.info("Returned to main menu.");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to return to menu.", e);
            throw e;
        }
    }

    @Override
    public void onDatabaseConnected() {
        if (databaseStatusLabel != null) {
            databaseStatusLabel.setText("Database: Connected");
            databaseStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            logger.info("Database connected.");
        }
    }

    @Override
    public void onDatabaseDisconnected() {
        if (databaseStatusLabel != null) {
            databaseStatusLabel.setText("Database: Disconnected");
            databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            showAlert(AlertType.WARNING, "Warning", "Database Disconnected",
                    "Working in offline mode. Changes won't be saved until reconnected.");
            logger.warning("Database disconnected.");
        }
    }

    protected void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
        logger.info("Alert shown: " + title + " - " + header);
    }

    protected void initializeDatabaseStatus() {
        if (databaseStatusLabel != null) {
            if (DatabaseStatusMonitor.getInstance().isDatabaseAvailable()) {
                onDatabaseConnected();
            } else {
                onDatabaseDisconnected();
            }
        }
    }

    protected boolean showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        logger.info("Confirmation dialog shown: " + title);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}
