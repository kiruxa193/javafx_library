package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.model.BookLoan;
import com.vojtechruzicka.javafxweaverexample.model.Subscriber;
import com.vojtechruzicka.javafxweaverexample.service.BookLoanService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.sql.Date;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@FxmlView("bookLoan-view.fxml")
public class BookLoanController {

    @FXML
    private TableView<BookLoan> bookLoanTable;

    @FXML
    private HBox addBookLoanFields;

    @FXML
    private HBox updateBookLoanFields;

    @FXML
    private HBox deleteBookLoanFields;

    @FXML
    private ComboBox<Integer> bookLoanNumberComboBox;

    private final ObservableList<Integer> bookLoanNumbers = FXCollections.observableArrayList();

    @FXML
    private TextField ticketNumberFieldAdd, bookNumberFieldAdd, loanDateFieldAdd, returnDateFieldAdd,
            ticketNumberFieldUpdate, bookNumberFieldUpdate, loanDateFieldUpdate, returnDateFieldUpdate,
            deleteBookLoanField;

    private final BookLoanService bookLoanService;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public BookLoanController(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    @FXML
    private void goBackToMainMenu(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Закрываем текущее окно
        stage.close();
    }


    @FXML
    private void initialize() {
        setupTableColumns();
        initializeValidation();
        initializeVisibilityBindings();
        initializeComboBox();
        refreshTable();
    }

    private void setupTableColumns() {

        TableColumn<BookLoan, Integer> bookLoanNumberColumn = new TableColumn<>("BookLoan Number");
        bookLoanNumberColumn.setCellValueFactory(cellData -> cellData.getValue().bookLoanNumberProperty().asObject());

        TableColumn<BookLoan, Integer> ticketNumberColumn = new TableColumn<>("Ticket Number");
        ticketNumberColumn.setCellValueFactory(cellData -> cellData.getValue().ticketNumberProperty().asObject());

        TableColumn<BookLoan, Integer> bookNumberColumn = new TableColumn<>("Book Number");
        bookNumberColumn.setCellValueFactory(cellData -> cellData.getValue().bookNumberProperty().asObject());

        TableColumn<BookLoan, String> loanDateColumn = new TableColumn<>("Loan Date");
        loanDateColumn.setCellValueFactory(cellData -> cellData.getValue().loanDateAsStringProperty());

        TableColumn<BookLoan, String> returnDateColumn = new TableColumn<>("Return Date");
        returnDateColumn.setCellValueFactory(cellData -> cellData.getValue().returnDateAsStringProperty());


        bookLoanTable.getColumns().addAll(bookLoanNumberColumn, ticketNumberColumn, bookNumberColumn, loanDateColumn,
                returnDateColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(ticketNumberFieldAdd, Validator
                .createEmptyValidator("Ticket Number is required"));
        validationSupport.registerValidator(bookNumberFieldAdd, Validator
                .createEmptyValidator("Book Number is required"));
        validationSupport.registerValidator(loanDateFieldAdd, Validator
                .createRegexValidator("Invalid Date Format",
                        "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
        validationSupport.registerValidator(returnDateFieldAdd, Validator
                .createRegexValidator("Invalid Date Format",
                        "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));

//        validationSupport.registerValidator(ticketNumberFieldUpdate, Validator
//                .createEmptyValidator("Ticket Number is required"));
//        validationSupport.registerValidator(bookNumberFieldUpdate, Validator
//                .createEmptyValidator("Book Number is required"));
//        validationSupport.registerValidator(loanDateFieldUpdate, Validator
//                .createRegexValidator("Invalid Date Format",
//                        "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
//        validationSupport.registerValidator(returnDateFieldUpdate, Validator
//                .createRegexValidator("Invalid Date Format",
//                        "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
//        validationSupport.registerValidator(deleteBookLoanField, Validator
//                .createEmptyValidator("Book Loan Number is required"));
    }

    private void initializeVisibilityBindings() {
        addBookLoanFields.managedProperty().bind(addBookLoanFields.visibleProperty());
        updateBookLoanFields.managedProperty().bind(updateBookLoanFields.visibleProperty());
        deleteBookLoanFields.managedProperty().bind(deleteBookLoanFields.visibleProperty());
    }

    private void initializeComboBox() {
        bookLoanNumbers.addAll(bookLoanService.getAllBookLoanNumbers());
        bookLoanNumberComboBox.setItems(bookLoanNumbers);
    }

    @FXML
    private void handleAddBookLoan(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        BookLoan newBookLoan = createBookLoanFromFields(ticketNumberFieldAdd, bookNumberFieldAdd,
                loanDateFieldAdd, returnDateFieldAdd);

        System.out.println(newBookLoan.getBookLoanNumber());
        bookLoanService.addBookLoan(newBookLoan);

        refreshTable();
    }

    @FXML
    private void handleUpdateBook(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedBookNumber = bookLoanNumberComboBox.getValue();
        BookLoan selectedBookLoan = bookLoanService.getBookLoanByBookLoanNumber(selectedBookNumber).orElseThrow(() -> new NoSuchElementException("BookLoan is not present"));

        updateBookFromFields(selectedBookLoan, ticketNumberFieldUpdate,
                bookNumberFieldUpdate, loanDateFieldUpdate, returnDateFieldUpdate);

        bookLoanService.updateBookLoan(selectedBookLoan);
        refreshTable();
    }

    private void updateBookFromFields(BookLoan bookLoan, TextField... fields) {

        Subscriber subscriber = new Subscriber();
        subscriber.setTicketNumber(Integer.parseInt(fields[0].getText()));
        bookLoan.setTicketNumber(subscriber);
        Book book = new Book();
        book.setBookNumber(Integer.parseInt(fields[1].getText()));
        bookLoan.setBookNumber(book);
        bookLoan.setLoanDate(Date.valueOf(fields[2].getText()));
        bookLoan.setReturnDate(Date.valueOf(fields[3].getText()));
    }

    private void showValidationErrorDialog() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText("Invalid Input");

        StringBuilder contentText = new StringBuilder("Please correct the invalid fields:\n");

        // Получаем результат валидации и проходимся по сообщениям об ошибках
        validationSupport.getValidationResult().getMessages().forEach(message -> {
            // Используем promptText вместо userData, если доступен
            Node errorNode = message.getTarget();
            String fieldName = "";

            if (errorNode instanceof TextInputControl) {
                fieldName = ((TextInputControl) errorNode).getPromptText();
            }

            // Добавляем сообщения об ошибках в текст контента
            contentText.append(fieldName).append(": ").append(message.getText()).append("\n");
        });

        alert.setContentText(contentText.toString());
        alert.showAndWait();
    }

    private BookLoan createBookLoanFromFields(TextField... fields) {

        BookLoan newBookLoan = new BookLoan();
        Subscriber subscriber = new Subscriber();
        subscriber.setTicketNumber(Integer.parseInt(fields[0].getText()));
        newBookLoan.setTicketNumber(subscriber);
        Book book = new Book();
        book.setBookNumber(Integer.parseInt(fields[1].getText()));
        newBookLoan.setBookNumber(book);
        newBookLoan.setLoanDate(Date.valueOf(fields[2].getText()));
        newBookLoan.setReturnDate(Date.valueOf(fields[3].getText()));

        return newBookLoan;
    }

    @FXML
    private void handleSelectBook(ActionEvent event) {

        try {
            Integer selectedBookLoanNumber = bookLoanNumberComboBox.getValue();
            BookLoan selectedBookLoan = bookLoanService.getBookLoanByBookLoanNumber(selectedBookLoanNumber).orElseThrow(() -> new NoSuchElementException("BookLoan is not present"));


            ticketNumberFieldUpdate.setText(String.valueOf(selectedBookLoan.getTicketNumber()));
            bookNumberFieldUpdate.setText(String.valueOf(selectedBookLoan.getBookNumber()));
            loanDateFieldUpdate.setText(String.valueOf(selectedBookLoan.getLoanDate()));
            returnDateFieldUpdate.setText(String.valueOf(selectedBookLoan.getReturnDate()));

            updateBookLoanFields.setVisible(true);

        }
        catch (NullPointerException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Choose value");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleDeleteBook(ActionEvent event) {

        try {

            int bookLoanNumberToDelete = Integer.parseInt(deleteBookLoanField.getText());
            Optional<BookLoan> optionalBookLoan = bookLoanService.getBookLoanByBookLoanNumber(bookLoanNumberToDelete);

            if (optionalBookLoan.isPresent()) {
                BookLoan selectedBookLoan = optionalBookLoan.get();
                bookLoanService.deleteBookLoan(selectedBookLoan.getBookLoanNumber());
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("BookLoan not found for the given book number");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Field is empty");
            alert.showAndWait();

        }
    }

    public void refreshTable() {
        bookLoanTable.getItems().clear();
        bookLoanTable.getItems().addAll(bookLoanService.getAllBookLoans());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addBookLoanFields.setVisible(true);
        updateBookLoanFields.setVisible(false);
        deleteBookLoanFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addBookLoanFields.setVisible(false);
        updateBookLoanFields.setVisible(true);
        deleteBookLoanFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addBookLoanFields.setVisible(false);
        updateBookLoanFields.setVisible(false);
        deleteBookLoanFields.setVisible(true);
    }

    private void clearUpdateFields() {
        ticketNumberFieldUpdate.clear();
        bookNumberFieldUpdate.clear();
        loanDateFieldUpdate.clear();
        returnDateFieldUpdate.clear();
    }
}
