package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Storage;
import com.vojtechruzicka.javafxweaverexample.model.Subscriber;
import com.vojtechruzicka.javafxweaverexample.service.SubscriberService;
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
@FxmlView("subscriber-view.fxml")
public class SubscriberController {

    @FXML
    private TableView<Subscriber> subscriberTable;

    @FXML
    private HBox addSubscriberFields;

    @FXML
    private HBox updateSubscriberFields;

    @FXML
    private HBox deleteSubscriberFields;

    @FXML
    private ComboBox<Integer> ticketNumberComboBox;

    private final ObservableList<Integer> subscriberNumbers = FXCollections.observableArrayList();

    @FXML
    private TextField lastNameFieldAdd, firstNameFieldAdd, middleNameFieldAdd,
            addressFieldAdd, phoneNumberFieldAdd,
            lastNameFieldUpdate, firstNameFieldUpdate, middleNameFieldUpdate,
            addressFieldUpdate, phoneNumberFieldUpdate,
            deleteSubscriberField;

    private final SubscriberService subscriberService;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService ;
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
        TableColumn<Subscriber, Integer> ticketNumberColumn = new TableColumn<>("Ticket Number");
        ticketNumberColumn.setCellValueFactory(cellData -> cellData.getValue().ticketNumberProperty().asObject());
        TableColumn<Subscriber, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        TableColumn<Subscriber, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        TableColumn<Subscriber, String> middleNameColumn = new TableColumn<>("Middle Name");
        middleNameColumn.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());
        TableColumn<Subscriber, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> cellData.getValue().addressProperty());
        TableColumn<Subscriber, String> phoneNumberColumn = new TableColumn<>("Phone Number");
        phoneNumberColumn.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());

        subscriberTable.getColumns().addAll(ticketNumberColumn, lastNameColumn, firstNameColumn,
                middleNameColumn, addressColumn, phoneNumberColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(lastNameFieldAdd, Validator.createEmptyValidator("library Number is required"));
        validationSupport.registerValidator(firstNameFieldAdd, Validator.createEmptyValidator("floor is required"));
        validationSupport.registerValidator(middleNameFieldAdd, Validator.createEmptyValidator("capacity name is required"));
        //TODO Дописать
    }

    private void initializeVisibilityBindings() {
        addSubscriberFields.managedProperty().bind(addSubscriberFields.visibleProperty());
        updateSubscriberFields.managedProperty().bind(updateSubscriberFields.visibleProperty());
        deleteSubscriberFields.managedProperty().bind(deleteSubscriberFields.visibleProperty());
    }

    private void initializeComboBox() {
        subscriberNumbers.addAll(subscriberService.getAllSubscriberNumbers());
        ticketNumberComboBox.setItems(subscriberNumbers);
    }

    @FXML
    private void handleAddSubscriber(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Subscriber newSub = createSubscriberFromFields(lastNameFieldAdd, firstNameFieldAdd,
                middleNameFieldAdd, addressFieldAdd, phoneNumberFieldAdd);

        subscriberService.addSubscriber(newSub);
        refreshTable();
    }

    @FXML
    private void handleUpdateSubscriber(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedTicketNumber = ticketNumberComboBox.getValue();
        Subscriber selectedSubscriber= subscriberService.getSubscribersByTicketNumbers(
                selectedTicketNumber).
                orElseThrow(() -> new NoSuchElementException("Sub is not present"));

        updateSubscriberFromFields(selectedSubscriber, lastNameFieldUpdate,
                firstNameFieldUpdate, middleNameFieldUpdate, addressFieldUpdate,
                phoneNumberFieldUpdate);

        subscriberService.updateSubscriber(selectedSubscriber);
        refreshTable();
    }

    private void updateSubscriberFromFields(Subscriber subscriber, TextField... fields) {
        subscriber.setLastName(fields[0].getText());
        subscriber.setFirstName(fields[1].getText());
        subscriber.setMiddleName(fields[2].getText());
        subscriber.setAddress(fields[3].getText());
        subscriber.setPhoneNumber(fields[4].getText());
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

    private Subscriber createSubscriberFromFields(TextField... fields) {
        Subscriber newSub = new Subscriber();
        newSub.setLastName(fields[0].getText());
        newSub.setFirstName(fields[1].getText());
        newSub.setMiddleName(fields[2].getText());
        newSub.setAddress(fields[3].getText());
        newSub.setPhoneNumber(fields[4].getText());
        return newSub;
    }

    @FXML
    private void handleSelectSubscriber(ActionEvent event) {

        try {

            Integer selectedTicketNumber = ticketNumberComboBox.getValue();
            Subscriber selectedSub = subscriberService.getSubscribersByTicketNumbers(
                            selectedTicketNumber)
                    .orElseThrow(() -> new NoSuchElementException("Sub is not present"));

            lastNameFieldUpdate.setText(selectedSub.getLastName());
            firstNameFieldUpdate.setText(selectedSub.getFirstName());
            middleNameFieldUpdate.setText(selectedSub.getMiddleName());
            addressFieldUpdate.setText(selectedSub.getAddress());
            phoneNumberFieldUpdate.setText(selectedSub.getPhoneNumber());


            updateSubscriberFields.setVisible(true);
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
    private void handleDeleteSubscriber(ActionEvent event) {

        try {


            int ticketNumberToDelete = Integer.parseInt(deleteSubscriberField.getText());
            Optional<Subscriber> optionalSub = subscriberService.getSubscribersByTicketNumbers(
                    ticketNumberToDelete);

            if (optionalSub.isPresent()) {
                Subscriber selectedSub = optionalSub.get();
                subscriberService.deleteSubscriber(selectedSub.getTicketNumber());
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Sub not found for the given book number");
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
        subscriberTable.getItems().clear();
        subscriberTable.getItems().addAll(subscriberService.getAllSubscribers());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addSubscriberFields.setVisible(true);
        updateSubscriberFields.setVisible(false);
        deleteSubscriberFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addSubscriberFields.setVisible(false);
        updateSubscriberFields.setVisible(true);
        deleteSubscriberFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addSubscriberFields.setVisible(false);
        updateSubscriberFields.setVisible(false);
        deleteSubscriberFields.setVisible(true);
    }

    private void clearUpdateFields() {
        lastNameFieldUpdate.clear();
        firstNameFieldUpdate.clear();
        middleNameFieldUpdate.clear();
        addressFieldUpdate.clear();
        phoneNumberFieldUpdate.clear();

    }
}
