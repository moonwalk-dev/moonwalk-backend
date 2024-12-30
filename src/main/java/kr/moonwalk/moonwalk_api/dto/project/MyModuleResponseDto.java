package kr.moonwalk.moonwalk_api.dto.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyModuleResponseDto {

    private Long id;

    private Long projectId;

    private Long moduleId;

    private String moduleName;

    private int moduleCapacity;

    private String moduleSerialNumber;

    private String moduleIsoImageUrl;

    private int quantity;

    private String category;

    private String parentCategory;

    private String moduleSize;

    private List<String> moduleMaterials;

    private int modulePrice;
}
