package kr.moonwalk.moonwalk_api.dto.module;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleSaveDto {

    private String name;

    private String description;

    private Integer width;

    private Integer height;

    private Integer price;

    private List<String> materials;

    private String serialNumber;

    private Integer capacity;

    private Long categoryId;
}
