package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Book;
import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.validation.Severity;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@FxmlView("book-view.fxml")
public class BookController{



    @FXML
    private TableView<Book> bookTable;

    @FXML
    private HBox addBookFields;

    @FXML
    private HBox updateBookFields;

    @FXML
    private HBox deleteBookFields;

    @FXML
    private ComboBox<Integer> bookNumberComboBox;

    private final ObservableList<Integer> bookNumbers = FXCollections.observableArrayList();

    @FXML
    private TextField bookCipherFieldAdd, authorFieldAdd, titleFieldAdd, publisherFieldAdd,
            publicationYearFieldAdd, priceFieldAdd, acquisitionDateFieldAdd, storageNumberFieldAdd,
            bookCipherFieldUpdate, authorFieldUpdate, titleFieldUpdate, publisherFieldUpdate,
            publicationYearFieldUpdate, priceFieldUpdate, acquisitionDateFieldUpdate, storageNumberFieldUpdate,
            deleteBookField;

    private final BookService bookService;


    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
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
        TableColumn<Book, Integer> bookNumberColumn = new TableColumn<>("Book Number");
        bookNumberColumn.setCellValueFactory(cellData -> cellData.getValue().bookNumberProperty().asObject());

        TableColumn<Book, String> bookCipherColumn = new TableColumn<>("Book Cipher");
        bookCipherColumn.setCellValueFactory(cellData -> cellData.getValue().bookCipherProperty());

        TableColumn<Book, String> bookAuthorColumn = new TableColumn<>("Author");
        bookAuthorColumn.setCellValueFactory(cellData -> cellData.getValue().authorProperty());

        TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
        titleColumn.setCellValueFactory(cellData -> cellData.getValue().titleProperty());

        TableColumn<Book, String> publisherColumn = new TableColumn<>("Publisher");
        publisherColumn.setCellValueFactory(cellData -> cellData.getValue().publisherProperty());

        TableColumn<Book, Integer> publicationYearColumn = new TableColumn<>("Publication Year");
        publicationYearColumn.setCellValueFactory(cellData -> cellData.getValue().publicationYearProperty().asObject());

        TableColumn<Book, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

        TableColumn<Book, String> acquisitionDateColumn = new TableColumn<>("Acquisition Date");
        acquisitionDateColumn.setCellValueFactory(cellData -> cellData.getValue().acquisitionDateAsStringProperty());

        TableColumn<Book, Integer> storageNumberColumn = new TableColumn<>("Storage Number");
        storageNumberColumn.setCellValueFactory(cellData -> cellData.getValue().storageNumberProperty().asObject());

        bookTable.getColumns().addAll(bookNumberColumn, bookCipherColumn, bookAuthorColumn,
                titleColumn, publisherColumn, publicationYearColumn, priceColumn,
                acquisitionDateColumn, storageNumberColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(bookCipherFieldAdd, Validator.createEmptyValidator("Book Cipher is required"));
        validationSupport.registerValidator(authorFieldAdd, Validator.createEmptyValidator("Author is required"));
        validationSupport.registerValidator(titleFieldAdd, Validator.createEmptyValidator("Title is required"));
        validationSupport.registerValidator(publisherFieldAdd, Validator.createEmptyValidator("Publisher is required"));
        validationSupport.registerValidator(publicationYearFieldAdd, Validator.createEmptyValidator("Publication Year is required"));
        validationSupport.registerValidator(priceFieldAdd, Validator.createRegexValidator("Invalid input", "^$|^\\d+(\\.\\d+)?$", Severity.ERROR));
        validationSupport.registerValidator(acquisitionDateFieldAdd, Validator.createRegexValidator("Invalid Date Format", "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
        validationSupport.registerValidator(storageNumberFieldAdd, Validator.createEmptyValidator("Storage Number is required"));
    }

    private void initializeVisibilityBindings() {
        addBookFields.managedProperty().bind(addBookFields.visibleProperty());
        updateBookFields.managedProperty().bind(updateBookFields.visibleProperty());
        deleteBookFields.managedProperty().bind(deleteBookFields.visibleProperty());
    }

    private void initializeComboBox() {
        bookNumbers.addAll(bookService.getAllBookNumbers());
        bookNumberComboBox.setItems(bookNumbers);
    }

    @FXML
    private void handleAddBook(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Book newBook = createBookFromFields(bookCipherFieldAdd, authorFieldAdd, titleFieldAdd,
                publisherFieldAdd, publicationYearFieldAdd, priceFieldAdd, acquisitionDateFieldAdd,
                storageNumberFieldAdd);

        bookService.addBook(newBook);
        refreshTable();
    }

    @FXML
    private void handleUpdateBook(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedBookNumber = bookNumberComboBox.getValue();
        Book selectedBook = bookService.getBookByNumber(selectedBookNumber).orElseThrow(() -> new NoSuchElementException("Book is not present"));

        updateBookFromFields(selectedBook, bookCipherFieldUpdate, authorFieldUpdate, titleFieldUpdate,
                publisherFieldUpdate, publicationYearFieldUpdate, priceFieldUpdate,
                acquisitionDateFieldUpdate, storageNumberFieldUpdate);

        bookService.updateBook(selectedBook);
        refreshTable();
    }

    private void updateBookFromFields(Book book, TextField... fields) {
        book.setBookCipher(fields[0].getText());
        book.setAuthor(fields[1].getText());
        book.setTitle(fields[2].getText());
        book.setPublisher(fields[3].getText());
        book.setPublicationYear(Integer.parseInt(fields[4].getText()));
        book.setPrice(Double.parseDouble(fields[5].getText()));
        book.setAcquisitionDate(Date.valueOf(LocalDate.parse(fields[6].getText())));
        Storage storage = new Storage();
        storage.setStorageNumber(Integer.parseInt(fields[7].getText()));
        book.setStorageNumber(storage);
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

    private Book createBookFromFields(TextField... fields) {
        Book newBook = new Book();
        newBook.setBookCipher(fields[0].getText());
        newBook.setAuthor(fields[1].getText());
        newBook.setTitle(fields[2].getText());
        newBook.setPublisher(fields[3].getText());
        newBook.setPublicationYear(Integer.parseInt(fields[4].getText()));
        newBook.setPrice(Double.parseDouble(fields[5].getText()));
        newBook.setAcquisitionDate(Date.valueOf(LocalDate.parse(fields[6].getText())));
        Storage storage = new Storage();
        storage.setStorageNumber(Integer.parseInt(fields[7].getText()));
        newBook.setStorageNumber(storage);
        return newBook;
    }

    @FXML
    private void handleSelectBook(ActionEvent event) {

        try {
            Integer selectedBookNumber = bookNumberComboBox.getValue();
            Book selectedBook = bookService.getBookByNumber(selectedBookNumber).orElseThrow(() -> new NoSuchElementException("Book is not present"));

            bookCipherFieldUpdate.setText(selectedBook.getBookCipher());
            authorFieldUpdate.setText(selectedBook.getAuthor());
            titleFieldUpdate.setText(selectedBook.getTitle());
            publisherFieldUpdate.setText(selectedBook.getPublisher());
            publicationYearFieldUpdate.setText(String.valueOf(selectedBook.getPublicationYear()));
            priceFieldUpdate.setText(String.valueOf(selectedBook.getPrice()));
            acquisitionDateFieldUpdate.setText(String.valueOf(selectedBook.getAcquisitionDate()));
            storageNumberFieldUpdate.setText(String.valueOf(selectedBook.getStorageNumber()));

            updateBookFields.setVisible(true);
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
            int bookNumberToDelete = Integer.parseInt(deleteBookField.getText());
            Optional<Book> optionalBook = bookService.getBookByNumber(bookNumberToDelete);
            if (optionalBook.isPresent()) {
                Book selectedBook = optionalBook.get();
                bookService.deleteBook(selectedBook.getBookNumber());
                refreshTable();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Book not found for the given book number");
                alert.showAndWait();
            }
        }
        catch (NumberFormatException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText("Invalid Input");
            alert.setContentText("Field is empty");
            alert.showAndWait();

        }


    }

    public void refreshTable() {
        bookTable.getItems().clear();
        bookTable.getItems().addAll(bookService.getAllBooks());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addBookFields.setVisible(true);
        updateBookFields.setVisible(false);
        deleteBookFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addBookFields.setVisible(false);
        updateBookFields.setVisible(true);
        deleteBookFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addBookFields.setVisible(false);
        updateBookFields.setVisible(false);
        deleteBookFields.setVisible(true);
    }

    private void clearUpdateFields() {
        bookCipherFieldUpdate.clear();
        authorFieldUpdate.clear();
        titleFieldUpdate.clear();
        publisherFieldUpdate.clear();
        publicationYearFieldUpdate.clear();
        priceFieldUpdate.clear();
        acquisitionDateFieldUpdate.clear();
        storageNumberFieldUpdate.clear();
    }
}
