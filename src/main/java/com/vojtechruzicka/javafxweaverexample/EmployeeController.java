package com.vojtechruzicka.javafxweaverexample;

import com.vojtechruzicka.javafxweaverexample.model.Employee;
import com.vojtechruzicka.javafxweaverexample.model.Library;
import com.vojtechruzicka.javafxweaverexample.service.EmployeeService;
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
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

@Controller
@FxmlView("employee-view.fxml")
public class EmployeeController {

    @FXML
    private TableView<Employee> employeeTable;

    @FXML
    private HBox addEmployeeFields;

    @FXML
    private HBox updateEmployeeFields;

    @FXML
    private HBox deleteEmployeeFields;

    @FXML
    private ComboBox<Integer> employeeIdComboBox;

    private final ObservableList<Integer> employeeIds = FXCollections.observableArrayList();

    @FXML
    private TextField libraryNumberFieldAdd, lastNameFieldAdd, firstNameFieldAdd, middleNameFieldAdd, birthDateFieldAdd,
            positionFieldAdd, educationFieldAdd,
            libraryNumberFieldUpdate, lastNameFieldUpdate, firstNameFieldUpdate, middleNameFieldUpdate, birthDateFieldUpdate,
            positionFieldUpdate, educationFieldUpdate,
            deleteEmployeeField;

    private final EmployeeService employeeService;
    private final ValidationSupport validationSupport = new ValidationSupport();

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService ;
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
        TableColumn<Employee, Integer> employeeIdColumn = new TableColumn<>("Employee Id");
        employeeIdColumn.setCellValueFactory(cellData -> cellData.getValue().employeeIdProperty().asObject());

        TableColumn<Employee, Integer> libraryNumberColumn = new TableColumn<>("Library Number");
        libraryNumberColumn.setCellValueFactory(cellData -> cellData.getValue().libraryNumberProperty().asObject());

        TableColumn<Employee, String> lastNameColumn = new TableColumn<>("Last Name");
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        TableColumn<Employee, String> firstNameColumn = new TableColumn<>("First Name");
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());

        TableColumn<Employee, String> middleNameColumn = new TableColumn<>("Middle Name");
        middleNameColumn.setCellValueFactory(cellData -> cellData.getValue().middleNameProperty());

        TableColumn<Employee, String> birthDateColumn = new TableColumn<>("BirthDate");
        birthDateColumn.setCellValueFactory(cellData -> cellData.getValue().birthDateAsStringProperty());

        TableColumn<Employee, String> positionColumn = new TableColumn<>("Position");
        positionColumn.setCellValueFactory(cellData -> cellData.getValue().positionProperty());

        TableColumn<Employee, String> educationColumn = new TableColumn<>("Education");
        educationColumn.setCellValueFactory(cellData -> cellData.getValue().educationProperty());

        employeeTable.getColumns().addAll(employeeIdColumn, libraryNumberColumn, lastNameColumn, firstNameColumn,
                middleNameColumn, birthDateColumn, positionColumn, educationColumn);
    }

    private void initializeValidation() {
        validationSupport.registerValidator(libraryNumberFieldAdd, Validator.createEmptyValidator("Library Number is required"));
        validationSupport.registerValidator(lastNameFieldAdd, Validator.createEmptyValidator("Last name is required"));
        validationSupport.registerValidator(firstNameFieldAdd, Validator.createEmptyValidator("First name is required"));
        validationSupport.registerValidator(middleNameFieldAdd, Validator.createEmptyValidator("Middle name is required"));
        validationSupport.registerValidator(birthDateFieldAdd, Validator.createRegexValidator("Invalid Date Format", "^$|^\\d{4}-\\d{2}-\\d{2}$", Severity.ERROR));
        validationSupport.registerValidator(positionFieldAdd, Validator.createEmptyValidator("Position is required"));
        validationSupport.registerValidator(educationFieldAdd, Validator.createEmptyValidator("Education is required"));
    }

    private void initializeVisibilityBindings() {
        addEmployeeFields.managedProperty().bind(addEmployeeFields.visibleProperty());
        updateEmployeeFields.managedProperty().bind(updateEmployeeFields.visibleProperty());
        deleteEmployeeFields.managedProperty().bind(deleteEmployeeFields.visibleProperty());
    }

    private void initializeComboBox() {
        employeeIds.addAll(employeeService.getAllEmployeeIds());
        employeeIdComboBox.setItems(employeeIds);
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Employee newEmployee = createEmployeeFromFields(libraryNumberFieldAdd, lastNameFieldAdd, firstNameFieldAdd, middleNameFieldAdd, birthDateFieldAdd,
                positionFieldAdd, educationFieldAdd);

        employeeService.addEmployee(newEmployee);
        refreshTable();
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent event) {
        if (validationSupport.isInvalid()) {
            showValidationErrorDialog();
            return;
        }

        Integer selectedEmployeeId = employeeIdComboBox.getValue();
        Employee selectedEmployee= employeeService.getEmployeesByEmployeeId(selectedEmployeeId).orElseThrow(() -> new NoSuchElementException("Book is not present"));

        updateEmployeeFromFields(selectedEmployee, libraryNumberFieldUpdate,  lastNameFieldUpdate, firstNameFieldUpdate, middleNameFieldUpdate, birthDateFieldUpdate,
                positionFieldUpdate, educationFieldUpdate);

        employeeService.updateEmployee(selectedEmployee);
        refreshTable();
    }

    private void updateEmployeeFromFields(Employee employee, TextField... fields) {
        Library library = new Library();
        library.setLibraryNumber(Integer.parseInt(fields[0].getText()));
        employee.setLibraryNumberEmpl(library);
        employee.setLastName(fields[1].getText());
        employee.setFirstName(fields[2].getText());
        employee.setMiddleName(fields[3].getText());
        employee.setBirthDate(Date.valueOf(LocalDate.parse(fields[4].getText())));
        employee.setPosition(fields[5].getText());
        employee.setEducation(fields[6].getText());
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

    private Employee createEmployeeFromFields(TextField... fields) {
        Employee newEmployee = new Employee();
        Library library = new Library();
        library.setLibraryNumber(Integer.parseInt(fields[0].getText()));
        newEmployee.setLibraryNumberEmpl(library);
        newEmployee.setLastName(fields[1].getText());
        newEmployee.setFirstName(fields[2].getText());
        newEmployee.setMiddleName(fields[3].getText());
        newEmployee.setBirthDate(Date.valueOf(LocalDate.parse(fields[4].getText())));
        newEmployee.setPosition(fields[5].getText());
        newEmployee.setEducation(fields[6].getText());
        return newEmployee;
    }

    @FXML
    private void handleSelectEmployee(ActionEvent event) {

        try {

            Integer selectedEmployeeId = employeeIdComboBox.getValue();
            Employee selectedEmployee = employeeService.getEmployeesByEmployeeId(selectedEmployeeId).orElseThrow(() -> new NoSuchElementException("Employee is not present"));
            libraryNumberFieldUpdate.setText(String.valueOf(selectedEmployee.getLibraryNumberEmpl()));
            lastNameFieldUpdate.setText(selectedEmployee.getLastName());
            firstNameFieldUpdate.setText(selectedEmployee.getFirstName());
            middleNameFieldUpdate.setText(selectedEmployee.getMiddleName());
            birthDateFieldUpdate.setText(String.valueOf(selectedEmployee.getBirthDate()));
            positionFieldUpdate.setText(selectedEmployee.getPosition());
            educationFieldUpdate.setText(selectedEmployee.getEducation());

            updateEmployeeFields.setVisible(true);
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
    private void handleDeleteEmployee(ActionEvent event) {


        try {
            int employeeIdToDelete = Integer.parseInt(deleteEmployeeField.getText());
            Optional<Employee> optionalEmployee = employeeService.getEmployeesByEmployeeId(employeeIdToDelete);

            if (optionalEmployee.isPresent()) {
                Employee selectedEmployee = optionalEmployee.get();
                employeeService.deleteEmployee(selectedEmployee.getEmployeeId());
                refreshTable();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Input Error");
                alert.setHeaderText("Invalid Input");
                alert.setContentText("Employee not found for the given book number");
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
        employeeTable.getItems().clear();
        employeeTable.getItems().addAll(employeeService.getAllEmployees());
    }

    @FXML
    private void selectAddOperation(ActionEvent event) {
        addEmployeeFields.setVisible(true);
        updateEmployeeFields.setVisible(false);
        deleteEmployeeFields.setVisible(false);
    }

    @FXML
    private void selectUpdateOperation(ActionEvent event) {
        addEmployeeFields.setVisible(false);
        updateEmployeeFields.setVisible(true);
        deleteEmployeeFields.setVisible(false);
        clearUpdateFields();
    }

    @FXML
    private void selectDeleteOperation(ActionEvent event) {
        addEmployeeFields.setVisible(false);
        updateEmployeeFields.setVisible(false);
        deleteEmployeeFields.setVisible(true);
    }

    private void clearUpdateFields() {
        libraryNumberFieldUpdate.clear();
        lastNameFieldUpdate.clear();
        firstNameFieldUpdate.clear();
        middleNameFieldUpdate.clear();
        birthDateFieldUpdate.clear();
        positionFieldUpdate.clear();
        educationFieldUpdate.clear();
    }
}
