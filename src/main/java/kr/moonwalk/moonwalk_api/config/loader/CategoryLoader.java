package kr.moonwalk.moonwalk_api.config.loader;

import java.util.ArrayList;
import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(2)
public class CategoryLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    @Override
    @Transactional
    public void run(String... args) throws Exception {

        if (categoryRepository.count() == 0) {
            String[] officeCategory = {"업무공간", "회의공간", "커뮤니티 공간", "시설공간", "기타공간"};

            for (String categoryName : officeCategory) {
                Category category = new Category(categoryName, null, Type.TYPE_OFFICE);
                categoryRepository.save(category);
            }

            String[] moduleCategory = {"공용공간", "사무공간", "출입공간", "회의공간"};

            List<Category> parentCategoryList = new ArrayList<>();
            for (String categoryName : moduleCategory) {
                Category category = new Category(categoryName, null, Type.TYPE_MODULE);
                parentCategoryList.add(category);
            }
            categoryRepository.saveAll(parentCategoryList);

            List<Category> subCategoryList = new ArrayList<>();

            subCategoryList.add(new Category("라운지", parentCategoryList.get(0), Type.TYPE_MODULE));
            subCategoryList.add(new Category("락커룸", parentCategoryList.get(0), Type.TYPE_MODULE));
            subCategoryList.add(new Category("팬트리", parentCategoryList.get(0), Type.TYPE_MODULE));
            subCategoryList.add(new Category("휴게공간", parentCategoryList.get(0), Type.TYPE_MODULE));

            subCategoryList.add(new Category("업무공간", parentCategoryList.get(1), Type.TYPE_MODULE));
            subCategoryList.add(new Category("임원실", parentCategoryList.get(1), Type.TYPE_MODULE));
            subCategoryList.add(new Category("집중업무부스", parentCategoryList.get(1), Type.TYPE_MODULE));
            subCategoryList.add(new Category("OA공간", parentCategoryList.get(1), Type.TYPE_MODULE));

            subCategoryList.add(new Category("이미지월", parentCategoryList.get(2), Type.TYPE_MODULE));
            subCategoryList.add(new Category("인포메이션", parentCategoryList.get(2), Type.TYPE_MODULE));

            subCategoryList.add(new Category("대회의실", parentCategoryList.get(3), Type.TYPE_MODULE));
            subCategoryList.add(new Category("중회의실", parentCategoryList.get(3), Type.TYPE_MODULE));
            subCategoryList.add(new Category("소회의실", parentCategoryList.get(3), Type.TYPE_MODULE));
            subCategoryList.add(new Category("퀵미팅부스", parentCategoryList.get(3), Type.TYPE_MODULE));

            categoryRepository.saveAll(subCategoryList);
        }
    }
}
