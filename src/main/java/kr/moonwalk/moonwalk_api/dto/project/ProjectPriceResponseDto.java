package kr.moonwalk.moonwalk_api.dto.project;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectPriceResponseDto {

    private int placedTotalPrice;
    private int estimatedTotalPrice;
    private List<CategoryPriceResponseDto> categoryPrice;

    @Getter
    @AllArgsConstructor
    public static class CategoryPriceResponseDto {

        private String categoryName;
        private int totalPrice;
        private int modulesCount;
        private List<ModuleDetail> modules;

        @Getter
        @AllArgsConstructor
        public static class ModuleDetail {
            private String moduleName;
            private int modulePrice;
        }
    }
}
