package com.suhoi.util;

import com.suhoi.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListView extends javafx.scene.control.ListView<String> {
    private File mDirectory;

    private TextField mTextField;

    private ObservableList<String> mChildrenList;

    private String userDirectory;

    private WatchServiceHelper mWatchServiceHelper;

    public ListView(String path) {
        super();
        userDirectory = path;
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mDirectory = new File(path);

        mChildrenList = FXCollections.observableArrayList();
        setItems(mChildrenList);

        mTextField = new TextField();
        mTextField.setStyle("-fx-font-size: 10px;");

        setOnKeyPressed(key -> {
            switch (key.getCode()) {
                case ENTER:
                    if (isFocused()) {
                        navigate(getSelectionModel().getSelectedItem());
                    }
                    break;
                case BACK_SPACE:

                    back();
                    break;
            }
        });
        setOnMouseClicked(m -> {
            if (m.getButton().equals(MouseButton.PRIMARY) && m.getClickCount() == 2)
                navigate(getSelectionModel().getSelectedItem());
        });
        setCellFactory(new Callback<javafx.scene.control.ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(javafx.scene.control.ListView<String> list) {
                return new SystemIconsHelper.AttachmentListCell(ListView.this);
            }
        });
        mWatchServiceHelper = new WatchServiceHelper(this);

        refresh();
    }


    public void refresh() {
        if (!isWithinUserDirectory(Paths.get(mDirectory.getAbsolutePath()))) {
            Alerts.showErrorAlert("Отказано в доступе", ViewFactory.primaryStage);
            return;
        }
        showList(getCurrentFilesList());
        mTextField.setText(mDirectory.getAbsolutePath());
        mWatchServiceHelper.changeObservableDirectory(mDirectory.toPath());
    }

    public TextField getTextField() {
        return mTextField;
    }

    public java.util.List<Path> getSelection() {
        List<Path> selection = new ArrayList<>();
        for (String item : getSelectionModel().getSelectedItems()) {
            selection.add(mDirectory.toPath().resolve(item));
        }
        return selection;
    }

    public Path getDirectory() {
        return mDirectory.toPath();
    }
    public void select(String regex) {
        if (regex.startsWith("*")) regex = "." + regex;
        getSelectionModel().clearSelection();
        for (int i = 0; i < mChildrenList.size(); ++i) {
            String item = mChildrenList.get(i);
            if (item.matches(regex) || StringHelper.containsWord(item, regex)) {
                getSelectionModel().select(i);
            }
        }
    }
    private String[] getCurrentFilesList() {
        File[] listFiles = mDirectory.listFiles(file -> !file.isHidden());

        if (listFiles == null) {
            listFiles = new File[0];
        }

        Arrays.sort(listFiles, (f1, f2) -> {
            if ((f1.isDirectory() && f2.isDirectory()) || (f1.isFile() && f2.isFile())) {
                return f1.compareTo(f2);
            }
            return f1.isDirectory() ? -1 : 1;
        });

        String[] list = new String[listFiles.length];
        for (int i = 0; i < list.length; ++i) {
            list[i] = listFiles[i].getName();
        }

        return list;
    }
    private void showList(String[] list) {
        if (list != null) {
            mChildrenList.clear();
            mChildrenList.addAll(list);
        } else {
            mChildrenList.clear();
        }
    }
    public void openFile(File file) {
        if (!file.exists()) {
            refresh();
            return;
        }
        if (file.isDirectory()) {
            mDirectory = file;
            refresh();
        } else if (file.isFile()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (Exception e) {
                DialogHelper.showException(e);
            }
        }
    }

    private void navigate(String name) {
        String selectedPath = mDirectory.getAbsolutePath() + File.separator + name;
        if (!isWithinUserDirectory(Paths.get(selectedPath))) {
            Alerts.showErrorAlert("Отказано в доступе", ViewFactory.primaryStage);
            return;
        }
        File selectedFile = new File(selectedPath);
        if (selectedFile.isDirectory()) {
            mDirectory = selectedFile;
            refresh();
        } else {
            try {
                Desktop.getDesktop().open(selectedFile);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void back() {
        File parent = mDirectory.getParentFile();
        if (!isWithinUserDirectory(Paths.get(parent.getAbsolutePath()))) {
            Alerts.showErrorAlert("Отказано в доступе", ViewFactory.primaryStage);
            return;
        }
        mDirectory = parent;
        if (mDirectory.exists()) {
            refresh();
        } else {
            back();
        }
    }

    private boolean isWithinUserDirectory(Path path) {
        return path.startsWith(userDirectory);
    }
}
