package com.vojtechruzicka.javafxweaverexample.model;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
@Table(name = "storage")
public class Storage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_number")
    private int storageNumber;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "library_number", referencedColumnName = "library_number")
    private Library libraryNumber;

    @Column(name = "floor")
    private int floor;

    @Column(name = "capacity")
    private int capacity;

    @OneToMany(mappedBy = "storageNumber")
    private List<Book> books;


    public Storage(String string){

    }

    private final transient IntegerProperty storageNumberProperty =
            new SimpleIntegerProperty(this, "storageNumber");
    private final transient IntegerProperty libraryNumberProperty =
            new SimpleIntegerProperty(this, " libraryNumber");
    private final transient IntegerProperty floorProperty =
            new SimpleIntegerProperty(this, "floor");
    private final transient  IntegerProperty capacityProperty =
            new SimpleIntegerProperty(this, "capacity");


    public IntegerProperty storageNumberProperty(){
        return storageNumberProperty;
    }

    public IntegerProperty libraryNumberProperty(){
        return  libraryNumberProperty;
    }
    public IntegerProperty floorProperty(){
        return floorProperty;
    }

    public IntegerProperty capacityProperty(){
        return capacityProperty;
    }

    @PostLoad
    private void postLoad() {
        storageNumberProperty.set(storageNumber);
        libraryNumberProperty.set(getLibraryNumberAsString());
        floorProperty.set(floor);
        capacityProperty.set(capacity);

    }

    public int getLibraryNumberAsString(){
        return libraryNumber.getLibraryNumber();
    }
}
