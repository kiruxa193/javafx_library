package com.vojtechruzicka.javafxweaverexample.model;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "library")
public class Library implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "library_number")
    private int libraryNumber;

    @Column(name = "library_name")
    private String libraryName;

    @Column(name = "adress")
    private String address;

    @Column(name = "book_count")
    private int bookCount;

    @OneToMany(mappedBy = "libraryNumber")
    private List<Storage> storages;

    @OneToMany(mappedBy = "libraryNumberEmpl")
    private List<Employee> employees;

    private final transient IntegerProperty libraryNumberProperty =
            new SimpleIntegerProperty(this, "libraryNumber");
    private final transient StringProperty libraryNameProperty =
            new SimpleStringProperty(this, "libraryName");
    private final transient StringProperty addressProperty =
            new SimpleStringProperty(this, "address");
    private final transient IntegerProperty bookCountProperty =
            new SimpleIntegerProperty(this, "bookCount");

    public IntegerProperty libraryNumberProperty(){
        return libraryNumberProperty;
    }

    public StringProperty libraryNameProperty(){
        return libraryNameProperty;
    }

    public StringProperty addressProperty(){
        return addressProperty;
    }

    public IntegerProperty bookCountProperty(){
        return bookCountProperty;
    }

    @PostLoad
    private void postLoad() {
       libraryNumberProperty.set(libraryNumber);
       libraryNameProperty.set(libraryName);
       addressProperty.set(address);
       bookCountProperty.set(bookCount);

    }

}
