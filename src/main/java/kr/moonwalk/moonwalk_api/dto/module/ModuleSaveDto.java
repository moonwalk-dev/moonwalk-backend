package kr.moonwalk.moonwalk_api.dto.module;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleSaveDto {

    private String name;

    private String description;

    private int width;

    private int height;

    private int price;

    private List<String> materials;

    private String serialNumber;

    private int capacity;

    private Long categoryId;
}
