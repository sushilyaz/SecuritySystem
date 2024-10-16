package com.suhoi.repository.impl;

import com.suhoi.model.Role;
import com.suhoi.model.User;
import com.suhoi.repository.UserRepository;
import com.suhoi.util.Alerts;
import com.suhoi.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.suhoi.util.QuerySQL.*;

public class UserRepositoryImpl implements UserRepository{

    @Override
    public int save(User user) {
        try (Connection connection = ConnectionManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(SAVE_USER)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPasswordDigest());
            preparedStatement.setString(3, user.getCardUid());
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.executeUpdate();
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                return generatedKeys.getInt(1); // Возвращаем сгенерированный id
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return -1;
    }

    @Override
    public Optional<User> findByCardUid(String cardUid) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CARD_UID)) {

            preparedStatement.setString(1, cardUid);
            ResultSet resultSet = preparedStatement.executeQuery();

            User user = null;
            Map<Integer, User> usersMap = new HashMap<>(); // Карта для предотвращения дублирования пользователей

            while (resultSet.next()) {
                int userId = resultSet.getInt("id");

                // Если пользователь еще не создан, создаем и сохраняем его в карту
                if (!usersMap.containsKey(userId)) {
                    user = buildUser(resultSet);  // Создаем пользователя
                    usersMap.put(userId, user);  // Добавляем его в карту
                }

                // Получаем путь, если он существует, и добавляем его к списку путей пользователя
                String path = resultSet.getString("path");
                if (path != null) {
                    usersMap.get(userId).getPath().add(path);
                }
            }

            return usersMap.values().stream().findFirst();  // Возвращаем первого пользователя, если он есть
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
                .path(new ArrayList<>())  // Инициализируем пустой список путей, будем наполнять позже
                .build();
    }
}
