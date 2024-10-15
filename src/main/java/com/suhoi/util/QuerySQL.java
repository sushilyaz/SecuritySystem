package com.suhoi.util;

public class QuerySQL {
    public static final String FIND_BY_CARD_UID = "SELECT u.id, u.username, u.password_digest, u.card_uid, u.role, d.path " +
            "FROM users u " +
            "LEFT JOIN user_directories ud ON u.id = ud.user_id " +
            "LEFT JOIN directories d ON ud.directory_id = d.id " +
            "WHERE u.card_uid = ?";
    public static final String FIND_BY_CARD_UID_AND_PASSWORD_DIGEST = "SELECT id, username, password_digest, card_uid, role, path FROM users WHERE card_uid = ? AND password_digest = ?";
    public static final String SAVE_USER = "INSERT INTO users (username, password_digest, card_uid, role) VALUES (?, ?, ?, ?)";
    public static final String SAVE_DIRECTORY = "INSERT INTO directories (path) VALUES (?)";
    public static final String SAVE_USER_DIRECTORY = "INSERT INTO user_directories (user_id, directory_id) VALUES (?, ?)";
}
