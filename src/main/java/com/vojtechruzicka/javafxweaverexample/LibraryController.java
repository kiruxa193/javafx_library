package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.service.LibraryService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxmlView;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@FxmlView("library-view.fxml")
public class LibraryController {
    @FXML
    private TableView<Library> libraryTable;

    @FXML
    private HBox addLibraryFields;

    @FXML
    private HBox updateLibraryFields;

    @FXML
    private HBox deleteLibraryFields;

    @FXML
    private ComboBox<Integer> libraryNumberComboBox;

    private final ObservableList<Integer> libraryNumbers = FXCollections.observableArrayList();

    @FXML
    private TextField libraryNameFieldAdd, addressFieldAdd, bookCountFieldAdd,
            libraryNameFieldUpdate, addressFieldUpdate, bookCountFieldUpdate,
            deleteLibraryField;

    private final LibraryService libraryService;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService ;
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
        TableColumn<Library, Integer> libraryNumberColumn = new TableColumn<>("Library Number");
        libraryNumberColumn.setCellValueFactory(cellData -> cellData.getValue().libraryNumberProperty().asObject());

        TableColumn<Library, String> libraryNameColumn = new TableColumn<>("Library Name");
        libraryNameColumn.setCellValueFactory(cellData -> cellData.getValue().libraryNameProperty());

        TableColumn<Library, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());

        TableColumn<Library, Integer> bookCountColumn = new TableColumn<>("Book Count");
        bookCountColumn.setCellValueFactory(cellData -> cellData.getValue().bookCountProperty().asObject());

        libraryTable.getColumns().addAll(libraryNumberColumn, libraryNameColumn,
                addressColumn, bookCountColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(libraryNameFieldAdd, Validator.createEmptyValidator("library name is required"));
        validationSupport.registerValidator(addressFieldAdd, Validator.createEmptyValidator("adress is required"));
        validationSupport.registerValidator(bookCountFieldAdd, Validator.createEmptyValidator("book count is required"));
    }

    private void initializeVisibilityBindings() {
        addLibraryFields.managedProperty().bind(addLibraryFields.visibleProperty());
        updateLibraryFields.managedProperty().bind(updateLibraryFields.visibleProperty());
        deleteLibraryFields.managedProperty().bind(deleteLibraryFields.visibleProperty());
    }

    private void initializeComboBox() {
        libraryNumbers.addAll(libraryService.getAllLibraryNumbers());
        libraryNumberComboBox.setItems(libraryNumbers);
    }

    @FXML
    private void handleAddLibrary(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Library newLibrary = createLibraryFromFields(libraryNameFieldAdd, addressFieldAdd, bookCountFieldAdd);

        libraryService.addLibrary(newLibrary);
        refreshTable();
    }

    @FXML
    private void handleUpdateLibrary(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedLibraryNumber = libraryNumberComboBox.getValue();
        Library selectedLibrary= libraryService.getLibrariesByLibraryNumbers(
                selectedLibraryNumber).orElseThrow(() -> new NoSuchElementException(
                        "Library is not present"));

        updateLibraryFromFields(selectedLibrary, libraryNameFieldUpdate, addressFieldUpdate,
                bookCountFieldUpdate);

        libraryService.updateLibrary(selectedLibrary);
        refreshTable();
    }

    private void updateLibraryFromFields(Library library, TextField... fields) {
        library.setLibraryName(fields[0].getText());
        library.setAddress(fields[1].getText());
        library.setBookCount(Integer.parseInt(fields[2].getText()));

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

    private Library createLibraryFromFields(TextField... fields) {
        Library newLibrary = new Library();
        newLibrary.setLibraryName(fields[0].getText());
        newLibrary.setAddress(fields[1].getText());
        newLibrary.setBookCount(Integer.parseInt(fields[2].getText()));
        return newLibrary;
    }

    @FXML
    private void handleSelectLibrary(ActionEvent event) {

        try {

            Integer selectedLibraryNumber = libraryNumberComboBox.getValue();
            Library selectedLibrary = libraryService.getLibrariesByLibraryNumbers(
                    selectedLibraryNumber).orElseThrow(()
                    -> new NoSuchElementException("Library is not present"));

            libraryNameFieldUpdate.setText(selectedLibrary.getLibraryName());
            addressFieldUpdate.setText(selectedLibrary.getAddress());
            bookCountFieldUpdate.setText(String.valueOf(selectedLibrary.getBookCount()));

            updateLibraryFields.setVisible(true);
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
    private void handleDeleteLibrary(ActionEvent event) {

        try {


            int libraryNumberToDelete = Integer.parseInt(deleteLibraryField.getText());
            Optional<Library> optionalLibrary = libraryService.getLibrariesByLibraryNumbers
                    (libraryNumberToDelete);

            if (optionalLibrary.isPresent()) {
                Library selectedLibrary = optionalLibrary.get();
                libraryService.deleteLibrary(selectedLibrary.getLibraryNumber());
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Library not found for the given book number");
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
        libraryTable.getItems().clear();
        libraryTable.getItems().addAll(libraryService.getAllLibraries());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addLibraryFields.setVisible(true);
        updateLibraryFields.setVisible(false);
        deleteLibraryFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addLibraryFields.setVisible(false);
        updateLibraryFields.setVisible(true);
        deleteLibraryFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addLibraryFields.setVisible(false);
        updateLibraryFields.setVisible(false);
        deleteLibraryFields.setVisible(true);
    }

    private void clearUpdateFields() {
        libraryNameFieldUpdate.clear();
        addressFieldUpdate.clear();
        bookCountFieldUpdate.clear();
    }
}
