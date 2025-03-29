package org.example.librarydb;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.librarydb.HelloController;

public class LibraryApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(LibraryApp.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Library App");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        HelloController controller = new HelloController();
        controller.close();
    }

    public static void main(String[] args) {
        launch();
    }
}