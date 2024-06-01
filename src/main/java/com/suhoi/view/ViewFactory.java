package com.suhoi.view;


import com.suhoi.controller.AuthController;
import com.suhoi.controller.CreateUserController;
import com.suhoi.controller.FileController;
import com.suhoi.repository.UserRepository;
import com.suhoi.repository.impl.UserRepositoryImpl;
import com.suhoi.service.UserService;
import com.suhoi.service.impl.UserServiceImpl;
import com.suhoi.util.Alerts;
import com.suhoi.util.FileView;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class ViewFactory {

    private static final KeyCombination SHORTCUT_COPY = new KeyCodeCombination(KeyCode.F5);
    private static final KeyCombination SHORTCUT_MOVE = new KeyCodeCombination(KeyCode.F6);
    private static final KeyCombination SHORTCUT_DELETE = new KeyCodeCombination(KeyCode.DELETE);
    private static final KeyCombination SHORTCUT_NEW_FILE = new KeyCodeCombination(KeyCode.N,
            KeyCombination.SHORTCUT_DOWN);
    private static final KeyCombination SHORTCUT_NEW_DIRECTORY = new KeyCodeCombination(KeyCode.N,
            KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_RENAME = new KeyCodeCombination(KeyCode.F6, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_FOCUS_TEXT_FIELD = new KeyCodeCombination(KeyCode.D, KeyCombination.SHIFT_DOWN);
    private static final KeyCombination SHORTCUT_HTML_EDITOR = new KeyCodeCombination(KeyCode.F3);


    public static Stage primaryStage;

    public static Stage createUserStage;


    private static UserService userService;

    private static FXMLLoader authFormLoader;
    private static FXMLLoader fileFormLoader;

    private static FileView mFileView;

    private static Scene authScene;
    private static Scene fileExplorerScene;

    private static Parent contentAuthView;
    private static Parent contentFileExplorerView;

    static {
        dependencyInjection();
    }

    public static void getAuthView() {
        try {
            if (authFormLoader == null) {
                authFormLoader = new FXMLLoader(ViewFactory.class.getResource("/authForm.fxml"));
                contentAuthView = authFormLoader.load();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (authScene == null) {
            authScene = new Scene(contentAuthView);
            AuthController controller = authFormLoader.getController();
            controller.setUserService(userService);
        } else {
            authScene.setRoot(contentAuthView);
        }
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(authScene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void getFileExplorerView(String path) {
        VBox root = new VBox();
        mFileView = new FileView(path);
        VBox.setVgrow(mFileView, Priority.ALWAYS);
        root.getChildren().addAll(getMenuBar(), mFileView, getToolBar());
        Scene scene = new Scene(root);

        scene.addEventFilter(KeyEvent.KEY_RELEASED, e -> {
            if (SHORTCUT_DELETE.match(e)) {
                mFileView.delete();
            } else if (SHORTCUT_NEW_FILE.match(e)) {
                mFileView.createFile();
            } else if (SHORTCUT_NEW_DIRECTORY.match(e)) {
                mFileView.createDirectory();
            } else if (SHORTCUT_RENAME.match(e)) {
                mFileView.rename();
            } else if (SHORTCUT_COPY.match(e)) {
                mFileView.copy();
            } else if (SHORTCUT_MOVE.match(e)) {
                mFileView.move();
            } else if (SHORTCUT_FOCUS_TEXT_FIELD.match(e)) {
                mFileView.focusTextField();
            }
        });
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void getCreateUserView() {
        try {
            FXMLLoader loader = new FXMLLoader(ViewFactory.class.getResource("/createUserView.fxml"));
            Parent root = loader.load();
            CreateUserController controller = loader.getController();
            controller.setUserService(userService);
            if (createUserStage == null) {
                createUserStage = new Stage();
            }
            controller.setStage(createUserStage);
            createUserStage.setTitle("Create User");
            createUserStage.setScene(new Scene(root));
            createUserStage.setAlwaysOnTop(true);
            createUserStage.initOwner(primaryStage);
            createUserStage.initModality(Modality.APPLICATION_MODAL);
            createUserStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dependencyInjection() {
        UserRepository userRepository = new UserRepositoryImpl();
        userService = new UserServiceImpl(userRepository);
    }

    private static MenuBar getMenuBar() {
        Menu fileMenu = new Menu("File");

        // Create file menu
        MenuItem newFile = new MenuItem("New File");
        newFile.setOnAction(e -> mFileView.createFile());
        newFile.setAccelerator(SHORTCUT_NEW_FILE);

        MenuItem newFolder = new MenuItem("New Folder     ");
        newFolder.setOnAction(e -> mFileView.createDirectory());
        newFolder.setAccelerator(SHORTCUT_NEW_DIRECTORY);

        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(e -> mFileView.rename());
        renameItem.setAccelerator(SHORTCUT_RENAME);

        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> mFileView.delete());
        deleteItem.setAccelerator(SHORTCUT_DELETE);

        fileMenu.getItems().addAll(newFile, newFolder, renameItem, deleteItem);

        //Create helpMenu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(e -> Alerts.showInfoAlert("File Manager. \n Copyright © 2024 by Ilya Sushentsov")
        );
        helpMenu.getItems().addAll(aboutMenuItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private static ToolBar getToolBar() {
        Label labelCopy = new Label("F5 Copy");
        labelCopy.setOnMouseClicked(e -> mFileView.copy());

        Label labelMove = new Label("F6 Move");
        labelMove.setOnMouseClicked(e -> mFileView.move());

        return new ToolBar(labelCopy, new Separator(), labelMove);
    }
}
