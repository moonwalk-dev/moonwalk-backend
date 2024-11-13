package kr.moonwalk.moonwalk_api.dto.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleResponseDto {

    private Long id;

    private String name;

    private String description;

    private String size;

    private int price;

    private String material;

    private String serialNumber;

    private int capacity;

    private String topImageUrl;

    private String isoImageUrl;

}
