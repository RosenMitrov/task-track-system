package http.tasktracksystem.domain.entities;

import http.tasktracksystem.domain.enums.TaskGroupStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
@Entity
@Table(name = "task_groups")
public class TaskGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TaskGroupStatus status;

    @Column(name = "created_at")
    private Instant createdAt;

    @OneToMany(mappedBy = "taskGroup")
    private List<TaskEntity> tasks;
}
