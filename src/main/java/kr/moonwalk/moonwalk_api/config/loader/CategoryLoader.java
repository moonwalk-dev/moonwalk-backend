package kr.moonwalk.moonwalk_api.config.loader;

import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(1)
public class CategoryLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    @Override
    public void run(String... args) throws Exception {

        String[] parentCategory = {"업무공간", "회의공간", "커뮤니티 공간", "시설공간", "기타공간"};

        if (categoryRepository.count() == 0) {
            for (String categoryName : parentCategory) {
                Category category = new Category(categoryName, null);
                categoryRepository.save(category);
            }

        }
    }
}
