module com.vse.librarydb {
    requires javafx.controls;
    requires javafx.fxml;

    // JPA and Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires jakarta.validation;
    requires org.hibernate.validator;

    // Logging
    requires org.apache.logging.log4j;
    requires java.desktop;

    // Open packages for Hibernate and Validator
    opens com.vse.librarydb to javafx.fxml, org.hibernate.orm.core;
    opens com.vse.librarydb.model to
            org.hibernate.orm.core,
            org.hibernate.validator,
            javafx.base;  // Add this if you're using JavaFX bindings with your model

    // Export packages
    exports com.vse.librarydb;
    exports com.vse.librarydb.model;
    exports com.vse.librarydb.service;
    exports com.vse.librarydb.controllers;
    opens com.vse.librarydb.controllers to javafx.fxml;
}