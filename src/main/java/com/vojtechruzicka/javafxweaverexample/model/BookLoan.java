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
@Table(name = "book_loan")
public class BookLoan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loan_number")
    private int bookLoanNumber;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "ticket_number", referencedColumnName = "ticket_number")
    private Subscriber ticketNumber;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "book_number", referencedColumnName = "book_number")
    private Book bookNumber;

    @Column(name = "loan_date")
    private Date loanDate;

    @Column(name = "return_date")
    private Date returnDate;



    private final transient IntegerProperty bookLoanNumberProperty =
            new SimpleIntegerProperty(this, "bookLoanNumber");
    private final transient IntegerProperty ticketNumberProperty =
            new SimpleIntegerProperty(this, "ticketNumber");
    private final transient IntegerProperty bookNumberProperty =
            new SimpleIntegerProperty(this, "bookNumber");
    private final transient StringProperty loanDateAsStringProperty =
            new SimpleStringProperty(this, "loanDateAsString");
    private final transient StringProperty returnDateAsStringProperty =
            new SimpleStringProperty(this, "returnDateAsString");


    public IntegerProperty bookLoanNumberProperty() {
        return bookLoanNumberProperty;
    }
    public IntegerProperty bookNumberProperty() {
        return bookNumberProperty;
    }

    public IntegerProperty ticketNumberProperty() {
        return ticketNumberProperty;
    }

    public StringProperty loanDateAsStringProperty() {
        return loanDateAsStringProperty;
    }

    public StringProperty returnDateAsStringProperty() {
        return returnDateAsStringProperty;
    }


    @PostLoad
    private void postLoad() {

        bookLoanNumberProperty.set(bookLoanNumber);
        bookNumberProperty.set(getBookNumberAsInt());
        ticketNumberProperty.set(getTicketNumberAsInt());
        loanDateAsStringProperty.set(loanDate.
                toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        returnDateAsStringProperty.set(returnDate.
                toLocalDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

    }

    public int getBookNumberAsInt(){
        return bookNumber.getBookNumber();
    }

    public int getTicketNumberAsInt(){
        return ticketNumber.getTicketNumber();
    }
}
