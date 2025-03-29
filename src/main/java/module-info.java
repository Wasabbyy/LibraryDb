module org.example.librarydb {
    requires javafx.controls;
    requires javafx.fxml;

    // JPA and Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // H2 database (if you want another, adjust it)
    requires org.apache.logging.log4j;

    // Open packages for Hibernate (JPA needs access to entities)
    opens org.example.librarydb to javafx.fxml, org.hibernate.orm.core;
    opens org.example.librarydb.model to org.hibernate.orm.core; // If you store entities in a separate package

    exports org.example.librarydb;
    exports org.example.librarydb.model;
    exports org.example.librarydb.service;
}