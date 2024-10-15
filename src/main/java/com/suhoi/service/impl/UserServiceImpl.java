package com.suhoi.service.impl;

import com.suhoi.dto.AuthDto;
import com.suhoi.dto.UserCreateDto;
import com.suhoi.model.Directory;
import com.suhoi.model.Role;
import com.suhoi.model.User;
import com.suhoi.repository.DirectoryRepository;
import com.suhoi.repository.UserRepository;
import com.suhoi.service.UserService;
import com.suhoi.util.Alerts;
import com.suhoi.util.FileAccessControl;
import com.suhoi.util.UserUtils;
import com.suhoi.view.ViewFactory;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final DirectoryRepository directoryRepository;

    @Override
    public void auth(AuthDto dto) {
        Optional<User> user = userRepository.findByCardUid(dto.getCardUID());
        if (user.isPresent()) {
            if (BCrypt.checkpw(dto.getPassword(), user.get().getPasswordDigest())) {
                UserUtils.setCurrentUser(user.get());
                List<String> paths = user.get().getPath();
                for (String path : paths) {
                    boolean accessGranted = FileAccessControl.unblockSpecific(path);
                    if (!accessGranted) {
                        System.err.println("Failed to grant access to user's directory.");
                        System.exit(1);
                    }
                }

                ViewFactory.getFileExplorerView(user.get().getPath());
            } else {
                Alerts.showErrorAlert("Неправильный пароль", ViewFactory.primaryStage);
            }

        } else {
            Alerts.showErrorAlert("Пользователь не найден", ViewFactory.primaryStage);
        }

    }

    @Override
    public void save(UserCreateDto dto) {
        Optional<User> user = userRepository.findByCardUid(dto.getCardUid());
        if (user.isPresent()) {
            Alerts.showErrorAlert("Пользователь уже существует", ViewFactory.createUserStage);
        } else {
            String hashpw = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
            User build = User.builder()
                    .username(dto.getUsername())
                    .passwordDigest(hashpw)
                    .cardUid(dto.getCardUid())
                    .path(dto.getPath())
                    .role(Role.SIMPLE_USER)
                    .build();
            int userId = userRepository.save(build);
            for (String path : dto.getPath()) {
                int directoryId = directoryRepository.save(Directory.builder()
                        .path(path)
                        .build());

                directoryRepository.saveUserDirectory(userId, directoryId);
            }
            Alerts.showInfoAlert("Пользователь создан успешно");
        }
    }
}
