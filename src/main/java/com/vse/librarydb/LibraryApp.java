package com.vse.librarydb;

import com.vse.librarydb.controllers.DatabaseStatusMonitor;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibraryApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("intro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        DatabaseStatusMonitor.getInstance().shutdown();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}