package com.suhoi.service.impl;

import com.suhoi.dto.AuthDto;
import com.suhoi.dto.UserCreateDto;
import com.suhoi.model.User;
import com.suhoi.repository.UserRepository;
import com.suhoi.service.UserService;
import com.suhoi.util.Alerts;
import com.suhoi.view.ViewFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public void auth(AuthDto dto) {
        Optional<User> user = userRepository.findByCardUidAndPassword(dto.getCardUID(), dto.getPassword());
        if (user.isPresent()) {
            ViewFactory.getFileExplorerView(user.get().getPath());
        } else {
            Alerts.showErrorAlert("Пользователь не найден");
        }

    }

    @Override
    public void save(UserCreateDto user) {

    }
}
