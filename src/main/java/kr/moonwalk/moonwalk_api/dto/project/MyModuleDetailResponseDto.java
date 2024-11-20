package kr.moonwalk.moonwalk_api.dto.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MyModuleDetailResponseDto {

    private Long id;

    private Long projectId;

    private Long moduleId;

    private String mainCategory;

    private String subCategory;

    private String moduleName;

    private int moduleCapacity;

    private String moduleSerialNumber;

    private String moduleIsoImageUrl;

    private String moduleSize;

    private String moduleMaterial;

    private int quantity;

    private int price;
}
