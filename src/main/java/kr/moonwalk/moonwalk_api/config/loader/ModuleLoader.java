package kr.moonwalk.moonwalk_api.config.loader;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(3)
public class ModuleLoader implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (moduleRepository.count() == 0) {

            Category sharedSpaceCategory = categoryRepository.findByNameAndType("공용공간", Type.TYPE_MODULE).orElseThrow();
            Category officeSpaceCategory = categoryRepository.findByNameAndType("사무공간", Type.TYPE_MODULE).orElseThrow();
            Category accessSpaceCategory = categoryRepository.findByNameAndType("출입공간", Type.TYPE_MODULE).orElseThrow();
            Category meetingSpaceCategory = categoryRepository.findByNameAndType("회의공간", Type.TYPE_MODULE).orElseThrow();

            List<Module> modules = List.of(
                new Module("기본 데스크", "사무 업무에 적합한 기본 데스크", "Medium", 50000, "나무", "M123456", 1, sharedSpaceCategory,
                    new Image("iso_image_url_1.jpg"), new Image("top_image_url_1.jpg")),
                new Module("프리미엄 회의 테이블", "고급 회의용 테이블", "Large", 200000, "유리", "M654321", 8, officeSpaceCategory,
                    new Image("iso_image_url_2.jpg"), new Image("top_image_url_2.jpg")),
                new Module("라운지 소파", "편안한 라운지 소파", "Large", 150000, "가죽", "M789012", 5, accessSpaceCategory,
                    new Image("iso_image_url_3.jpg"), new Image("top_image_url_3.jpg")),

                new Module("스탠딩 데스크", "건강을 위한 스탠딩 데스크", "Medium", 120000, "대나무", "M321456", 1, sharedSpaceCategory,
                    new Image("iso_image_url_4.jpg"), new Image("top_image_url_4.jpg")),
                new Module("회의실 화이트보드", "회의실 전용 대형 화이트보드", "Small", 70000, "메탈", "M654789", 10, meetingSpaceCategory,
                    new Image("iso_image_url_5.jpg"), new Image("top_image_url_5.jpg")),
                new Module("출입구 카운터", "출입구 관리용 카운터", "Large", 250000, "합성목재", "M987654", 3, accessSpaceCategory,
                    new Image("iso_image_url_6.jpg"), new Image("top_image_url_6.jpg")),
                new Module("이동식 의자", "다양한 공간에서 사용 가능한 이동식 의자", "Small", 30000, "플라스틱", "M456123", 20, sharedSpaceCategory,
                    new Image("iso_image_url_7.jpg"), new Image("top_image_url_7.jpg"))
            );

            moduleRepository.saveAll(modules);
        }
    }
}
