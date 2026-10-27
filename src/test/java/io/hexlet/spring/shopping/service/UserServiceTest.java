package io.hexlet.spring.shopping.service;

import io.hexlet.spring.shopping.dto.UserCreateDTO;
import io.hexlet.spring.shopping.dto.UserDTO;
import io.hexlet.spring.shopping.dto.UserUpdateDTO;
import io.hexlet.spring.shopping.exception.ResourceNotFoundException;
import io.hexlet.spring.shopping.mapper.UserMapper;
import io.hexlet.spring.shopping.model.User;
import io.hexlet.spring.shopping.repository.UserRepository;
import io.hexlet.spring.shopping.util.ModelGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private io.hexlet.spring.shopping.repository.ProductRepository productRepository;

    @Mock
    private io.hexlet.spring.shopping.repository.ShoppingListRepository shoppingListRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreate() {
        UserCreateDTO createDTO = ModelGenerator.buildUserCreateDTO();
        User user = ModelGenerator.buildUser();
        user.setEmail(createDTO.getEmail());
        User saved = ModelGenerator.buildUser();
        saved.setId(1L);
        saved.setEmail(createDTO.getEmail());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(createDTO.getEmail());

        given(userMapper.map(createDTO)).willReturn(user);
        given(userRepository.save(user)).willReturn(saved);
        given(userMapper.map(saved)).willReturn(userDTO);

        UserDTO result = userService.create(createDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(createDTO.getEmail());
        verify(userMapper, times(1)).map(createDTO);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testGetById() {
        User user = ModelGenerator.buildUser();
        user.setId(1L);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setEmail(user.getEmail());

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userMapper.map(user)).willReturn(userDTO);

        UserDTO result = userService.getById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo(user.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetByIdNotFound() {
        Long fakeId = 999L;
        given(userRepository.findById(fakeId)).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getById(fakeId));
        verify(userRepository, times(1)).findById(fakeId);
        verify(userMapper, never()).map(any(User.class));
    }

    @Test
    void testUpdate() {
        User user = ModelGenerator.buildUser();
        user.setId(1L);
        UserUpdateDTO updateDTO = new UserUpdateDTO();
        updateDTO.setFirstName(JsonNullable.of("NewName"));
        User saved = ModelGenerator.buildUser();
        saved.setId(1L);
        saved.setFirstName("NewName");
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setFirstName("NewName");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(userRepository.save(user)).willReturn(saved);
        given(userMapper.map(saved)).willReturn(userDTO);

        UserDTO result = userService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("NewName");
        verify(userMapper, times(1)).update(updateDTO, user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testDelete() {
        Long id = 1L;
        given(userRepository.existsById(id)).willReturn(true);

        userService.delete(id);

        verify(userRepository, times(1)).deleteById(id);
        verify(userRepository, times(1)).existsById(id);
    }
    @Test
    void testGetAll() {
        User entity = new User();
        UserDTO dto = new UserDTO();
        dto.setId(7L);
        given(userRepository.findAll()).willReturn(java.util.List.of(entity));
        given(userMapper.map(entity)).willReturn(dto);
        assertThat(userService.getAll()).containsExactly(dto);
    }
}
