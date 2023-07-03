package com.example.practica;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class FinalPageController {

    @FXML
    private Label informationLabel;

    @FXML
    private Button okButton;

    @FXML
    void initialize() {
        informationLabel.setText(MainPageController.informationString);

        okButton.setOnAction(ActionEvent -> {
            okButton.getScene().getWindow().hide();
        });
    }

}
