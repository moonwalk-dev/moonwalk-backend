package kr.moonwalk.moonwalk_api.dto.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryModuleDto {

    private Long id;

    private String name;

    private int capacity;

    private String serialNumber;

    private String coverImage;

}
