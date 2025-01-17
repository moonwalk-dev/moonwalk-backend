package kr.moonwalk.moonwalk_api.dto.module;

import java.util.List;
import java.text.NumberFormat;
import lombok.Getter;

@Getter
public class ModuleResponseDto {
    private Long id;
    private String name;
    private String description;
    private String size;
    private String price;
    private List<String> materials;
    private String serialNumber;
    private int capacity;
    private String topImageUrl;
    private String isoImageUrl;

    public ModuleResponseDto(Long id, String name, String description, String size, 
            int price, List<String> materials, String serialNumber, 
            int capacity, String topImageUrl, String isoImageUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.size = size;
        this.price = formatPrice(price);
        this.materials = materials;
        this.serialNumber = serialNumber;
        this.capacity = capacity;
        this.topImageUrl = topImageUrl;
        this.isoImageUrl = isoImageUrl;
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance().format(price);
    }
}
