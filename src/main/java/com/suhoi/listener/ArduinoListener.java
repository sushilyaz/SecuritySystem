package com.suhoi.listener;

import com.fazecast.jSerialComm.SerialPort;
import com.suhoi.view.ViewFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;

public class ArduinoListener extends Task<Void> {

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
                    // Проверяем пришедшее сообщение
                    if (message.trim().length() == 26) {
                        // Если пришло сообщение в виде 26-значного номера, выполняем одно действие
                        System.out.println("Received number: " + message.trim());
                        updateMessage(message);
                    } else if (message.trim().equalsIgnoreCase("stop")) {
                        // Если пришло сообщение "stop", выполняем другое действие
                        System.out.println("Received 'stop' command.");
                        updateMessage("stop");
                        Platform.runLater(ViewFactory::getAuthView);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
