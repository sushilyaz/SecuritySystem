package com.suhoi.repository;

import com.suhoi.model.Directory;

public interface DirectoryRepository {
    int save(Directory directory);
    void saveUserDirectory(int userId, int directoryId);
}
