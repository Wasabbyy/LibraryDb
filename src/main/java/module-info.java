module org.example.librarydb {
    requires javafx.controls;
    requires javafx.fxml;

    // JPA a Hibernate
    requires jakarta.persistence;
    requires org.hibernate.orm.core;

    // H2 databáze (pokud chceš jinou, uprav to)
    requires java.sql;

    // Otevření balíčků pro Hibernate (JPA potřebuje přístup k entitám)
    opens org.example.librarydb to javafx.fxml, org.hibernate.orm.core;
    opens org.example.librarydb.model to org.hibernate.orm.core; // Pokud ukládáš entity do samostatného balíčku

    exports org.example.librarydb;
}
