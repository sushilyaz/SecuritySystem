package com.suhoi.view;


import com.suhoi.controller.AuthController;
import com.suhoi.controller.FileController;
import com.suhoi.repository.UserRepository;
import com.suhoi.repository.impl.UserRepositoryImpl;
import com.suhoi.service.UserService;
import com.suhoi.service.impl.UserServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ViewFactory {

    public static Stage primaryStage;

    private static UserService userService;

    private static FXMLLoader authFormLoader;
    private static FXMLLoader fileFormLoader;


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
        primaryStage.setScene(authScene);
        primaryStage.show();
    }

    public static void getFileExplorerView(String path) {
        try {
            if (fileFormLoader == null) {
                fileFormLoader = new FXMLLoader(ViewFactory.class.getResource("/fileView.fxml"));
                contentFileExplorerView = fileFormLoader.load();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (fileExplorerScene == null) {
            fileExplorerScene = new Scene(contentFileExplorerView);
        } else {
            fileExplorerScene.setRoot(contentFileExplorerView);
        }
        FileController controller = fileFormLoader.getController();
        controller.setDirectory(path);
        primaryStage.setScene(fileExplorerScene);
        primaryStage.show();
    }

    private static void dependencyInjection() {
        UserRepository userRepository = new UserRepositoryImpl();
        userService = new UserServiceImpl(userRepository);
    }
}
