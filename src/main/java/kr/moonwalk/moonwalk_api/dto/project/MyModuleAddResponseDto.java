package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyModuleAddResponseDto {


    private Long moduleId;

    private String moduleName;

    private String category;

    private String parentCategory;

    private Long projectId;

    private int quantity;

    private int usedQuantity;
}
