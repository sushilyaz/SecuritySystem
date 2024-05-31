package com.suhoi.util;

public class QuerySQL {
    public static final String FIND_BY_CARD_UID_AND_PASSWORD_DIGEST = "SELECT id, username, password_digest, card_uid, role, path FROM users WHERE password_digest = ? AND card_uid = ?";
}
