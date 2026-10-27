package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.UserCreateDTO;
import io.hexlet.spring.shopping.dto.UserDTO;
import io.hexlet.spring.shopping.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {

    UserDTO create(UserCreateDTO dto);

    List<UserDTO> getAll();

    UserDTO getById(Long id);

    UserDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
