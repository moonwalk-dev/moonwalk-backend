package kr.moonwalk.moonwalk_api.config.loader;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Guide;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.GuideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class GuideLoader implements CommandLineRunner {

    private final GuideRepository guideRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (guideRepository.count() == 0) {
            List<String> categoryNames = List.of("업무공간", "회의공간", "커뮤니티 공간", "시설공간", "기타공간");
            Map<String, Category> categories = categoryNames.stream()
                .map(categoryRepository::findByName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toMap(Category::getName, category -> category));

            for (String categoryName : categoryNames) {
                if (categories.get(categoryName) == null) {
                    throw new IllegalStateException(categoryName + " 카테고리가 존재하지 않습니다.");
                }
            }

            List<Guide> guides = List.of(
                new Guide("핫데스크", "하나의 데스크를 여러 명이 각기 다른 시간에 임시로 사용하는 조직적 업무공간 시스템", List.of("협업", "소통", "자율좌석"), categories.get("업무공간")),
                new Guide("폰부스", "개인, 업무 통화를 위한 독립된 부스형 공간. 외부로 소음이 세어나가지 않도록 흡음재, 차음도어 등 사용", List.of("방음", "1인"), categories.get("업무공간")),
                new Guide("포커스존(부스형)", "1인 좌석과 간이 데스크로 구성된 프라이빗한 업무 공간. 외부로 소음이 세어나가지 않도록 흡음재, 차음도어 등 사용", List.of("집중", "1인업무", "방음"), categories.get("업무공간")),
                new Guide("포커스존(개방형)", "1인 좌석과 간이 데스크로 구성된 오픈형 이동식 업무 공간", List.of("집중", "1인업무"), categories.get("업무공간")),
                new Guide("어라운드존", "창가나 가장자리 부분에 바타입 형태의 집중 업무공간", List.of("집중", "자율 자석"), categories.get("업무공간")),
                new Guide("모션데스크존", "모션데스크를 활용한 개인 맞춤형 업무공간", List.of("집중", "1인업무", "높낮이 조절"), categories.get("업무공간")),

                new Guide("가변형 회의실", "평소에는 별도의 회의실로 사용하다가 필요시 하나의 회의공간으로 사용할 수 있는 가변 공간. 주로 폴딩도어나 무빙월을 활용한다", List.of("가변공간", "회의실", "다용도"), categories.get("회의공간")),
                new Guide("퀵미팅존(부스형)", "가벼운 대화를 나눌 수 있는 부스형 미팅존", List.of("협업", "퀵미팅"), categories.get("회의공간")),
                new Guide("퀵미팅존(개방형)", "4인 정도의 인원이 빠르게 협업할 수 있는 개방형 부스", List.of("협업", "퀵미팅"), categories.get("회의공간")),
                new Guide("브레인스토밍룸", "상석이 없는 원형 테이블에서 자유롭게 브레인스토밍할 수 있는 회의실", List.of("브레인스토밍", "원형테이블", "수평적"), categories.get("회의공간")),
                new Guide("허들룸", "2~4인이 이용할 수 있는 소규모 회의실로 예약없이 단시간내 빠르게 아이디어 교환이 가능한 공간", List.of("퀵미팅", "소규모", "협업"), categories.get("회의공간")),
                new Guide("세미나룸(강연장)", "대형스크린, 멀티미디어 장비, 좌석이 갖춰진 공간으로, 사내 행사 및 교육을 위한 목적으로 조성", List.of("방음", "스크린", "강당"), categories.get("회의공간")),

                new Guide("인포메이션", "기업을 처음 맞이하는 공간으로", List.of("안내", "메인이미지월"), categories.get("커뮤니티 공간")),
                new Guide("워크 라운지", "하이브리드 근무 및 협업이 많은 기업에 필요한 자유로운 형태의 라운지형 업무공간", List.of("자율좌석", "개인업무"), categories.get("커뮤니티 공간")),
                new Guide("카페라운지", "소파, 의자, 테이블, 캔틴 등을 배치해 편안하게 휴식을 취할 수 있는 라운지", List.of("스몰토크", "캐주얼미팅", "휴식"), categories.get("커뮤니티 공간")),
                new Guide("타운홀 라운지", "대형 스크린, 계단식 좌석 등 오픈된 라운지 공간에 다양한 행사 및 활동 진행", List.of("소통", "세미나", "워크숍"), categories.get("커뮤니티 공간")),

                new Guide("OA존", "프린터, 복합기, 문서분쇄기를 사용하는 독립된 공간,", List.of("인쇄", "복사"), categories.get("시설공간")),
                new Guide("라커존", "개인소지품 및 외투등을 보관할 수 있는 공간. IoT스마트 솔루션을 도입한 스마트라커 도입이 가능하다", List.of("수납", "보안"), categories.get("시설공간")),
                new Guide("탕비실", "싱크대와 냉장고, 정수기 등 물을 끓이거나 그릇을 세척할 수 있도록 마련된 중소규모 공간", List.of("싱크", "물", "음식"), categories.get("시설공간")),
                new Guide("서버룸", "별도의 항온항습이 필요한 서버 관리가 필요한 경우 별도의 실을 구성하여 운영", List.of("서버관리", "항온항습"), categories.get("시설공간")),
                new Guide("창고", "각종 물품들을 보관할 수 있는 필수 공간", List.of("물품보관"), categories.get("시설공간")),

                new Guide("스튜디오", "기업의 제품이나 컨텐츠를 자체 촬영할 수 있는 공간. 촬영장비, 소품 등을 보관할 수 있는 창고도 함께 구축할 수 있다", List.of("촬영", "방음"), categories.get("기타공간")),
                new Guide("홍보 전시관", "기업의 연혁 및 소개할 수 있는 홍보 전시공간", List.of("연혁", "아이덴티티"), categories.get("기타공간")),
                new Guide("릴렉스룸", "안마의자 또는 리클라이너가 배치되어 편안하게 휴식을 취할 수 있는 공간", List.of("휴식", "방음", "1인"), categories.get("기타공간")),
                new Guide("게이밍존", "동료들과 간단한 스포츠 활동이나 게임을 즐길 수 있는 공간", List.of("휴식", "교류", "게임"), categories.get("기타공간")),
                new Guide("멀티미디어룸", "편안한 좌석과 멀티미디어 설비가 갖춰진 직원 복지 공간", List.of("휴식", "방음", "엔터테인먼트"), categories.get("기타공간"))
            );

            guideRepository.saveAll(guides);
        }
    }
}
