package kr.moonwalk.moonwalk_api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.dto.space.CategoriesSpacesResponseDto;
import kr.moonwalk.moonwalk_api.dto.space.CategoriesSpacesDto;
import kr.moonwalk.moonwalk_api.dto.space.SpaceDto;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.SpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpaceService {

    private final SpaceRepository spaceRepository;
    private final CategoryRepository categoryRepository;

    public CategoriesSpacesResponseDto getSpacesByCategories(List<String> categoryNames) {
        List<CategoriesSpacesDto> categorySpacesList = new ArrayList<>();

        for (String categoryName : categoryNames) {
            Category category = categoryRepository.findByName(categoryName).orElse(null);
            if (category != null) {
                List<SpaceDto> spaceDtos = spaceRepository.findByCategory(category).stream().map(
                    space -> new SpaceDto(space.getName(), space.getDescription(),
                        space.getKeyword(), space.getImagePath())).collect(Collectors.toList());

                categorySpacesList.add(new CategoriesSpacesDto(category.getName(), spaceDtos));
            }
        }

        return new CategoriesSpacesResponseDto(categorySpacesList);
    }
}
