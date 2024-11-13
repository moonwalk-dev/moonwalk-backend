package kr.moonwalk.moonwalk_api.config.loader;

import java.util.List;

import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ImageRepository;
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
    private final ImageRepository imageRepository;

    @Override
    public void run(String... args) throws Exception {
        if (moduleRepository.count() == 0) {

            Category workspace = categoryRepository.findByName("업무공간").orElseThrow();
            Category meetingSpace = categoryRepository.findByName("회의공간").orElseThrow();
            Category communitySpace = categoryRepository.findByName("커뮤니티 공간").orElseThrow();

            Image isoImage1 = imageRepository.save(new Image("iso_image_url_1.jpg"));
            Image topImage1 = imageRepository.save(new Image("top_image_url_1.jpg"));
            Image isoImage2 = imageRepository.save(new Image("iso_image_url_2.jpg"));
            Image topImage2 = imageRepository.save(new Image("top_image_url_2.jpg"));
            Image isoImage3 = imageRepository.save(new Image("iso_image_url_3.jpg"));
            Image topImage3 = imageRepository.save(new Image("top_image_url_3.jpg"));

            List<Module> modules = List.of(
                new Module("기본 데스크", "사무 업무에 적합한 기본 데스크", "Medium", 50000, "나무", "M123456", 1, workspace, isoImage1, topImage1),
                new Module("프리미엄 회의 테이블", "고급 회의용 테이블", "Large", 200000, "유리", "M654321", 8, meetingSpace, isoImage2, topImage2),
                new Module("라운지 소파", "편안한 라운지 소파", "Large", 150000, "가죽", "M789012", 5, communitySpace, isoImage3, topImage3)
            );

            moduleRepository.saveAll(modules);
        }
    }
}
