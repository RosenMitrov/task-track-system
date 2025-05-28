package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.requests.UserRegisterRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.dtos.responses.UserSecurityDto;
import http.tasktracksystem.domain.entities.RoleEntity;
import http.tasktracksystem.domain.entities.UserEntity;
import http.tasktracksystem.domain.exceptions.custom.AlreadyExistsException;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import http.tasktracksystem.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private  RoleService roleService;

    @InjectMocks
    private UserService userServiceTest;

    @Test
    void test_getUserByUsername_success() {
        final String username = "admin";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setUsername(username);

        when(this.userRepository.findByUsername(username))
                .thenReturn(Optional.of(expectedUser));

        UserEntity actualUser = this.userServiceTest.getUserByUsername(username);

        verify(userRepository, times(1))
                .findByUsername(username);
        assertEquals(expectedUser, actualUser);
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
    }

    @Test
    void test_getUserByUsername_shouldThrow() {
        final String username = "not-found";
        when(this.userRepository.findByUsername(username))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> this.userServiceTest.getUserByUsername(username));
    }


    @Test
    void test_getUserById_success() {
        final Long userId = 100L;
        final String username = "admin";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setUsername(username);
        expectedUser.setId(userId);

        when(this.userRepository.findById(userId))
                .thenReturn(Optional.of(expectedUser));

        UserEntity actualUser = this.userServiceTest.getUserById(userId);

        verify(userRepository, times(1))
                .findById(userId);
        assertEquals(expectedUser, actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
    }

    @Test
    void test_getUserById_shouldThrow() {
        final Long userId = 100L;
        when(this.userRepository.findById(userId))
                .thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class,
                () -> this.userServiceTest.getUserById(userId));
    }

    @Test
    void test_getUserForSecurity_success() {
        final String username = "admin";
        UserEntity expectedUser = new UserEntity();
        expectedUser.setUsername(username);
        RoleEntity role = new RoleEntity();
        role.setName(ROLE_USER);
        expectedUser.setRoles(Set.of(role));

        when(this.userRepository.findByUsername(username))
                .thenReturn(Optional.of(expectedUser));

        UserSecurityDto actualUser = this.userServiceTest.getUserForSecurity(username);

        verify(userRepository, times(1))
                .findByUsername(username);

        assertEquals(expectedUser.getUsername(), actualUser.username());
        assertEquals(expectedUser.getRoles().size(), actualUser.roles().size());
        assertEquals(expectedUser.getRoles().stream().map(RoleEntity::getName).toList(), actualUser.roles());
        assertEquals(ROLE_USER, actualUser.roles().get(0));
    }

    @Test
    void test_UserSecurityResponse_shouldThrow() {
        final String username = "not-found";
        when(this.userRepository.findByUsername(username))
                .thenThrow(UsernameNotFoundException.class);
        assertThrows(UsernameNotFoundException.class,
                () -> this.userServiceTest.getUserForSecurity(username));
    }

    @Test
    void test_registerUser_shouldThrowWithExistingUsername() {
        final String username = "not-found-username";
        final String email = "not-found-email";
        final String password = "fake-pass";
        UserRegisterRequest request = new UserRegisterRequest(username, email, password);
        when(this.userRepository.existsByUsername(username))
                .thenReturn(true);
        assertThrows(AlreadyExistsException.class,
                () -> this.userServiceTest.registerUser(request));
    }

    @Test
    void test_registerUser_shouldThrowWithExistingEmail() {
        final String username = "not-found-username";
        final String email = "not-found-email";
        final String password = "fake-pass";
        UserRegisterRequest request = new UserRegisterRequest(username, email, password);
        when(this.userRepository.existsByEmail(email))
                .thenReturn(true);
        assertThrows(AlreadyExistsException.class,
                () -> this.userServiceTest.registerUser(request));
    }

    @Test
    void test_registerUser_success() {
        final String username = "not-found-username";
        final String email = "not-found-email";
        final String password = "fake-pass";
        final Long userId = 1L;
        UserRegisterRequest request = new UserRegisterRequest(username, email, password);
        RoleEntity role = new RoleEntity();
        role.setName(ROLE_USER);
        UserEntity savedUser = new UserEntity();
        savedUser.setId(userId);
        savedUser.setUsername(username);
        savedUser.setEmail(email);
        savedUser.setCreatedAt(Instant.now());
        savedUser.setUpdatedAt(Instant.now());
        savedUser.addRole(role);


        when(this.userRepository.existsByEmail(email)).thenReturn(false);
        when(this.userRepository.existsByUsername(username)).thenReturn(false);
        when(this.passwordEncoder.encode(password)).thenReturn("encoded-pass");
        when(this.roleService.findUserRole()).thenReturn(role);
        when(userRepository.saveAndFlush(any(UserEntity.class))).thenReturn(savedUser);

        ApiResponse<Map<String, Object>> response = this.userServiceTest.registerUser(request);

        assertNotNull(response);
        assertEquals(REGISTER_USER, response.message());
        assertEquals(userId, response.data().get(ID));
        assertEquals(username, response.data().get(USERNAME));
        assertEquals(email, response.data().get(EMAIL));
        verify(this.userRepository, times(1))
                .saveAndFlush(any(UserEntity.class));
    }

    @Test
    void test_delete_user_success() {
        final Long userId = 555L;
        final String deletedBy = "user";
        UserEntity toDelete = new UserEntity();
        when(this.userRepository.findById(userId))
                .thenReturn(Optional.of(toDelete));

        ApiResponse<Map<String, Object>> response = this.userServiceTest.delete(userId, deletedBy);

        assertNotNull(response);
        assertEquals(DELETE_USER, response.message());
        assertEquals(userId, response.data().get(DELETED_USER_ID));
        assertEquals(deletedBy, response.data().get(DELETED_BY));

        verify(this.userRepository, times(1))
                .findById(userId);
        verify(this.userRepository, times(1))
                .delete(toDelete);
    }

    @Test
    void test_delete_user_shouldThrow() {
        final Long userId = 555L;
        final String deletedBy = "user";
        when(this.userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> this.userServiceTest.delete(userId, deletedBy));

        verify(this.userRepository, never())
                .delete(any(UserEntity.class));
    }

}