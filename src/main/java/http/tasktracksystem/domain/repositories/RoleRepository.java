package http.tasktracksystem.domain.repositories;

import http.tasktracksystem.domain.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);

    Set<RoleEntity> findAllByNameIn(Set<String> roles);
}
