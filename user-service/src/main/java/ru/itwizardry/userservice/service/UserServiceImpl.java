package ru.itwizardry.userservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itwizardry.userservice.domain.User;
import ru.itwizardry.userservice.dto.UserCreateRequest;
import ru.itwizardry.userservice.dto.UserDto;
import ru.itwizardry.userservice.dto.UserUpdateRequest;
import ru.itwizardry.userservice.exception.EmailAlreadyExistsException;
import ru.itwizardry.userservice.exception.UserNotFoundException;
import ru.itwizardry.userservice.kafka.publisher.UserEventPublisher;
import ru.itwizardry.userservice.mapper.UserMapper;
import ru.itwizardry.userservice.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;

    @Override
    public UserDto createUser(UserCreateRequest request) {
        try {
            User user = userMapper.toEntity(request);
            User saved = userRepository.saveAndFlush(user); // важно!
            userEventPublisher.publishUserCreated(saved.getEmail());
            return userMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException(request.email(), ex);
        }
    }

    @Override
    public UserDto updateUser(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        try {
            userMapper.updateEntity(user, request);
            User saved = userRepository.saveAndFlush(user);
            return userMapper.toDto(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new EmailAlreadyExistsException(request.email(), ex);
        }
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userRepository.delete(user);

        userEventPublisher.publishUserDeleted(user.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByAge(int age) {
        return userMapper.toDtoList(userRepository.findAllByAge(age));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }
}
