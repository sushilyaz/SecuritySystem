package com.suhoi.controller;

import com.suhoi.Main;
import com.suhoi.dto.UserCreateDto;
import com.suhoi.listener.ArduinoListener;
import com.suhoi.service.UserService;
import com.suhoi.util.Alerts;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import lombok.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateUserController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField pathField;

    private String cardUID;

    @Setter
    private UserService userService;
    @FXML
    private Label cardPromptLabel;
    @FXML
    private Label cardStatusLabel;
    @FXML
    private Label usernameError;
    @FXML
    private Label passwordError;
    @FXML
    private Label pathError;

    @Setter
    @Getter
    private Stage stage;
    private boolean isCardRead = false;

    @FXML
    public void initialize() {
        ArduinoListener listener = Main.arduinoListener;
        listener.messageProperty().addListener((observable, oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (!Objects.equals(newValue, "stop")) {
                    cardUID = newValue;
                    isCardRead = true;
                    cardStatusLabel.setVisible(true);
                    cardPromptLabel.setVisible(false);
                }

            });
        });
    }

    @FXML
    private void handleBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null) {
            pathField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleFinish() {
        boolean isValid = validateFields();

        if (isValid && isCardRead) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String path = pathField.getText();

            List<String> paths = Arrays.stream(path.split(";"))
                    .map(String::trim)
                    .filter(p -> !p.isEmpty())
                    .collect(Collectors.toList());

            UserCreateDto dto = UserCreateDto.builder()
                    .username(username)
                    .password(password)
                    .path(paths)
                    .cardUid(cardUID)
                    .build();
            userService.save(dto);
        } else if (!isCardRead) {
            Alerts.showErrorAlert("Карта не вставлена", getStage());
        } else {
            Alerts.showErrorAlert("Заполните все поля", getStage());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (usernameField.getText() == null || usernameField.getText().isEmpty()) {
            usernameError.setVisible(true);
            isValid = false;
        } else {
            usernameError.setVisible(false);
        }

        if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
            passwordError.setVisible(true);
            isValid = false;
        } else {
            passwordError.setVisible(false);
        }

        if (pathField.getText() == null || pathField.getText().isEmpty()) {
            pathError.setVisible(true);
            isValid = false;
        } else {
            pathError.setVisible(false);
        }

        return isValid;
    }
}

