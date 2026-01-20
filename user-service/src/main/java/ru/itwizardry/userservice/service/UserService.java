package ru.itwizardry.userservice.service;

import ru.itwizardry.userservice.dto.UserCreateRequest;
import ru.itwizardry.userservice.dto.UserDto;
import ru.itwizardry.userservice.dto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    UserDto createUser(UserCreateRequest request);

    UserDto updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    UserDto getUserById(Long id);

    List<UserDto> findByAge(int age);

    List<UserDto> getAllUsers();
}