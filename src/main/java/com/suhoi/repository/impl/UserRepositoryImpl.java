package com.suhoi.repository.impl;

import com.suhoi.model.Role;
import com.suhoi.model.User;
import com.suhoi.repository.UserRepository;
import com.suhoi.util.Alerts;
import com.suhoi.util.ConnectionManager;

import java.sql.*;
import java.util.Optional;

import static com.suhoi.util.QuerySQL.*;

public class UserRepositoryImpl implements UserRepository{

    @Override
    public void save(User user) {
        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SAVE)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordDigest());
            preparedStatement.setString(3, user.getCardUid());
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setString(5, user.getPath());
            preparedStatement.executeUpdate();
            Alerts.showInfoAlert("Пользователь создан успешно");
        } catch (SQLException e) {
            Alerts.showErrorAlert(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByCardUid(String cardUid) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CARD_UID)) {
            preparedStatement.setString(1, cardUid);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<User> findByCardUidAndPassword(String cardUid, String password) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CARD_UID_AND_PASSWORD_DIGEST)) {
            preparedStatement.setString(1, cardUid);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            User user = null;
            if (resultSet.next()) {
                user = buildUser(resultSet);
            }
            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User buildUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .username(resultSet.getString("username"))
                .passwordDigest(resultSet.getString("password_digest"))
                .cardUid(resultSet.getString("card_uid"))
                .role(Role.valueOf(resultSet.getString("role")))
                .path(resultSet.getString("path"))
                .build();
    }
}
