package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
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
@Table(name = "moods")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mood {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "cover_image_id")
    private Image coverImage;

    @OneToMany(mappedBy = "mood", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> detailImages = new ArrayList<>();

    public Mood(String name, String description, Image coverImage) {
        this.name = name;
        this.description = description;
        this.coverImage = coverImage;
    }

    public void addDetailImages(List<Image> images) {
        images.forEach(image -> {
            image.setMood(this);
            this.detailImages.add(image);
        });
    }
}
