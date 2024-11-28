package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_module_id")
    private ProjectModule projectModule;

    private int positionX;
    private int positionY;
    private int angle;

    private String actionType;
    private LocalDateTime timestamp;

    public ProjectPlacementHistory(Project project, Module module, ProjectModule projectModule, int positionX, int positionY, int angle, String actionType) {
        this.project = project;
        project.getHistories().add(this);
        this.module = module;
        this.projectModule = projectModule;
        this.positionX = positionX;
        this.positionY = positionY;
        this.angle = angle;
        this.actionType = actionType;
        this.timestamp = LocalDateTime.now();
    }
}
