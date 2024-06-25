package com.suhoi.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.suhoi.util.UserUtils;
import com.suhoi.view.ViewFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.OutputStream;
import java.io.PrintWriter;

public class ArduinoListener extends Task<Void> {

    private static String SERIAL_PORT = "COM3";

    public ArduinoListener(String serialPort) {
        SERIAL_PORT = serialPort;
    }

    @Override
    protected Void call() throws Exception {
        SerialPort serialPort = SerialPort.getCommPort(SERIAL_PORT);
        serialPort.setComPortParameters(9600, 8, 1, 0);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 100, 0);
        serialPort.openPort();
        System.out.println("serial port opened");
        try {
            while (true) {
                if (serialPort.bytesAvailable() > 0) {
                    Thread.sleep(1000);
                    byte[] buffer = new byte[serialPort.bytesAvailable()];
                    serialPort.readBytes(buffer, buffer.length);
                    String message = new String(buffer);
                    // Проверяем пришедшее сообщение
                    if (message.trim().length() == 26) {
                        // Если пришло сообщение в виде 26-значного номера, выполняем одно действие
                        System.out.println("Received number: " + message.trim());
                        updateMessage(message);
                    } else if (message.trim().contains("stop")) {
                        // Если пришло сообщение "stop", выполняем другое действие
                        System.out.println("Received 'stop' command.");
                        updateMessage("stop");
                        UserUtils.setCurrentUser(null);
                        Platform.runLater(ViewFactory::getAuthView);
                    } else {
                        System.out.println("Received '" + message + "' command.");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
