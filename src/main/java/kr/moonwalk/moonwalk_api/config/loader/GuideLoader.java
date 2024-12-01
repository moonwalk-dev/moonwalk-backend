package kr.moonwalk.moonwalk_api.config.loader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Category;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Guide;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.GuideRepository;
import kr.moonwalk.moonwalk_api.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(4)
public class GuideLoader implements CommandLineRunner {

    private final GuideRepository guideRepository;
    private final CategoryRepository categoryRepository;
    private final AmazonS3 amazonS3;
    private final ImageRepository imageRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (guideRepository.count() == 0) {

            String bucketName = "moonwalk-project";
            String baseFolder = "guides/";

            Category businessSpaceCategory = categoryRepository.findByNameAndType("업무공간", Type.TYPE_OFFICE).orElseThrow();
            Category conferenceSpaceCategory = categoryRepository.findByNameAndType("회의공간", Type.TYPE_OFFICE).orElseThrow();
            Category communitySpaceCategory = categoryRepository.findByNameAndType("커뮤니티 공간", Type.TYPE_OFFICE).orElseThrow();
            Category facilitySpaceCategory = categoryRepository.findByNameAndType("시설공간", Type.TYPE_OFFICE).orElseThrow();
            Category miscellaneousSpaceCategory = categoryRepository.findByNameAndType("기타공간", Type.TYPE_OFFICE).orElseThrow();

            List<Guide> guides = List.of(
                // 업무공간
                createGuide("핫데스크", "하나의 데스크를 여러 명이 각기 다른 시간에 임시로 사용하는 조직적 업무공간 시스템",
                    List.of("협업", "소통", "자율좌석"), "hotdesk", bucketName, baseFolder, businessSpaceCategory),
                createGuide("폰부스", "개인, 업무 통화를 위한 독립된 부스형 공간. 외부로 소음이 세어나가지 않도록 흡음재, 차음도어 등 사용",
                    List.of("방음", "1인"), "phonebooth", bucketName, baseFolder, businessSpaceCategory),
                createGuide("포커스존(부스형)", "1인 좌석과 간이 데스크로 구성된 프라이빗한 업무 공간. 외부로 소음이 세어나가지 않도록 흡음재, 차음도어 등 사용",
                    List.of("집중", "1인업무", "방음"), "focuszone_booth", bucketName, baseFolder, businessSpaceCategory),
                createGuide("포커스존(개방형)", "1인 좌석과 간이 데스크로 구성된 오픈형 이동식 업무 공간",
                    List.of("집중", "1인업무"), "focuszone_open", bucketName, baseFolder, businessSpaceCategory),
                createGuide("어라운드존", "창가나 가장자리 부분에 바타입 형태의 집중 업무공간",
                    List.of("집중", "자율 자석"), "aroundzone", bucketName, baseFolder, businessSpaceCategory),
                createGuide("모션데스크존", "모션데스크를 활용한 개인 맞춤형 업무공간",
                    List.of("집중", "1인업무", "높낮이 조절"), "motiondesk", bucketName, baseFolder, businessSpaceCategory),
                createGuide("콜라보레이션 허브", "다른 팀이나 기업의 직원들이 한 시설과 서비스를 공유하는 공간",
                    List.of("원격 근무", "온라인 협업체계 한계 해결", "공유오피스"), "collaboration_hub", bucketName, baseFolder, businessSpaceCategory),
                createGuide("코워킹공간", "기업의 직원들을 한 곳에 모아 팀워크, 창의성, 자기개발을 돕는 공간",
                    List.of("개인", "생산성 향상", "공유공간"), "coworking_space", bucketName, baseFolder, businessSpaceCategory),
                createGuide("OA부스", "프린터, 복합기, 문서분쇄기를 사용하는 독립된 공간",
                    List.of("프린트", "업무 보조", "출력", "기자재", "문구"), "oa_booth", bucketName, baseFolder, businessSpaceCategory),
                createGuide("이동형 OA부스", "컴팩트한 사이즈에 바퀴가 달린 OA부스를 통해 자유롭게 이동 가능",
                    List.of("이동식", "자유로움", "재배치"), "mobile_oa_booth", bucketName, baseFolder, businessSpaceCategory),

                // 회의공간
                createGuide("가변형 회의실", "평소에는 별도의 회의실로 사용하다가 필요시 하나의 회의공간으로 사용할 수 있는 가변 공간. 주로 폴딩도어나 무빙월을 활용한다",
                    List.of("가변공간", "회의실", "다용도"), "flexible_meetingroom", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("퀵미팅존(부스형)", "가벼운 대화를 나눌 수 있는 부스형 미팅존",
                    List.of("협업", "퀵미팅"), "quickmeeting_booth", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("퀵미팅존(개방형)", "4인 정도의 인원이 빠르게 협업할 수 있는 개방형 부스",
                    List.of("협업", "퀵미팅"), "quickmeeting_open", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("브레인스토밍룸", "상석이 없는 원형 테이블에서 자유롭게 브레인스토밍할 수 있는 회의실",
                    List.of("브레인스토밍", "원형테이블", "수평적"), "brainstorming_room", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("허들룸", "2~4인이 이용할 수 있는 소규모 회의실로 예약없이 단시간내 빠르게 아이디어 교환이 가능한 공간",
                    List.of("퀵미팅", "소규모", "협업"), "huddle_room", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("세미나룸(강연장)", "대형스크린, 멀티미디어 장비, 좌석이 갖춰진 공간으로, 사내 행사 및 교육을 위한 목적으로 조성",
                    List.of("방음", "스크린", "강당"), "seminar_room", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("얼라인 회의실", "평소에는 2개의 회의실로 사용하다 용도에 따라 넓은 공간으로 변경 가능",
                    List.of("가변공간", "회의실", "다용도"), "align_meetingroom", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("스피어팟", "비대면 협업에 최적화된 조명, 카메라, 모니터 배치",
                    List.of("비대면 회의", "1인 회의실", "단독 공간"), "sphere_pod", bucketName, baseFolder, conferenceSpaceCategory),
                createGuide("티키타카 회의실", "상석이 없는 원형 테이블에서 자유롭게 브레인스토밍할 수 있는 공간",
                    List.of("브레인스토밍", "원형테이블", "수평적 관계"), "tikitaka_meetingroom", bucketName, baseFolder, conferenceSpaceCategory),

                // 커뮤니티 공간
                createGuide("인포메이션", "기업을 처음 맞이하는 공간으로",
                    List.of("안내", "메인이미지월"), "information", bucketName, baseFolder, communitySpaceCategory),
                createGuide("워크 라운지", "하이브리드 근무 및 협업이 많은 기업에 필요한 자유로운 형태의 라운지형 업무공간",
                    List.of("자율좌석", "개인업무"), "work_lounge", bucketName, baseFolder, communitySpaceCategory),
                createGuide("카페라운지", "소파, 의자, 테이블, 캔틴 등을 배치해 편안하게 휴식을 취할 수 있는 라운지",
                    List.of("스몰토크", "캐주얼미팅", "휴식"), "cafe_lounge", bucketName, baseFolder, communitySpaceCategory),
                createGuide("타운홀 라운지", "대형 스크린, 계단식 좌석 등 오픈된 라운지 공간에 다양한 행사 및 활동 진행",
                    List.of("소통", "세미나", "워크숍"), "townhall_lounge", bucketName, baseFolder, communitySpaceCategory),

                // 시설 공간
                createGuide("OA존", "프린터, 복합기, 문서분쇄기를 사용하는 독립된 공간,",
                    List.of("인쇄", "복사"), "oa_zone", bucketName, baseFolder, facilitySpaceCategory),
                createGuide("라커존", "개인소지품 및 외투등을 보관할 수 있는 공간. IoT스마트 솔루션을 도입한 스마트라커 도입이 가능하다",
                    List.of("수납", "보안"), "locker_zone", bucketName, baseFolder, facilitySpaceCategory),
                createGuide("탕비실", "싱크대와 냉장고, 정수기 등 물을 끓이거나 그릇을 세척할 수 있도록 마련된 중소규모 공간",
                    List.of("싱크", "물", "음식"), "pantry", bucketName, baseFolder, facilitySpaceCategory),
                createGuide("서버룸", "별도의 항온항습이 필요한 서버 관리가 필요한 경우 별도의 실을 구성하여 운영",
                    List.of("서버관리", "항온항습"), "server_room", bucketName, baseFolder, facilitySpaceCategory),
                createGuide("창고", "각종 물품들을 보관할 수 있는 필수 공간",
                    List.of("물품보관"), "storage", bucketName, baseFolder, facilitySpaceCategory),

                // 기타 공간
                createGuide("스튜디오", "기업의 제품이나 컨텐츠를 자체 촬영할 수 있는 공간. 촬영장비, 소품 등을 보관할 수 있는 창고도 함께 구축할 수 있다",
                    List.of("촬영", "방음"), "studio", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("홍보 전시관", "기업의 연혁 및 소개할 수 있는 홍보 전시공간",
                    List.of("연혁", "아이덴티티"), "exhibition_hall", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("릴렉스룸", "안마의자 또는 리클라이너가 배치되어 편안하게 휴식을 취할 수 있는 공간",
                    List.of("휴식", "방음", "1인"), "relax_room", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("게이밍존", "동료들과 간단한 스포츠 활동이나 게임을 즐길 수 있는 공간",
                    List.of("휴식", "교류", "게임"), "gaming_zone", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("멀티미디어룸", "편안한 좌석과 멀티미디어 설비가 갖춰진 직원 복지 공간",
                    List.of("휴식", "방음", "엔터테인먼트"), "multimedia_room", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("포토 스튜디오", "촬영장비와 소품 보관 가능하며 소음 방해 없이 촬영할 수 있는 공간",
                    List.of("촬영", "소음차단", "소품", "창고"), "photo_studio", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("디자인 공작실", "제본, 목업 등을 작업하며 작업물을 보관할 수 있는 공간",
                    List.of("목업제작", "스프레이", "간단한 작업", "제본"), "design_workshop", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("엔터치존", "최신 디지털 디바이스를 체험하고 공유할 수 있는 체험 공간",
                    List.of("제품 체험", "열린공간", "공유공간"), "interaction_zone", bucketName, baseFolder, miscellaneousSpaceCategory),
                createGuide("플레이 그라운드", "무빙월이나 이동식 파티션을 사용해 다목적 공간으로 활용 가능",
                    List.of("무빙월", "이동식 파티션", "다목적공간"), "playground", bucketName, baseFolder, miscellaneousSpaceCategory)
            );

            guideRepository.saveAll(guides);
        }
    }

    private Guide createGuide(String name, String description, List<String> keywords, String englishName,
        String bucketName, String baseFolder, Category category) {
        String folderPath = baseFolder + englishName + "/";

        String coverImageKey = getCoverImageKey(bucketName, folderPath);
        Image coverImage = coverImageKey != null ? new Image(getS3ObjectUrl(bucketName, coverImageKey)) : null;
        if (coverImage != null) {
            coverImage = imageRepository.save(coverImage);
        }

        List<Image> detailImages = getS3ImagesFromFolder(bucketName, folderPath).stream()
            .filter(imageKey -> imageKey.contains("detail"))
            .map(imageKey -> {
                Image image = new Image(getS3ObjectUrl(bucketName, imageKey));
                return imageRepository.save(image);
            })
            .collect(Collectors.toList());

        Guide guide = new Guide(name, description, keywords, category);
        guide.setCoverImage(coverImage);
        guide.addDetailImages(detailImages);

        return guide;
    }

    private String getCoverImageKey(String bucketName, String folderPath) {
        List<String> keys = getS3ImagesFromFolder(bucketName, folderPath);
        return keys.stream()
            .filter(key -> key.contains("cover"))
            .findFirst()
            .orElse(null);
    }

    private List<String> getS3ImagesFromFolder(String bucketName, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(folderPath);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return result.getObjectSummaries().stream()
            .map(S3ObjectSummary::getKey)
            .filter(key -> key.matches(".*\\.(png|jpg|jpeg|gif)$"))
            .collect(Collectors.toList());
    }

    private String getS3ObjectUrl(String bucketName, String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
