package kr.moonwalk.moonwalk_api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "modules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Long id;

    private String name;

    private String description;

    private String size;

    private int price;

    private String material;

    private String serialNumber;

    private int capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "iso_image_id")
    private Image isoImage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "top_image_id")
    private Image topImage;

    public Module(String name, String description, String size, int price, String material,
        String serialNumber, int capacity, Category category, Image isoImage, Image topImage) {
        this.name = name;
        this.description = description;
        this.size = size;
        this.price = price;
        this.material = material;
        this.serialNumber = serialNumber;
        this.capacity = capacity;
        this.category = category;
        this.isoImage = isoImage;
        this.topImage = topImage;
    }
}
