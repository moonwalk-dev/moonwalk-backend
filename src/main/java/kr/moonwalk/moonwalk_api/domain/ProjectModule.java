package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "project_modules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProjectModule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int positionX;
    private int positionY;
    private int angle;

    private boolean isDeleted = false;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public static ProjectModule createMyModule(MyModule myModule, Project project, int positionX, int positionY, int angle) {
        ProjectModule projectModule = new ProjectModule();
        projectModule.module = myModule.getModule();
        projectModule.project = project;
        projectModule.positionX = positionX;
        projectModule.positionY = positionY;
        projectModule.angle = angle;

        project.getProjectModules().add(projectModule);

        return projectModule;
    }

    public void updatePosition(int positionX, int positionY, int angle) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.angle = angle;
    }

    public void markAsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
