package kr.moonwalk.moonwalk_api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    private int width;

    private int height;

    private int price;

    @ElementCollection
    @Column(name = "material")
    private List<String> materials = new ArrayList<>();

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

    public Module(String name, String description, int width, int height, int price, List<String>  materials,
        String serialNumber, int capacity, Category category) {
        this.name = name;
        this.description = description;
        this.width = width;
        this.height = height;
        this.price = price;
        this.materials = materials;
        this.serialNumber = serialNumber;
        this.capacity = capacity;
        this.category = category;
    }

    public void setTopImage(Image topImage) {
        this.topImage = topImage;
    }

    public void setIsoImage(Image isoImage) {
        this.isoImage = isoImage;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateDescription(String description) {
        this.description = description;
    }

    public void updateWidth(int width) {
        this.width = width;
    }

    public void updateHeight(int height) {
        this.height = height;
    }

    public void updatePrice(int price) {
        this.price = price;
    }

    public void updateMaterials(List<String> materials) {
        this.materials = materials;
    }

    public void updateSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public void updateCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void updateCategory(Category category) {
        this.category = category;
    }
}
