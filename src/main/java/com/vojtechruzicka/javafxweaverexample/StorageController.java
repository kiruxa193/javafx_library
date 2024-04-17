package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.service.StorageService;
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

import javax.transaction.Transactional;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@FxmlView("storage-view.fxml")
public class StorageController {
    @FXML
    private TableView<Storage> storageTable;

    @FXML
    private HBox addStorageFields;

    @FXML
    private HBox updateStorageFields;

    @FXML
    private HBox deleteStorageFields;

    @FXML
    private ComboBox<Integer> storageNumberComboBox;

    private final ObservableList<Integer> storageNumbers = FXCollections.observableArrayList();

    @FXML
    private TextField libraryNumberFieldAdd, floorFieldAdd, capacityFieldAdd,
            libraryNumberFieldUpdate, floorFieldUpdate, capacityFieldUpdate,
            deleteStorageField;

    private final StorageService storageService;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService ;
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
        TableColumn<Storage, Integer> storageNumberColumn = new TableColumn<>("Storage Number");
        storageNumberColumn.setCellValueFactory(cellData -> cellData.getValue().storageNumberProperty().asObject());
        TableColumn<Storage, Integer> libraryNumberColumn = new TableColumn<>("library Number");
        libraryNumberColumn.setCellValueFactory(cellData -> cellData.getValue().libraryNumberProperty().asObject());
        TableColumn<Storage, Integer> floorColumn = new TableColumn<>("Floor");
        floorColumn.setCellValueFactory(cellData -> cellData.getValue().floorProperty().asObject());
        TableColumn<Storage, Integer> capacityColumn = new TableColumn<>("Capacity");
        capacityColumn.setCellValueFactory(cellData -> cellData.getValue().capacityProperty().asObject());


        storageTable.getColumns().addAll(storageNumberColumn, libraryNumberColumn,
                floorColumn, capacityColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(libraryNumberFieldAdd, Validator.createEmptyValidator("library Number is required"));
        validationSupport.registerValidator(floorFieldAdd, Validator.createEmptyValidator("floor is required"));
        validationSupport.registerValidator(capacityFieldAdd, Validator.createEmptyValidator("capacity name is required"));

    }

    private void initializeVisibilityBindings() {
        addStorageFields.managedProperty().bind(addStorageFields.visibleProperty());
        updateStorageFields.managedProperty().bind(updateStorageFields.visibleProperty());
        deleteStorageFields.managedProperty().bind(deleteStorageFields.visibleProperty());
    }

    private void initializeComboBox() {
        storageNumbers.addAll(storageService.getAllStorageNumbers());
        storageNumberComboBox.setItems(storageNumbers);
    }

    @FXML
    private void handleAddStorage(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Storage newStorage = createStorageFromFields(libraryNumberFieldAdd, floorFieldAdd, capacityFieldAdd);

        storageService.addStorage(newStorage);
        refreshTable();
    }

    @FXML
    private void handleUpdateStorage(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedStorageNumber = storageNumberComboBox.getValue();
        Storage selectedStorage= storageService.getStoragesByStorageNumbers(selectedStorageNumber).
                orElseThrow(() -> new NoSuchElementException("Storage is not present"));

        updateStorageFromFields(selectedStorage, libraryNumberFieldUpdate,
                floorFieldUpdate, capacityFieldUpdate);

        storageService.updateStorage(selectedStorage);
        refreshTable();
    }

    private void updateStorageFromFields(Storage storage, TextField... fields) {
        Library library = new Library();
        library.setLibraryNumber(Integer.parseInt(fields[0].getText()));
        storage.setLibraryNumber(library);
        storage.setFloor(Integer.parseInt(fields[1].getText()));
        storage.setCapacity(Integer.parseInt(fields[2].getText()));
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

    private Storage createStorageFromFields(TextField... fields) {
        Storage newStorage = new Storage();
        Library library = new Library();
        library.setLibraryNumber(Integer.parseInt(fields[0].getText()));
        newStorage.setLibraryNumber(library);
        newStorage.setFloor(Integer.parseInt(fields[1].getText()));
        newStorage.setCapacity(Integer.parseInt(fields[2].getText()));
        return newStorage;
    }

    @FXML
    private void handleSelectStorage(ActionEvent event) {
        try {


            Integer selectedStorageNumber = storageNumberComboBox.getValue();
            Storage selectedStorage = storageService.getStoragesByStorageNumbers(selectedStorageNumber)
                    .orElseThrow(() -> new NoSuchElementException("Storage is not present"));

            libraryNumberFieldUpdate.setText(String.valueOf(selectedStorage.getLibraryNumber()));
            floorFieldUpdate.setText(String.valueOf(selectedStorage.getFloor()));
            capacityFieldUpdate.setText(String.valueOf(selectedStorage.getCapacity()));


            updateStorageFields.setVisible(true);
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
    private void handleDeleteStorage(ActionEvent event) {

        try {

            int storageNumberToDelete = Integer.parseInt(deleteStorageField.getText());
            Optional<Storage> optionalStorage = storageService.getStoragesByStorageNumbers(
                    storageNumberToDelete);

            if (optionalStorage.isPresent()) {
                Storage selectedStorages = optionalStorage.get();
                storageService.deleteStorage(selectedStorages.getStorageNumber());
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Storage not found for the given book number");
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
        storageTable.getItems().clear();
        storageTable.getItems().addAll(storageService.getAllStorages());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addStorageFields.setVisible(true);
        updateStorageFields.setVisible(false);
        deleteStorageFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addStorageFields.setVisible(false);
        updateStorageFields.setVisible(true);
        deleteStorageFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addStorageFields.setVisible(false);
        updateStorageFields.setVisible(false);
        deleteStorageFields.setVisible(true);
    }

    private void clearUpdateFields() {
        libraryNumberFieldUpdate.clear();
        floorFieldUpdate.clear();
        capacityFieldUpdate.clear();

    }
}
