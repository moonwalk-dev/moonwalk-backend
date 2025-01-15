package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private int estimatedTotalPrice;
    private int placedTotalPrice;
    private String client;
    private String area;
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String canvas;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MyModule> myModules = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectModule> projectModules = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectPlacementHistory> histories = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "blueprint_image_id")
    private Image blueprintImage;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_image_id")
    private Image coverImage;

    public Project(User user, String title, String client, String area) {

        this.createdAt = LocalDateTime.now();
        this.user = user;
        user.getProjects().add(this);

        this.title = title;
        this.client = client;
        this.area = area;
        this.placedTotalPrice = 0;
        this.canvas = null;
    }

    public Project(Estimate estimate, User user) {

        this.createdAt = LocalDateTime.now();
        this.user = user;
        user.getProjects().add(this);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.title = "Project - " + this.createdAt.format(formatter);
        this.placedTotalPrice = 0;
        this.canvas = null;

        for (Cart cart : estimate.getCarts()) {
            MyModule myModule = MyModule.createFromCart(cart, this);
            this.myModules.add(myModule);
        }
        updateEstimatedTotalPrice();
    }

    public void setBlueprintImage(Image blueprintImage) {
        this.blueprintImage = blueprintImage;
    }

    public void updateEstimatedTotalPrice() {
        this.estimatedTotalPrice = myModules.stream()
            .mapToInt(myModule -> myModule.getModule().getPrice() * myModule.getQuantity())
            .sum();
    }

    public void updatePlacedTotalPrice() {
        this.placedTotalPrice = myModules.stream()
            .mapToInt(myModule -> myModule.getModule().getPrice() * myModule.getUsedQuantity())
            .sum();
    }

    public void save(String title, String client, String area) {
        this.title = title;
        this.client = client;
        this.area = area;
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }

    public void addHistory(ProjectPlacementHistory projectPlacementHistory) {
        this.getHistories().add(projectPlacementHistory);
    }

    public void setCanvas(String canvas) {
        this.canvas = canvas;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public void setArea(String area) {
        this.area = area;
    }
}