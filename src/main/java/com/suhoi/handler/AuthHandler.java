package com.suhoi.handler;

import com.suhoi.Main;
import com.suhoi.dto.AuthDTO;
import com.suhoi.listener.ArduinoListener;
import com.suhoi.util.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.Setter;

public class AuthHandler {

    @FXML
    private TextField passwordField;

    @FXML
    private Button authButton;

    @Setter
    @FXML
    private Label rfidReaderLabel;

    @Setter
    private String cardUID;

    @FXML
    public void initialize() {
        ArduinoListener listener = Main.arduinoListener;
        listener.messageProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                rfidReaderLabel.setText("Карта вставлена");
                rfidReaderLabel.setTextFill(Color.GREEN);
                cardUID = newValue;
            });
        });
    }

    @FXML
    public void handleAuthButton() {
        if (passwordField == null) {
            Alerts.showErrorAlert("Пароль не введен");
        } else if (cardUID == null) {
            Alerts.showErrorAlert("Карта не вставлена");
        } else {
            String password = passwordField.getText();
            AuthDTO authDTO = AuthDTO.builder()
                    .password(password)
                    .cardUID(cardUID)
                    .build();
            Alerts.showInfoAlert("Worked! " + authDTO.getCardUID() + " " + authDTO.getPassword());
            System.out.println("все заебись, authDTO: " + authDTO);
        }

    }
}
