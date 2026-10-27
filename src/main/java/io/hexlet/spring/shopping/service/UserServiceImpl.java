package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.UserCreateDTO;
import io.hexlet.spring.shopping.dto.UserDTO;
import io.hexlet.spring.shopping.dto.UserUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.UserMapper;
import io.hexlet.spring.shopping.model.User;
import io.hexlet.spring.shopping.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final io.hexlet.spring.shopping.repository.ProductRepository productRepository;
    private final io.hexlet.spring.shopping.repository.ShoppingListRepository shoppingListRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        User user = userMapper.map(dto);
        User savedUser = userRepository.save(user);
        return userMapper.map(savedUser);
    }

    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    @Override
    public UserDTO getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.map(user);
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
        userMapper.update(dto, user);
        User savedUser = userRepository.save(user);
        return userMapper.map(savedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", id);
        }
        if (shoppingListRepository.existsByAuthorId(id) || productRepository.existsByAuthorId(id)) {
            throw new io.hexlet.spring.shopping.exception.ResourceConflictException("Delete the user's lists and products first");
        }
        userRepository.deleteById(id);
    }
}
