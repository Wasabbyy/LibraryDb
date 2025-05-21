package com.vse.librarydb.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import com.vse.librarydb.LibraryApp;

import java.io.IOException;

public class BaseController implements DatabaseStatusMonitor.DatabaseStatusListener {
    @FXML
    protected Label databaseStatusLabel;

    @FXML
    protected void onReturnToMenuButtonClick(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("intro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setScene(scene);
        stage.setWidth(1000);
        stage.setHeight(600);
        stage.show();
    }

    @Override
    public void onDatabaseConnected() {
        if (databaseStatusLabel != null) {
            databaseStatusLabel.setText("Database: Connected");
            databaseStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");

        }
    }

    @Override
    public void onDatabaseDisconnected() {
        if (databaseStatusLabel != null) {
            databaseStatusLabel.setText("Database: Disconnected");
            databaseStatusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            showAlert(Alert.AlertType.WARNING, "Warning", "Database Disconnected",
                    "Working in offline mode. Changes won't be saved until reconnected.");
        }
    }

    protected void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
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

}