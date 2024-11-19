package kr.moonwalk.moonwalk_api.dto.project;

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
}
