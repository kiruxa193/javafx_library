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
@Table(name = "subscriber")
public class Subscriber implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_number")
    private int ticketNumber;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "ticketNumber")
    private List<BookLoan> bookLoans;


    private final transient IntegerProperty ticketNumberProperty =
            new SimpleIntegerProperty(this, "ticketNumber");
    private final transient StringProperty lastNameProperty =
            new SimpleStringProperty(this, "lastName");
    private final transient StringProperty firstNameProperty =
            new SimpleStringProperty(this, "firstName");
    private final transient StringProperty middleNameProperty =
            new SimpleStringProperty(this, "middleName");
    private final transient StringProperty addressProperty =
            new SimpleStringProperty(this, "address");
    private final transient StringProperty phoneNumberProperty =
            new SimpleStringProperty(this, "phoneNumber");


    public IntegerProperty ticketNumberProperty() {
        return ticketNumberProperty;
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


    public StringProperty addressProperty() {
        return addressProperty;
    }


    public StringProperty phoneNumberProperty() {
        return phoneNumberProperty;
    }

    @PostLoad
    private void postLoad() {
        ticketNumberProperty.set(ticketNumber);
        lastNameProperty.set(lastName);
        firstNameProperty.set(firstName);
        middleNameProperty.set(middleName);
        addressProperty.set(address);
        phoneNumberProperty.set(phoneNumber);
    }
}
