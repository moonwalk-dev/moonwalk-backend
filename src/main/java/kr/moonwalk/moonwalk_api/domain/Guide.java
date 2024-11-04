package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "guides")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Long id;

    private String name;

    private String description;

    @ElementCollection
    @Column(name = "keyword")
    private List<String> keywords = new ArrayList<>();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "cover_image_id")
    private Image coverImage;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> detailImages = new ArrayList<>();

    public Guide(String name, String description, List<String> keywords, Category category) {
        this.name = name;
        this.description = description;
        this.keywords = keywords;
        this.category = category;
    }

    public void addDetailImages(List<Image> images) {
        images.forEach(image -> image.setGuide(this));
        this.detailImages.addAll(images);
    }

    public void setCoverImage(Image coverImage) {
        this.coverImage = coverImage;
    }
}
