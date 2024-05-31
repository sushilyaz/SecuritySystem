package com.suhoi.controller;

import com.suhoi.model.Role;
import com.suhoi.model.User;
import com.suhoi.util.UserUtils;
import com.suhoi.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.io.File;

public class FileController {

    @FXML
    private Button addUserButton;

    @FXML
    private ListView<String> fileListView;

    @FXML
    public void initialize() {
        User currentUser = UserUtils.getCurrentUser();
        addUserButton.setVisible(currentUser != null && currentUser.getRole().equals(Role.ADMIN));
    }

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

    @FXML
    private void handleAddUser() {
        ViewFactory.getCreateUserView();
    }
}
