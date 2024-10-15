package com.suhoi.repository.impl;

import com.suhoi.model.Directory;
import com.suhoi.repository.DirectoryRepository;
import com.suhoi.util.Alerts;
import com.suhoi.util.ConnectionManager;
import com.suhoi.view.ViewFactory;

import java.sql.*;

import static com.suhoi.util.QuerySQL.SAVE_DIRECTORY;
import static com.suhoi.util.QuerySQL.SAVE_USER_DIRECTORY;

public class DirectoryRepositoryImpl implements DirectoryRepository {
    @Override
    public int save(Directory directory) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_DIRECTORY, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, directory.getPath());
            preparedStatement.executeUpdate();

            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Возвращаем сгенерированный id
            }
        } catch (SQLException e) {
            Alerts.showErrorAlert("Ошибка сохранения в таблицу Directory", ViewFactory.primaryStage);
        }
        return -1; // Возвращаем -1 в случае ошибки
    }

    @Override
    public void saveUserDirectory(int userId, int directoryId) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER_DIRECTORY)) {
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, directoryId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            Alerts.showErrorAlert("Ошибка сохранения в таблицу User-Directory", ViewFactory.primaryStage);
        }
    }
}
