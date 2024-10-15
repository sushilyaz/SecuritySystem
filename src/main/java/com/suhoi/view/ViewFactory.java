package com.suhoi.view;


import com.suhoi.controller.AuthController;
import com.suhoi.controller.CreateUserController;
import com.suhoi.model.Role;
import com.suhoi.repository.DirectoryRepository;
import com.suhoi.repository.UserRepository;
import com.suhoi.repository.impl.DirectoryRepositoryImpl;
import com.suhoi.repository.impl.UserRepositoryImpl;
import com.suhoi.service.UserService;
import com.suhoi.service.impl.UserServiceImpl;
import com.suhoi.util.Alerts;
import com.suhoi.util.FileView;
import com.suhoi.util.UserUtils;
import javafx.collections.FXCollections;
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
import java.util.List;

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
//        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(authScene);
        primaryStage.setResizable(false);
//        primaryStage.setFullScreen(true);
        primaryStage.show();
    }

    public static void getFileExplorerView(List<String> availableDirectories) {
        VBox root = new VBox();

        // Создание ListView для отображения списка доступных директорий
        ListView<String> directoryListView = new ListView<>();
        directoryListView.setItems(FXCollections.observableArrayList(availableDirectories));
        // Обработка выбора директории
        directoryListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedDirectory = directoryListView.getSelectionModel().getSelectedItem();
                if (selectedDirectory != null) {
                    // Когда пользователь выберет директорию, откроем ее во FileView
                    openDirectory(selectedDirectory);
                }
            }
        });

        root.getChildren().addAll(directoryListView);

        Scene scene = new Scene(root, 800, 600); // Размер окна по умолчанию
        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setResizable(false);
        primaryStage.show();


    }

    public static void openDirectory(String path) {
        VBox root = new VBox();
        mFileView = new FileView(path);
        VBox.setVgrow(mFileView, Priority.ALWAYS);
        root.getChildren().addAll(getMenuBar(), mFileView, getToolBar());
        primaryStage.setFullScreenExitHint("");
//        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
//        primaryStage.setFullScreen(true);
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
        primaryStage.setResizable(false);
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
                createUserStage.initOwner(primaryStage);
                createUserStage.initModality(Modality.APPLICATION_MODAL);
            }
            controller.setStage(createUserStage);
            createUserStage.setTitle("Создание нового пользователя");
            createUserStage.setScene(new Scene(root));
            createUserStage.setAlwaysOnTop(true);
            createUserStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void dependencyInjection() {
        UserRepository userRepository = new UserRepositoryImpl();
        DirectoryRepository directoryRepository = new DirectoryRepositoryImpl();
        userService = new UserServiceImpl(userRepository, directoryRepository);
    }

    private static MenuBar getMenuBar() {
        Menu fileMenu = new Menu("Файл");

        // Create file menu
        MenuItem newFile = new MenuItem("Новый файл");
        newFile.setOnAction(e -> mFileView.createFile());
        newFile.setAccelerator(SHORTCUT_NEW_FILE);

        MenuItem newFolder = new MenuItem("Новая папка");
        newFolder.setOnAction(e -> mFileView.createDirectory());
        newFolder.setAccelerator(SHORTCUT_NEW_DIRECTORY);

        MenuItem renameItem = new MenuItem("Переименовать");
        renameItem.setOnAction(e -> mFileView.rename());
        renameItem.setAccelerator(SHORTCUT_RENAME);

        MenuItem deleteItem = new MenuItem("Удалить");
        deleteItem.setOnAction(e -> mFileView.delete());
        deleteItem.setAccelerator(SHORTCUT_DELETE);

        MenuItem addNewUserItem = new MenuItem("Добавить нового пользователя");
        if (!UserUtils.getCurrentUser().getRole().equals(Role.ADMIN)) {
            addNewUserItem.setVisible(false);
        }
        addNewUserItem.setOnAction(e -> ViewFactory.getCreateUserView());

        fileMenu.getItems().addAll(newFile, newFolder, renameItem, deleteItem, addNewUserItem);

        //Create helpMenu
        Menu helpMenu = new Menu("Help");
        MenuItem aboutMenuItem = new MenuItem("About");
        aboutMenuItem.setOnAction(e -> Alerts.showInfoAlert("Security File Manager. \n Copyright © 2024 by Ilya Sushentsov")
        );
        helpMenu.getItems().addAll(aboutMenuItem);

        return new MenuBar(fileMenu, helpMenu);
    }

    private static ToolBar getToolBar() {
        Label labelCopy = new Label("F5 Копировать");
        labelCopy.setOnMouseClicked(e -> mFileView.copy());

        Label labelMove = new Label("F6 Переместить");
        labelMove.setOnMouseClicked(e -> mFileView.move());

        return new ToolBar(labelCopy, new Separator(), labelMove);
    }
}
