package com.suhoi.util;

public class QuerySQL {
    public static final String FIND_BY_CARD_UID = "SELECT id, username, password_digest, card_uid, role, path FROM users WHERE card_uid = ?";
    public static final String FIND_BY_CARD_UID_AND_PASSWORD_DIGEST = "SELECT id, username, password_digest, card_uid, role, path FROM users WHERE card_uid = ? AND password_digest = ?";
    public static final String SAVE = "INSERT INTO users (username, password_digest, card_uid, role, path) VALUES (?, ?, ?, ?, ?)";
}
