package com.suhoi.controller;

import com.suhoi.Main;
import com.suhoi.dto.AuthDto;
import com.suhoi.listener.ArduinoListener;
import com.suhoi.service.UserService;
import com.suhoi.util.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Objects;

public class AuthController {

    @Setter
    private UserService userService;

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
                if (!Objects.equals(newValue, "stop")) {
                    rfidReaderLabel.setText("Карта вставлена");
                    rfidReaderLabel.setTextFill(Color.GREEN);
                    cardUID = newValue;
                } else {
                    rfidReaderLabel.setText("Карта не вставлена");
                    rfidReaderLabel.setTextFill(Color.RED);
                    passwordField.clear();
                    cardUID = null;
                }

            });
        });
    }

    @FXML
    public void handleAuthButton() {
        if (passwordField.getText().isEmpty()) {
            Alerts.showErrorAlert("Пароль не введен");
        } else if (cardUID == null) {
            Alerts.showErrorAlert("Карта не вставлена");
        } else {
            String password = passwordField.getText();
            AuthDto authDTO = AuthDto.builder()
                    .password(password)
                    .cardUID(cardUID)
                    .build();
            userService.auth(authDTO);
        }

    }
}
