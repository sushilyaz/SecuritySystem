package com.suhoi.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.suhoi.util.SceneUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ArduinoListener extends Task<Void> {

    private final Stage primaryStage;


    @Override
    protected Void call() throws Exception {
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
                    System.out.println("hui");
                    // Проверяем пришедшее сообщение
                    if (message.trim().length() == 26) {
                        // Если пришло сообщение в виде 26-значного номера, выполняем одно действие
                        System.out.println("Received number: " + message.trim());
                        updateMessage(message);
                    } else if (message.trim().equalsIgnoreCase("stop")) {
                        // Если пришло сообщение "stop", выполняем другое действие
                        System.out.println("Received 'stop' command.");
                        Platform.runLater(() -> {
                            SceneUtil.openScene("/authForm.fxml", primaryStage);
                        });
                    }
                }
                Thread.sleep(1000); // Пауза перед следующим чтением
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
