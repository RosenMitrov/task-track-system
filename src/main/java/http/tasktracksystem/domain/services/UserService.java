package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.dtos.requests.ChangeUserRolesRequest;
import http.tasktracksystem.domain.dtos.requests.UserRegisterRequest;
import http.tasktracksystem.domain.dtos.responses.ApiResponse;
import http.tasktracksystem.domain.dtos.responses.UserSecurityDto;
import http.tasktracksystem.domain.entities.RoleEntity;
import http.tasktracksystem.domain.entities.UserEntity;
import http.tasktracksystem.domain.exceptions.custom.AlreadyExistsException;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import http.tasktracksystem.domain.repositories.UserRepository;
import http.tasktracksystem.domain.utils.responses.GeneralUtils;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static http.tasktracksystem.domain.utils.responses.GeneralUtils.formatRow;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Package private as it could be reachable only from domain
     *
     * @param username Username to be found.
     * @return UserEntity if present or throws NotFoundException.
     */
    UserEntity getUserByUsername(String username) {
        return this.userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(formatRow(USER_USERNAME_NOT_FOUND, username)));
    }

    /**
     * Package private as it could be reachable only from domain
     *
     * @param userId User by id to be found.
     * @return UserEntity if present or throws NotFoundException.
     */
    UserEntity getUserById(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(formatRow(USER_ID_NOT_FOUND, userId)));
    }

    /**
     * Used to build UserDetails.
     *
     * @param username Username to be found
     * @return UserSecurityResponse Id, Username, Email, Password and Roles
     */
    public UserSecurityDto getUserForSecurity(String username) {
        return this.userRepository.findByUsername(username)
                .map(this::mapToSecurityResponse)
                .orElseThrow(() -> new UsernameNotFoundException(formatRow(USER_USERNAME_NOT_FOUND, username)));
    }

    /**
     * @param request UserRegisterRequest - Username, Email and Password.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> registerUser(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.username()) || userRepository.existsByEmail(request.email())) {
            throw new AlreadyExistsException(formatRow(USER_USERNAME_EMAIL_EXISTS, request.username(), request.email()));
        }

        UserEntity newUser = createNewUser(request);
        RoleEntity roleUser = this.roleService.findUserRole();
        newUser.addRole(roleUser);

        UserEntity createdUser = userRepository.saveAndFlush(newUser);
        return GeneralUtils.buildApiResponse(
                REGISTER_USER,
                Map.of(ID, createdUser.getId(),
                        USERNAME, createdUser.getUsername(),
                        EMAIL, createdUser.getEmail(),
                        ROLES, covertRolesToArray(createdUser.getRoles()),
                        CREATED_AT, createdUser.getCreatedAt(),
                        UPDATED_AT, createdUser.getUpdatedAt()));
    }

    /**
     * @param userId    User id to be deleted.
     * @param deletedBy Username of authenticated user.
     * @return ApiResponse based on given parameters.
     */
    public ApiResponse<Map<String, Object>> delete(Long userId,
                                                   String deletedBy) {
        this.userRepository.findById(userId)
                .ifPresentOrElse(this.userRepository::delete,
                        () -> {
                            throw new NotFoundException(formatRow(USER_ID_NOT_FOUND, userId));
                        }
                );

        return GeneralUtils.buildApiResponse(
                DELETE_USER,
                Map.of(DELETED_USER_ID, userId,
                        DELETED_BY, deletedBy));
    }

    public ApiResponse<Map<String, Object>> applyRoles(ChangeUserRolesRequest request,
                                                       String changedBy) {
        Set<RoleEntity> allByNames = this.roleService.findAllByNames(request.roles());
        UserEntity user = getUserByUsername(request.changeTo());
        user.setRoles(allByNames);
        user.setUpdatedAt(Instant.now());

        user = this.userRepository.saveAndFlush(user);
        return GeneralUtils.buildApiResponse(
                CHANGE_ROLE_TO_USER,
                Map.of(CHANGED_BY, changedBy,
                        USER_ID, user.getId(),
                        USERNAME, user.getUsername())
        );
    }

    private UserSecurityDto mapToSecurityResponse(UserEntity user) {
        return UserSecurityDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .roles(mapRolesToStrings(user.getRoles()))
                .build();
    }

    private List<String> mapRolesToStrings(Set<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .toList();
    }

    private UserEntity createNewUser(UserRegisterRequest request) {
        return UserEntity.builder()
                .username(request.username())
                .email(request.email())
                .password(this.passwordEncoder.encode(request.password()))
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .roles(new HashSet<>())
                .build();
    }

    private String[] covertRolesToArray(Set<RoleEntity> roles) {
        return roles.stream()
                .map(RoleEntity::getName)
                .toArray(String[]::new);
    }
}
