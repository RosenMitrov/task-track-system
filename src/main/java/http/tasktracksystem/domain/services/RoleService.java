package http.tasktracksystem.domain.services;

import http.tasktracksystem.domain.entities.RoleEntity;
import http.tasktracksystem.domain.enums.RoleType;
import http.tasktracksystem.domain.exceptions.custom.NotFoundException;
import http.tasktracksystem.domain.repositories.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

import static http.tasktracksystem.domain.utils.responses.GeneralTemplates.*;
import static http.tasktracksystem.domain.utils.responses.GeneralUtils.formatRow;

@AllArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Package private as it could be reachable only from domain
     *
     * @return RoleEntity if present or throws NotFoundException.
     */
    RoleEntity findUserRole() {
        return roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new NotFoundException(formatRow(ROLE_NOT_FOUND, ROLE_USER)));
    }

    Set<RoleEntity> findAllByNames(Set<RoleType> roles) {
        Set<RoleEntity> allRoles = this.roleRepository.findAllByNameIn(mapToName(roles));
        if (allRoles.isEmpty()) {
            throw new NotFoundException(ROLES_NOT_FOUND);
        }
        return allRoles;
    }

    private Set<String> mapToName(Set<RoleType> roles) {
        return roles.stream().map(RoleType::name).collect(Collectors.toSet());
    }
}
