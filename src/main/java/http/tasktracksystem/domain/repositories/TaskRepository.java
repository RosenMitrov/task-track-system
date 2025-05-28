package http.tasktracksystem.domain.repositories;

import http.tasktracksystem.domain.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
}
