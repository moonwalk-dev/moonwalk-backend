package kr.moonwalk.moonwalk_api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "projects")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long id;

    private String title;
    private int totalPrice;
    private String client;
    private String area;
    private LocalDateTime createdAt;

    @OneToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inventory> inventories = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectModule> projectModules = new ArrayList<>();

    public static Project createFromEstimate(Estimate estimate, User user, String title, String client, String area) {
        Project project = new Project();
        project.title = title;
        project.client = client;
        project.area = area;
        project.createdAt = LocalDateTime.now();
        project.user = user;

        int total = 0;
        for (Cart cart : estimate.getCarts()) {
            Inventory inventory = Inventory.createFromCart(cart, project);
            project.inventories.add(inventory);

//            ProjectModule projectModule = ProjectModule.createFromInventory(inventory, project);
//            project.projectModules.add(projectModule);

//            total += cart.getModule().getPrice() * cart.getQuantity();
        }
        project.totalPrice = 0;

        return project;
    }
}