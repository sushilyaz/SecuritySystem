package com.suhoi.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.File;

public class FileController {
    @FXML
    private ListView<String> fileListView;

    public void setDirectory(String path) {

        File directory = new File(path);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                ObservableList<String> fileNames = FXCollections.observableArrayList();
                for (File file : files) {
                    fileNames.add(file.getName());
                }
                fileListView.setItems(fileNames);
            }
        } else {
            // Обработка ошибки: путь не является директорией
            System.out.println("Provided path is not a directory");
        }
    }
}
