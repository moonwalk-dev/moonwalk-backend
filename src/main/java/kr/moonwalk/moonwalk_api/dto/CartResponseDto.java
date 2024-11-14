package kr.moonwalk.moonwalk_api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CartResponseDto {

    private Long id;

    private Long estimateId;

    private Long moduleId;

    private String moduleName;

    private int moduleCapacity;

    private String moduleSerialNumber;

    private String moduleIsoImageUrl;

    private int quantity;
}
