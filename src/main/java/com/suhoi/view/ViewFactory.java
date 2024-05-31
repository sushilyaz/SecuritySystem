package com.suhoi.view;


import com.suhoi.controller.AuthController;
import com.suhoi.controller.CreateUserController;
import com.suhoi.controller.FileController;
import com.suhoi.repository.UserRepository;
import com.suhoi.repository.impl.UserRepositoryImpl;
import com.suhoi.service.UserService;
import com.suhoi.service.impl.UserServiceImpl;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;

public class ViewFactory {

    public static Stage primaryStage;

    @Getter
    private static Stage createUserStage;


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
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        primaryStage.setScene(authScene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(true);
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
        controller.initialize();
        controller.setDirectory(path);
        primaryStage.setScene(fileExplorerScene);
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
}
