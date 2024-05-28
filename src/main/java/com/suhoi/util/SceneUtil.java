package com.suhoi.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneUtil {
    public static void openScene(String resource) {
        Stage stage = new Stage();
        Parent content = null;
        try {
            content = FXMLLoader.load(Objects.requireNonNull(SceneUtil.class.getResource(resource)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.setAlwaysOnTop(true);
        stage.show();
    }
}
