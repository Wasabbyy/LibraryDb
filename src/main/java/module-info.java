module com.vse.librarydb {
    requires javafx.controls;
    requires javafx.fxml;

    // JPA and Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // H2 database (if you want another, adjust it)
    requires org.apache.logging.log4j;

    // Open packages for Hibernate (JPA needs access to entities)
    opens com.vse.librarydb to javafx.fxml, org.hibernate.orm.core;
    opens com.vse.librarydb.model to org.hibernate.orm.core; // If you store entities in a separate package

    exports com.vse.librarydb;
    exports com.vse.librarydb.model;
    exports com.vse.librarydb.service;
    exports com.vse.librarydb.controllers;
    opens com.vse.librarydb.controllers to javafx.fxml, org.hibernate.orm.core;
}