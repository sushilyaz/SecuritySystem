package com.suhoi.repository;

import com.suhoi.model.User;

import java.util.Optional;

public interface UserRepository {
    int save(User user);
    Optional<User> findByCardUid(String cardUid);
    Optional<User> findByCardUidAndPassword(String cardUid, String password);
}
