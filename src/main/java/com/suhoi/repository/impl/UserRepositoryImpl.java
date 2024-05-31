package com.suhoi.repository.impl;

import com.suhoi.model.Role;
import com.suhoi.model.User;
import com.suhoi.repository.UserRepository;
import com.suhoi.util.ConnectionManager;

import java.sql.*;
import java.util.Optional;

import static com.suhoi.util.QuerySQL.FIND_BY_CARD_UID_AND_PASSWORD_DIGEST;

public class UserRepositoryImpl implements UserRepository{

    @Override
    public void save(User user) {

    }

    @Override
    public Optional<User> findByCardUidAndPassword(String cardUid, String password) {
        try (Connection connection = ConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CARD_UID_AND_PASSWORD_DIGEST)) {
            preparedStatement.setString(1, password);
            preparedStatement.setString(2, cardUid);
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
