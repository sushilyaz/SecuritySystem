package com.suhoi;

import com.fazecast.jSerialComm.SerialPort;
import com.suhoi.listener.ArduinoListener;
import com.suhoi.util.FileAccessControl;
import com.suhoi.view.ViewFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

public class Main extends Application {

    public static ArduinoListener arduinoListener;
    private static final String ARDUINO_ID = "07042000";
    private Stage primaryStage;

    public static String PORT = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws InterruptedException {
        boolean accessRestricted = FileAccessControl.blockAccess();
        if (!accessRestricted) {
            System.out.println("Failed to restrict access to files.");
            System.exit(1);
        }
        this.primaryStage = primaryStage;
        Stage stage = new Stage();

        ObservableList<String> portNames = FXCollections.observableArrayList();
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            portNames.add(port.getSystemPortName());
        }
        ListView<String> portListView = new ListView<>(portNames);

        VBox vbox = new VBox(portListView);
        stage.setScene(new Scene(vbox, 300, 200));
        stage.show();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (SerialPort port : ports) {
                    System.out.println("Пробуем подключиться к порту: " + port.getSystemPortName());

                    port.setBaudRate(9600);
                    port.setNumDataBits(8);
                    port.setNumStopBits(SerialPort.ONE_STOP_BIT);
                    port.setParity(SerialPort.NO_PARITY);

                    if (port.openPort()) {
                        System.out.println("Порт открыт: " + port.getSystemPortName());

                        try {
                            // Ждем стабилизации соединения
                            Thread.sleep(2000);

                            OutputStream out = port.getOutputStream();
                            out.write("07042000\n".getBytes()); // Отправляем строку
                            out.flush();

                            Thread.sleep(1000);

                            Scanner data = new Scanner(port.getInputStream());
                            long startTime = System.currentTimeMillis();

                            while (System.currentTimeMillis() - startTime < 5000) { // Ожидаем 5 секунд
                                if (data.hasNextLine()) {
                                    String response = data.nextLine();
                                    if (response.contains("Arduino Connected")) {
                                        System.out.println("Найдено Arduino на порту: " + port.getSystemPortName());
                                        PORT = port.getSystemPortName();
                                        port.closePort();
                                        Thread.sleep(1000);
//                                        stage.close();
                                        Platform.runLater(() -> runMainApp()); // Запускаем основное приложение на JavaFX потоке
                                        return null;
                                    }
                                }
                            }
                        } catch (InterruptedException | IOException e) {
                            e.printStackTrace();
                        } finally {
                            port.closePort();
                        }
                    } else {
                        System.out.println("Не удалось открыть порт: " + port.getSystemPortName());
                    }
                }
                return null;
            }
        };

        // Запускаем задачу в фоновом потоке
        Thread comPortCheck = new Thread(task);
        comPortCheck.setDaemon(true);
        comPortCheck.start();
    }

    private void runMainApp() {
        arduinoListener = new ArduinoListener(PORT);
        Thread thread = new Thread(arduinoListener);
        thread.setDaemon(true);
        thread.start();
        try {
            thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        primaryStage.setOnCloseRequest(event -> {
            // Восстанавливаем доступ ко всем файлам при закрытии приложения
            boolean accessRestored = FileAccessControl.allowAccess();
            if (!accessRestored) {
                System.err.println("Failed to restore access to files.");
            }
        });
        ViewFactory.primaryStage = primaryStage;
        ViewFactory.getAuthView();
    }
}


