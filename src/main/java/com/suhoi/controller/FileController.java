//package com.suhoi.controller;
//
//import com.suhoi.model.Role;
//import com.suhoi.model.User;
//import com.suhoi.util.UserUtils;
//import com.suhoi.view.ViewFactory;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.control.Button;
//import javafx.scene.control.ListView;
//import javafx.scene.control.SelectionMode;
//import javafx.scene.control.TextField;
//import javafx.scene.control.TextInputDialog;
//import javafx.scene.input.MouseEvent;
//
//import java.awt.*;
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Optional;
//
//public class FileController {
//
//    @FXML
//    private Button addUserButton;
//
//    @FXML
//    private ListView<String> fileListView;
//
//    private ObservableList<String> childrenList;
//
//    private File mDirectory;
//    private TextField mTextField;
//
//    private Path userDirectory;
//
//    @FXML
//    public void initialize() {
//        User currentUser = UserUtils.getCurrentUser();
//        addUserButton.setVisible(currentUser != null && currentUser.getRole().equals(Role.ADMIN));
//    }
//
//    public void setDirectory(String directory) {
//        this.userDirectory = Paths.get(directory);
//        listFiles(userDirectory);
//    }
//
//    private void listFiles(Path directory) {
//        try {
//            fileListView.getItems().clear();
//            Files.list(directory).forEach(path -> fileListView.getItems().add(path.getFileName().toString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FXML
//    private void handleFileClick(MouseEvent event) {
//        String selectedFile = fileListView.getSelectionModel().getSelectedItem();
//        if (selectedFile == null) {
//            return; // No file selected
//        }
//
//        Path selectedPath = userDirectory.resolve(selectedFile);
//
//        if (Files.isDirectory(selectedPath) && isWithinUserDirectory(selectedPath)) {
//            setDirectory(selectedPath.toString());
//        } else {
//            try {
//                File fileToOpen = selectedPath.toFile();
//                if (fileToOpen.exists() && fileToOpen.isFile()) {
//                    Desktop.getDesktop().open(fileToOpen);
//                } else {
//                    System.out.println("File does not exist or is not a file: " + selectedPath);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                System.out.println("Error opening file: " + selectedPath);
//            }
//        }
//    }
//
//
//    private boolean isWithinUserDirectory(Path path) {
//        return path.startsWith(userDirectory);
//    }
//
//    @FXML
//    private void handleCreateNewFolder(ActionEvent event) {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Create New Folder");
//        dialog.setHeaderText("Enter the name of the new folder:");
//        Optional<String> result = dialog.showAndWait();
//
//        result.ifPresent(name -> {
//            Path newFolderPath = userDirectory.resolve(name);
//            if (isWithinUserDirectory(newFolderPath)) {
//                try {
//                    Files.createDirectory(newFolderPath);
//                    listFiles(userDirectory);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//
//    @FXML
//    private void handleCreateNewFile(ActionEvent event) {
//        TextInputDialog dialog = new TextInputDialog();
//        dialog.setTitle("Create New File");
//        dialog.setHeaderText("Enter the name of the new file:");
//        Optional<String> result = dialog.showAndWait();
//
//        result.ifPresent(name -> {
//            Path newFilePath = userDirectory.resolve(name);
//            if (isWithinUserDirectory(newFilePath)) {
//                try {
//                    Files.createFile(newFilePath);
//                    listFiles(userDirectory);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//    @FXML
//    private void handleAddUser() {
//
//    }
//}
