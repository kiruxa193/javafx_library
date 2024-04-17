package com.vojtechruzicka.javafxweaverexample;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
@FxmlView("main-view.fxml")
public class MainMenuController {

    private final FxWeaver fxWeaver;

    @Autowired
    public MainMenuController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @FXML
    private void openBooksView() {
        openEntityView(BookController.class);
    }

    @FXML
    private void openBookLoanView() {
        openEntityView(BookLoanController.class);
    }

    @FXML
    private void openEmployeesView() {
        openEntityView(EmployeeController.class);
    }

    @FXML
    private void openLibrariesView() {
        openEntityView(LibraryController.class);
    }

    @FXML
    private void openStorageView() {
        openEntityView(StorageController.class);
    }

    @FXML
    private void openSubscriberView() {
        openEntityView(SubscriberController.class);
    }

    private void openEntityView(Class<?> controllerClass) {

        Parent root = fxWeaver.loadView(controllerClass);
        Scene scene = new Scene(root);

        Stage newStage = new Stage();
        newStage.setScene(scene);

        // Отображаем новое окно
        newStage.show();
    }
}
