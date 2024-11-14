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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public static ProjectModule createFromInventory(Inventory inventory, Project project, int positionX, int positionY) {
        ProjectModule projectModule = new ProjectModule();
        projectModule.module = inventory.getModule();
        projectModule.project = project;
        projectModule.positionX = positionX;
        projectModule.positionY = positionY;
        projectModule.angle = 0;

        return projectModule;
    }
}
