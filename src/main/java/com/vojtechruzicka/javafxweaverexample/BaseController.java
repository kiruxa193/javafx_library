package com.vojtechruzicka.javafxweaverexample;

import javafx.application.Platform;
import javafx.stage.Stage;

public abstract class BaseController {

    protected Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void closeCurrentStage() {
        if (stage != null) {
            Platform.runLater(() -> {
                stage.close();
                stage = null;
            });
        }
    }
}
