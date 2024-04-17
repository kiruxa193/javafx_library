package com.vojtechruzicka.javafxweaverexample.model;

import javafx.beans.property.*;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "book")
public class Book implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_number")
    @EqualsAndHashCode.Exclude
    private int bookNumber;

    @Column(name = "book_cipher")
    private String bookCipher;

    @Column(name = "author")
    private String author;

    @Column(name = "title")
    private String title;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "publish_year")
    private int publicationYear;

    @Column(name = "price")
    private double price;

    @Column(name = "receipt_date")
    private Date acquisitionDate;


    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "storage_number", referencedColumnName = "storage_number")
    private Storage storageNumber;

    @OneToMany(mappedBy = "bookNumber")
    private List<BookLoan> bookLoans;

    // Генерация JavaFX Properties

    private final transient IntegerProperty bookNumberProperty =
            new SimpleIntegerProperty(this, "bookNumber");
    private final transient StringProperty bookCipherProperty =
            new SimpleStringProperty(this, "bookCipher");
    private final transient StringProperty authorProperty =
            new SimpleStringProperty(this, "author");
    private final transient StringProperty titleProperty =
            new SimpleStringProperty(this, "title");
    private final transient StringProperty publisherProperty =
            new SimpleStringProperty(this, "publisher");
    private final transient IntegerProperty publicationYearProperty =
            new SimpleIntegerProperty(this, "publicationYear");
    private final transient DoubleProperty priceProperty =
            new SimpleDoubleProperty(this, "price");
    private final transient StringProperty acquisitionDateAsStringProperty =
            new SimpleStringProperty(this, "acquisitionDateAsString");
    private final transient IntegerProperty storageNumberProperty =
            new SimpleIntegerProperty(this, "storageNumber");

    public IntegerProperty bookNumberProperty() {
        return bookNumberProperty;
    }

    public StringProperty bookCipherProperty() {
        return bookCipherProperty;
    }

    public StringProperty authorProperty() {
        return authorProperty;
    }

    public StringProperty titleProperty() {
        return titleProperty;
    }

    public StringProperty publisherProperty() {
        return publisherProperty;
    }

    public IntegerProperty publicationYearProperty() {
        return publicationYearProperty;
    }

    public DoubleProperty priceProperty() {
        return priceProperty;
    }


    public StringProperty acquisitionDateAsStringProperty() {
        return acquisitionDateAsStringProperty;
    }

    @PostLoad
    private void postLoad() {

        bookNumberProperty.set(bookNumber);
        bookCipherProperty.set(bookCipher);
        authorProperty.set(author);
        titleProperty.set(title);
        publisherProperty.set(publisher);
        publicationYearProperty.set(publicationYear);
        priceProperty.set(price);
        storageNumberProperty.set(getStorageNumberAsInt());
        acquisitionDateAsStringProperty.set(acquisitionDate.
                toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    }

    public IntegerProperty storageNumberProperty() {
        return storageNumberProperty;
    }

    public int getStorageNumberAsInt() {
        return storageNumber.getStorageNumber();
    }

}
