package com.vse.librarydb;

import com.vse.librarydb.controllers.DatabaseStatusMonitor;
import com.vse.librarydb.service.BookService;
import com.vse.librarydb.service.LoanService;
import com.vse.librarydb.service.ReaderService;
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

        BookService bookService = new BookService();
        ReaderService readerService = new ReaderService();
        LoanService loanService = new LoanService();

        bookService.close();
        readerService.close();
        loanService.close();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}