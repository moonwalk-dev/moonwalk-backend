package kr.moonwalk.moonwalk_api.dto.module;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModuleSaveResponseDto {

    private Long id;

    private String name;

    private String serialNumber;

    private int capacity;

    private Long categoryId;
}
