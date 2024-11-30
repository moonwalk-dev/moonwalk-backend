package kr.moonwalk.moonwalk_api.config.loader;

import java.util.List;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(3)
public class ModuleLoader implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (moduleRepository.count() == 0) {

            Category sharedSpaceCategory = categoryRepository.findByNameAndType("공용공간", Type.TYPE_MODULE).orElseThrow();
            Category officeSpaceCategory = categoryRepository.findByNameAndType("사무공간", Type.TYPE_MODULE).orElseThrow();
            Category accessSpaceCategory = categoryRepository.findByNameAndType("출입공간", Type.TYPE_MODULE).orElseThrow();
            Category meetingSpaceCategory = categoryRepository.findByNameAndType("회의공간", Type.TYPE_MODULE).orElseThrow();

            List<Module> modules = List.of(
                new Module("기본 데스크", "사무 업무에 적합한 기본 데스크", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M123456", 1, sharedSpaceCategory),
                new Module("프리미엄 회의 테이블", "고급 회의용 테이블", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M654321", 8, officeSpaceCategory),
                new Module("라운지 소파", "편안한 라운지 소파", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M789012", 5, accessSpaceCategory),

                new Module("스탠딩 데스크", "건강을 위한 스탠딩 데스크", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M321456", 1, sharedSpaceCategory),
                new Module("회의실 화이트보드", "회의실 전용 대형 화이트보드", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M654789", 10, meetingSpaceCategory),
                new Module("출입구 카운터", "출입구 관리용 카운터", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M987654", 3, accessSpaceCategory),
                new Module("이동식 의자", "다양한 공간에서 사용 가능한 이동식 의자", 500, 100, 50000, List.of("바닥: 데코타", "벽: 인테리어 필름"), "M456123", 20, sharedSpaceCategory)
            );

            moduleRepository.saveAll(modules);
        }
    }
}
