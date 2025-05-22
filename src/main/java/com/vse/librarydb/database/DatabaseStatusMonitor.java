package com.vse.librarydb.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseStatusMonitor {
    private static final Logger logger = Logger.getLogger(DatabaseStatusMonitor.class.getName());
    private static DatabaseStatusMonitor instance;
    private boolean databaseAvailable = true;
    private final List<DatabaseStatusListener> listeners = new ArrayList<>();
    private ScheduledExecutorService monitorService;

    private DatabaseStatusMonitor() {
        startMonitoring();
    }

    public static synchronized DatabaseStatusMonitor getInstance() {
        if (instance == null) {
            instance = new DatabaseStatusMonitor();
        }
        return instance;
    }

    public boolean isDatabaseAvailable() {
        return databaseAvailable;
    }

    public void addListener(DatabaseStatusListener listener) {
        listeners.add(listener);
    }

    public void removeListener(DatabaseStatusListener listener) {
        listeners.remove(listener);
    }

    private void startMonitoring() {
        if (monitorService != null && !monitorService.isShutdown()) {
            monitorService.shutdown();
        }

        monitorService = Executors.newSingleThreadScheduledExecutor();
        monitorService.scheduleAtFixedRate(this::checkDatabaseStatus, 0, 5, TimeUnit.SECONDS);
    }

    private void checkDatabaseStatus() {
        boolean wasAvailable = databaseAvailable;

        try {
            // Actually test the database connection
            // This example uses a simple query - adjust to your actual database setup
            Connection connection = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/test", "sa", "");
            try (Statement stmt = connection.createStatement()) {
                stmt.executeQuery("SELECT 1");  // Simple test query
            }
            connection.close();
            databaseAvailable = true;
            logger.log(Level.INFO, "Database connection is available");
        } catch (Exception e) {
            databaseAvailable = false;
            logger.log(Level.SEVERE, "Database connection failed", e);
        }

        if (wasAvailable != databaseAvailable) {
            notifyListeners(databaseAvailable);
        }
    }

    private void notifyListeners(boolean available) {
        for (DatabaseStatusListener listener : listeners) {
            try {
                if (available) {
                    listener.onDatabaseConnected();
                } else {
                    listener.onDatabaseDisconnected();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error notifying listener", e);
            }
        }
    }

    public void shutdown() {
        if (monitorService != null) {
            monitorService.shutdown();
        }
    }

    public interface DatabaseStatusListener {
        void onDatabaseConnected();
        void onDatabaseDisconnected();
    }
}