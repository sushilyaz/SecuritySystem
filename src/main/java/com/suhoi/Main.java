package com.suhoi;

import com.fazecast.jSerialComm.SerialPort;
import com.suhoi.handler.AuthHandler;
import com.suhoi.handler.WindowHandler;
import com.suhoi.util.SceneUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        listenerThread();
        SceneUtil.openScene("/authForm.fxml");
    }

    private void listenerThread() {
        Thread listener = new Thread(() -> {
            SerialPort serialPort = SerialPort.getCommPort("COM3");
            serialPort.setComPortParameters(9600, 8, 1, 0);
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
            serialPort.openPort();
            System.out.println("serial port opened");
            try {
                while (true) {
                    if (serialPort.bytesAvailable() > 0) {
                        byte[] buffer = new byte[serialPort.bytesAvailable()];
                        serialPort.readBytes(buffer, buffer.length);
                        String message = new String(buffer);
                        // Проверяем пришедшее сообщение
                        if (message.trim().length() == 26) {
                            // Если пришло сообщение в виде 26-значного номера, выполняем одно действие
                            System.out.println("Received number: " + message.trim());
                            Label label = new Label("Карта прочитана");
                            label.setTextFill(Color.GREEN);
                            AuthHandler authHandler = AuthHandler.getInstance();
                            authHandler.setRfidReaderLabel(label);
                            authHandler.setCardUID(message);

                        } else if (message.trim().equalsIgnoreCase("stop")) {
                            // Если пришло сообщение "stop", выполняем другое действие
                            System.out.println("Received 'stop' command.");
                            Platform.runLater(() -> SceneUtil.openScene("/authForm.fxml"));
                        }
                    }
                    Thread.sleep(1000); // Пауза перед следующим чтением
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        listener.setDaemon(true);
        listener.start();
    }
}