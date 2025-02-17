package kr.moonwalk.moonwalk_api.dto.module;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.dto.ImageDto;
import lombok.Getter;

@Getter
public class ModuleResponseDto {
    private Long id;
    private String name;
    private String description;
    private String size;
    private int price;
    private List<String> materials;
    private String serialNumber;
    private int capacity;
    private ImageDto topImage;
    private ImageDto isoImage;

    public ModuleResponseDto(Long id, String name, String description, String size,
        int price, List<String> materials, String serialNumber,
        int capacity, Image topImage, Image isoImage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.price = price;
        this.materials = materials;
        this.serialNumber = serialNumber;
        this.capacity = capacity;
        this.topImage = topImage != null ? new ImageDto(topImage.getId(), topImage.getImageUrl()) : null;
        this.isoImage = isoImage != null ? new ImageDto(isoImage.getId(), isoImage.getImageUrl()) : null;
    }

}