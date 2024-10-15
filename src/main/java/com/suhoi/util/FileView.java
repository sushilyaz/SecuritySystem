package com.suhoi.util;

import com.suhoi.view.ViewFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;

public class FileView extends HBox {
    private static final String ACTION_SELECT = "select";
    private static final String ACTION_COPY = "copy";
    private static final String ACTION_MOVE = "move";
    private static final String ACTION_DELETE = "delete";
    private static final String ACTION_OPEN = "open";

    private ListView mLeftPane;
    private ListView mRightPane;

    public FileView(List<String> path) {
        if (path == null || path.isEmpty()) {
            Alerts.showErrorAlert("У пользователя нет разрешенных директорий", ViewFactory.primaryStage);
        }
        mLeftPane = new ListView(path);
        mRightPane = new ListView(path);


        mLeftPane.getTextField().setOnAction(e -> onTextEntered(mLeftPane.getTextField()));
        mRightPane.getTextField().setOnAction(e -> onTextEntered(mRightPane.getTextField()));
        VBox leftView = new VBox(mLeftPane.getTextField(), mLeftPane);
        VBox rightView = new VBox(mRightPane.getTextField(), mRightPane);
        mLeftPane.setFocusTraversable(true);

        VBox.setVgrow(mLeftPane, Priority.ALWAYS);
        VBox.setVgrow(mRightPane, Priority.ALWAYS);
        HBox.setHgrow(leftView, Priority.ALWAYS);
        HBox.setHgrow(rightView, Priority.ALWAYS);
        getChildren().addAll(leftView, rightView);
    }

    public void copy() {
        if (mLeftPane.isFocused()) {
            List<Path> source = mLeftPane.getSelection();
            Path target = mRightPane.getDirectory();
            FileHelper.copy(source, target);
        } else if (mRightPane.isFocused()) {
            List<Path> source = mRightPane.getSelection();
            Path target = mLeftPane.getDirectory();
            FileHelper.copy(source, target);
        }
    }

    public void move() {
        if (mLeftPane.isFocused()) {
            List<Path> source = mLeftPane.getSelection();
            Path target = mRightPane.getDirectory();
            FileHelper.move(source, target);
        } else if (mRightPane.isFocused()) {
            List<Path> source = mRightPane.getSelection();
            Path target = mLeftPane.getDirectory();
            FileHelper.move(source, target);
        }
    }

    public void delete() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane != null) FileHelper.delete(focusedPane.getSelection());
    }

    public void rename() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane != null) {
            List<Path> selection = focusedPane.getSelection();
            if (selection.size() == 1) FileHelper.rename(selection.get(0));
        }
    }

    public void createDirectory() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane != null) FileHelper.createDirectory(focusedPane.getDirectory());
    }

    public void createFile() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane != null) FileHelper.createFile(focusedPane.getDirectory());
    }

    public void focusTextField() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane != null) focusedPane.getTextField().requestFocus();
    }

    private ListView getFocusedPane() {
        if (mLeftPane.isFocused() || mLeftPane.getTextField().isFocused()) {
            return mLeftPane;
        } else if (mRightPane.isFocused() || mRightPane.getTextField().isFocused()) {
            return mRightPane;
        } else {
            return null;
        }
    }

    private ListView getFocusedPane(TextField textField) {
        if (textField == mLeftPane.getTextField()) {
            return mLeftPane;
        } else {
            return mRightPane;
        }
    }

    private Path getSelectedPath() {
        ListView focusedPane = getFocusedPane();
        if (focusedPane == null) return null;
        List<Path> selection = focusedPane.getSelection();
        if (selection.size() != 1) return null;
        return selection.get(0);
    }

    private void onTextEntered(TextField textField) {
        ListView focusedPane = getFocusedPane(textField);
        String command = textField.getText().trim();
        File file = new File(command);
        if (file.exists()) {
            focusedPane.openFile(file);
            focusedPane.requestFocus();
        } else if (command.startsWith(ACTION_SELECT)) {
            String regex = command.substring(ACTION_SELECT.length()).trim();
            focusedPane.select(regex);
            focusedPane.requestFocus();
        } else if (command.startsWith(ACTION_COPY)) {
            String regex = command.substring(ACTION_COPY.length()).trim();
            focusedPane.select(regex);
            focusedPane.requestFocus();
            copy();
        } else if (command.startsWith(ACTION_MOVE)) {
            String regex = command.substring(ACTION_MOVE.length()).trim();
            focusedPane.select(regex);
            focusedPane.requestFocus();
            move();
        } else if (command.startsWith(ACTION_DELETE)) {
            String regex = command.substring(ACTION_DELETE.length()).trim();
            focusedPane.select(regex);
            focusedPane.requestFocus();
            delete();
        } else if (command.startsWith(ACTION_OPEN)) {
            String regex = command.substring(ACTION_OPEN.length()).trim();
            focusedPane.select(regex);
            focusedPane.requestFocus();
            for (Path path : focusedPane.getSelection()) {
                try {
                    Desktop.getDesktop().open(path.toFile());
                } catch (Exception e) {
                    DialogHelper.showException(e);
                }
            }
        }
        textField.setText(focusedPane.getDirectory().toString());
    }
}
