package com.vse.librarydb.database;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:h2:./data/librarydb", "sa", "");
    }
}