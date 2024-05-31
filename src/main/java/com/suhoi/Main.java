package com.suhoi;

import com.suhoi.listener.ArduinoListener;
import com.suhoi.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static ArduinoListener arduinoListener;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        arduinoListener = new ArduinoListener();
        Thread thread = new Thread(arduinoListener);
        thread.setDaemon(true);
        thread.start();
        ViewFactory.primaryStage = primaryStage;
        ViewFactory.getAuthView();
    }
}