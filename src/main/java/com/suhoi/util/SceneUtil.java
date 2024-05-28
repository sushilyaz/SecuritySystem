package com.suhoi.util;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SceneUtil {
    public static void openScene(String resource, Stage stage) {
        Parent content = null;
        try {
            content = FXMLLoader.load(Objects.requireNonNull(SceneUtil.class.getResource(resource)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(content);
        stage.setScene(scene);
        stage.show();
    }
}
