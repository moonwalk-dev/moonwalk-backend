package kr.moonwalk.moonwalk_api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "project_placement_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectPlacementHistory {

    @Id
    @Column(name = "history_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long projectId;
    private Long moduleId;
    private Long projectModuleId;
    private int positionX;
    private int positionY;
    private int angle;

    private String actionType;
    private LocalDateTime timestamp;

    public ProjectPlacementHistory(Long projectId, Long moduleId, Long projectModuleId, int positionX, int positionY, int angle, String actionType) {
        this.projectId = projectId;
        this.moduleId = moduleId;
        this.projectModuleId = projectModuleId;
        this.positionX = positionX;
        this.positionY = positionY;
        this.angle = angle;
        this.actionType = actionType;
        this.timestamp = LocalDateTime.now();
    }
}
