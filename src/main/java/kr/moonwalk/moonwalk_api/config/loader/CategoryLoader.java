package kr.moonwalk.moonwalk_api.config.loader;

import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class CategoryLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    @Override
    public void run(String... args) throws Exception {

        if (categoryRepository.count() == 0) {
            String[] officeCategory = {"업무공간", "회의공간", "커뮤니티 공간", "시설공간", "기타공간"};

            for (String categoryName : officeCategory) {
                Category category = new Category(categoryName, null, Type.TYPE_OFFICE);
                categoryRepository.save(category);
            }

            String[] moduleCategory = {"공용공간", "사무공간", "출입공간", "회의공간"};

            for (String categoryName : moduleCategory) {
                Category category = new Category(categoryName, null, Type.TYPE_MODULE);
                categoryRepository.save(category);
            }
        }
    }
}
