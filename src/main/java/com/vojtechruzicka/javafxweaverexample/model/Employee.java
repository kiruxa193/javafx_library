package com.vojtechruzicka.javafxweaverexample.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Date;
import java.time.format.DateTimeFormatter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_number")
    private int employeeId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "library_number", referencedColumnName = "library_number")
    private Library libraryNumberEmpl;

    @Column(name = "surname")
    private String lastName;

    @Column(name = "name")
    private String firstName;

    @Column(name = "patronymic")
    private String middleName;

    @Column(name = "birth_date")
    private Date birthDate;

    @Column(name = "position_of_employee")
    private String position;

    @Column(name = "education")
    private String education;



    private final transient IntegerProperty employeeIdProperty =
            new SimpleIntegerProperty(this, "employeeId");
    private final transient IntegerProperty libraryNumberProperty =
            new SimpleIntegerProperty(this,"libraryNumber");
    private final transient StringProperty lastNameProperty =
            new SimpleStringProperty(this, "lastName");
    private final transient StringProperty firstNameProperty =
            new SimpleStringProperty(this, "firstName");
    private final transient StringProperty middleNameProperty =
            new SimpleStringProperty(this, "middleName");
    private final transient StringProperty birthDateAsStringProperty =
            new SimpleStringProperty(this, "birthDateAsString");
    private final transient StringProperty positionProperty =
            new SimpleStringProperty(this, "position");
    private final transient StringProperty educationProperty =
            new SimpleStringProperty(this, "education");



    public IntegerProperty employeeIdProperty() {
        return employeeIdProperty;
    }

    public IntegerProperty libraryNumberProperty(){
        return libraryNumberProperty;
    }

    public StringProperty lastNameProperty() {
        return lastNameProperty;
    }

    public StringProperty firstNameProperty() {
        return firstNameProperty;
    }

    public StringProperty middleNameProperty() {
        return middleNameProperty;
    }

    public StringProperty birthDateAsStringProperty() {
        return birthDateAsStringProperty;
    }

    public StringProperty positionProperty() {
        return positionProperty;
    }

    public StringProperty educationProperty() {
        return educationProperty;
    }

    @PostLoad
    private void postLoad() {
        employeeIdProperty.set(employeeId);
        libraryNumberProperty.set(getLibraryNumberAsInt());
        lastNameProperty.set(lastName);
        firstNameProperty.set(firstName);
        middleNameProperty.set(middleName);
        birthDateAsStringProperty.set(birthDate.
                toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        positionProperty.set(position);
        educationProperty.set(education);
    }

    public int getLibraryNumberAsInt(){
        return libraryNumberEmpl.getLibraryNumber();
    }

}


