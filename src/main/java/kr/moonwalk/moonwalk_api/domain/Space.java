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
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "spaces")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Space {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "space_id")
    private Long id;

    private String name;

    private String description;

    private String keyword;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "cover_image_id")
    private Image coverImage;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "space_id")
    private List<Image> detailImages = new ArrayList<>();


    public Space(String name, String description, String keyword, Category category) {
        this.name = name;
        this.description = description;
        this.keyword = keyword;
        this.category = category;
    }
}
