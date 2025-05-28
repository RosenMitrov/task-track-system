package http.tasktracksystem.domain.repositories;

import http.tasktracksystem.domain.entities.TaskGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskGroupRepository extends JpaRepository<TaskGroupEntity, Long> {

    boolean existsByName(String name);
}
