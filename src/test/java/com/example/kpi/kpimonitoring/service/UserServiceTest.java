package com.example.kpi.services;

import com.example.kpi.dto.UserRequest;
import com.example.kpi.dto.UserResponse;
import com.example.kpi.entities.User;
import com.example.kpi.enums.Role;
import com.example.kpi.exceptions.ResourceExistException;
import com.example.kpi.exceptions.ResourceNotFoundException;
import com.example.kpi.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Kullanıcı nesnesi
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setRoles(Collections.singleton(Role.valueOf("EMPLOYEE")));
    }

    @Test
    void testGetAllUsers() {
        // Mock veri
        User user2 = new User();
        user2.setId(2L);
        user2.setEmail("user2@example.com");
        user2.setRoles(Collections.singleton(Role.valueOf("CEO")));

        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("test@example.com", users.get(0).email());
        assertEquals("user2@example.com", users.get(1).email());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById(1L);

        assertNotNull(response);
        assertEquals("test@example.com", response.email());
    }

    @Test
    void testCreateUser() throws ResourceExistException {
        UserRequest userRequest = new UserRequest("new@example.com", "password",
                Collections.singleton(Role.valueOf("CEO")));

        when(userRepository.existsByEmail(userRequest.email())).thenReturn(false);

        userService.createUser(userRequest);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUserThrowsExceptionIfUserExists() {
        UserRequest userRequest = new UserRequest("existing@example.com", "password",
                Collections.singleton(Role.valueOf("EMPLOYEE")));

        when(userRepository.existsByEmail(userRequest.email())).thenReturn(true);

        assertThrows(ResourceExistException.class, () -> userService.createUser(userRequest));
    }

    @Test
    void testUpdateUser() {
        UserRequest updatedUser = new UserRequest("updated@example.com", "newpassword", Collections.singleton(Role.valueOf("CEO")));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserRequest result = userService.updateUser(1L, updatedUser);

        assertEquals("updated@example.com", result.email());
    }

    @Test
    void testDeleteUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testDeleteUserByIdThrowsExceptionIfUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(1L));
    }

    @Test
    void testAddRoleToUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.addRoleToUser(1L, Role.CEO);

        assertTrue(response.roleName().contains("CEO"));
    }

    @Test
    void testRemoveRoleFromUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = userService.removeRoleFromUser(1L, Role.EMPLOYEE);  // Rolü EMPLOYEE olarak kaldırıyoruz

        assertFalse(response.roleName().contains("EMPLOYEE"));
    }

    @Test
    void testGetUserProfile() {
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserProfile();

        assertNotNull(response);
        assertEquals("test@example.com", response.email());
    }
}
