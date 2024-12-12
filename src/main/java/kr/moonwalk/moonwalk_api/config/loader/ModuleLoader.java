package kr.moonwalk.moonwalk_api.config.loader;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Order(3)
public class ModuleLoader implements CommandLineRunner {

    private final ModuleRepository moduleRepository;
    private final CategoryRepository categoryRepository;
    private final AmazonS3 amazonS3;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (moduleRepository.count() == 0) {

            Category lounge = categoryRepository.findByNameAndType("라운지", Type.TYPE_MODULE)
                .orElseThrow();
            Category lockerRoom = categoryRepository.findByNameAndType("락커룸", Type.TYPE_MODULE)
                .orElseThrow();
            Category pantry = categoryRepository.findByNameAndType("팬트리", Type.TYPE_MODULE)
                .orElseThrow();
            Category restArea = categoryRepository.findByNameAndType("휴게공간", Type.TYPE_MODULE)
                .orElseThrow();
            Category oaSpace = categoryRepository.findByNameAndType("OA공간", Type.TYPE_MODULE)
                .orElseThrow();
            Category workSpace = categoryRepository.findByNameAndType("업무공간", Type.TYPE_MODULE)
                .orElseThrow();
            Category focusBooth = categoryRepository.findByNameAndType("집중업무부스", Type.TYPE_MODULE)
                .orElseThrow();
            Category executiveRoom = categoryRepository.findByNameAndType("임원실", Type.TYPE_MODULE)
                .orElseThrow();
            Category imageWall = categoryRepository.findByNameAndType("이미지월", Type.TYPE_MODULE)
                .orElseThrow();
            Category information = categoryRepository.findByNameAndType("인포메이션", Type.TYPE_MODULE)
                .orElseThrow();
            Category largeMeetingRoom = categoryRepository.findByNameAndType("대회의실",
                Type.TYPE_MODULE).orElseThrow();
            Category mediumMeetingRoom = categoryRepository.findByNameAndType("중회의실",
                Type.TYPE_MODULE).orElseThrow();
            Category smallMeetingRoom = categoryRepository.findByNameAndType("소회의실",
                Type.TYPE_MODULE).orElseThrow();
            Category quickMeetingBooth = categoryRepository.findByNameAndType("퀵미팅부스",
                Type.TYPE_MODULE).orElseThrow();

            List<Module> modules = List.of(
                new Module("라운지01", "플랜트박스와 테이블을 결합한 형태의 라운지", 3300, 3100, 10000,
                    List.of("테이블 : 우드&메탈", "의자 : 패브릭"), "LO_01", 5, lounge),
                new Module("라운지02", "C자 형태의 심플한 메인 라운지", 3200, 2900, 10000, List.of("의자 : 패브릭&메탈"),
                    "LO_02", 7, lounge),
                new Module("라운지03", "휴식과 업무가 혼합된 워크라운지 스타일의 구성", 4000, 3200, 10000,
                    List.of("테이블 : 우드&패브릭", "의자 : 패브릭"), "LO_03", 10, lounge),
                new Module("라운지04", "플랜트가 조성된 테이블이 있는 C자 라운지", 7500, 5100, 10000,
                    List.of("테이블 : 우드&메탈", "의자 : 패브릭"), "LO_04", 20, lounge),

                new Module("라커장 (4+12칸)", "코트 라커와 일반 라커 혼합 구성", 4500, 1500, 10000,
                    List.of("가구 : PET"), "LR_01", 16, lockerRoom),
                new Module("라커장 (12칸)", "12칸으로 구성된 라커장", 2400, 1800, 10000, List.of("가구 : PET"),
                    "LR_02", 12, lockerRoom),
                new Module("라커장 (16칸)", "16칸으로 구성된 라커장", 3200, 1800, 10000, List.of("가구 : PET"),
                    "LR_03", 16, lockerRoom),

                new Module("팬트리 01", "하부장, 싱크, 냉장고로 구성된 팬트리", 2800, 1500, 10000,
                    List.of("가구 : PET&스톤"), "PO_01", 4, pantry),
                new Module("팬트리(상부장) 01", "상부장과 하부장, 싱크, 냉장고로 구성된 팬트리", 3000, 1500, 10000,
                    List.of("가구 : PET&스톤"), "PO_02", 6, pantry),
                new Module("팬트리(상부장) 02", "상부장과 하부장, 싱크, 냉장고로 구성된 팬트리", 3800, 1500, 10000,
                    List.of("가구 : PET&스톤"), "PO_03", 6, pantry),
                new Module("팬트리(쇼케이스 & 냉장고)", "음료 쇼케이스와 냉장고가 배치된 팬트리", 3800, 2000, 10000,
                    List.of("가구 : 필름&스톤"), "PO_04", 8, pantry),

                new Module("바테이블 01", "주로 창가에 활용하는 2인 바테이블", 1800, 1500, 10000,
                    List.of("가구 : 우드&메탈"), "SD_01", 2, restArea),
                new Module("바테이블 02", "주로 창가에 활용하는 2인 바테이블", 2100, 1500, 10000,
                    List.of("가구 : 우드&메탈"), "SD_02", 2, restArea),
                new Module("라운지 테이블 01", "2인이 마주보는 형태의 기본적인 라운지 테이블", 2400, 1500, 10000,
                    List.of("가구 : 우드&메탈"), "SD_03", 2, restArea),
                new Module("라운지 테이블 02", "원형 테이블과 의자로 이루어진 기본형 라운지 테이블", 2000, 2000, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_04", 3, restArea),
                new Module("라운지 테이블 03", "원형 테이블과 의자로 이루어진 기본형 라운지 테이블", 2200, 2000, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_05", 3, restArea),
                new Module("라운지 소파 01", "4인이 별도로 활용가능한 기본형태의 라운지 소파, 간단한 미팅이 가능", 2500, 2000, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_06", 4, restArea),
                new Module("라운지 소파 02", "원형 러그를 중심으로 미니 테이블, 소파를 배치한 라운지", 2800, 2500, 10000,
                    List.of("가구 : 메탈&패브릭"), "SD_07", 4, restArea),
                new Module("라운지 소파 03", "소파와 테이블, 사각형 러그가 깔린 라운지", 2700, 2100, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_08", 5, restArea),
                new Module("라운지 테이블 04", "라운드 형태의 6인용 아일랜드 바 형태의 라운지 테이블", 4100, 3500, 10000,
                    List.of("가구 : 우드&스톤&패브릭"), "SD_09", 6, restArea),
                new Module("라운지 테이블 05", "6인까지 사용 가능한 워크라운지 타입의 테이블", 3000, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_10", 6, restArea),
                new Module("라운지 소파 04", "패브릭 소파와 스툴, 중앙의 테이블로 이루어진 배치", 3500, 3000, 10000,
                    List.of("가구 : 메탈&패브릭&우드"), "SD_11", 6, restArea),
                new Module("라운지 테이블 06", "8인까지 사용 가능한 워크라운지 타입의 테이블", 3600, 2300, 10000,
                    List.of("가구 : 우드&패브릭"), "SD_12", 8, restArea),
                new Module("라운지 테이블 07", "기본적인 형태의 라운지 테이블", 2400, 1500, 10000,
                    List.of("가구 : 우드&메탈"), "SD_13", 2, restArea),
                new Module("라운지 소파 05", "라운지의 벽을 활용한 일직선 형태의 라운지 소파 좌석", 7000, 1500, 10000,
                    List.of("가구 : 메탈&패브릭&우드"), "SD_14", 8, restArea),
                new Module("라운지 소파 06", "고급스러운 가죽소파와 스톤 테이블이 있는 라운지 소파 존", 3600, 2500, 10000,
                    List.of("가구 : 메탈&레더&스톤"), "SD_15", 5, restArea),
                new Module("라운지 테이블 08", "12인이 한번에 사용할 수 있는 빅테이블 형태의 라운지 테이블", 6800, 3800, 10000,
                    List.of("가구 : 우드"), "SD_16", 12, restArea),
                new Module("릴렉스 팟 01", "등을 기댈 수 있는 편한 좌석과 사이드 테이블이 배치된 1인 휴식 공간", 1300, 1200, 10000,
                    List.of("가구 : 패브릭&우드"), "SB_01", 1, restArea),
                new Module("릴렉스 팟 02", "등을 기댈 수 있는 편한 좌석과 사이드 테이블이 배치된 2인 휴식 공간", 2900, 1500, 10000,
                    List.of("가구 : 패브릭&우드"), "SB_02", 2, restArea),
                new Module("워크라운지 데스크 01", "파티션이 설치되어 라운지에서도 간단한 집중 업무를 볼 수 있는 데스크", 3000, 2600,
                    10000, List.of("가구 : 패브릭&우드"), "SB_03", 5, restArea),
                new Module("마사지체어 01", "직원 복지 및 휴식을 위한 마사지 체어 공간", 2400, 1500, 10000, List.of("-"),
                    "SM_01", 1, restArea),

                new Module("OA존 01", "상/하부장과 사무용기기로 구성된 OA공간", 3300, 2000, 10000,
                    List.of("가구 : 필름"), "OA_01", 4, oaSpace),
                new Module("OA존 02", "상/하부장과 사무용기기로 구성된 OA공간", 2800, 2000, 10000,
                    List.of("가구 : 필름"), "OA_01", 4, oaSpace),
                new Module("OA존 03", "상/하부장과 사무용기기로 구성된 OA공간", 3500, 2000, 10000,
                    List.of("가구 : PET"), "OA_03", 6, oaSpace),

                new Module("데스크 (일반)", "일반적인 사무용 데스크", 2000, 1500, 10000, List.of("가구 : 우드&패브릭"),
                    "OD_01A", 1, workSpace),
                new Module("데스크 (파티션)", "일반적인 사무용 데스크 (파티션 사용)", 2000, 1500, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_01B", 1, workSpace),
                new Module("Y형 데스크", "Y자 형태의 데스크 배치", 4000, 4000, 10000, List.of("가구 : 우드&패브릭"),
                    "OD_02A", 3, workSpace),
                new Module("Y형 데스크(파티션)", "Y자 형태의 데스크 배치 (파티션 사용)", 4000, 4000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_02B", 3, workSpace),
                new Module("데스크 (4인)", "4인 구성의 일반적인 데스크 배치", 4000, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_03A", 4, workSpace),
                new Module("데스크 (4인/파티션)", "4인 구성의 일반적인 데스크 배치 (파티션 사용)", 4000, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_03B", 4, workSpace),
                new Module("팀데스크 01", "팀장 1석과 팀원 4인으로 구성된 데스크 배치", 4500, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_04A", 5, workSpace),
                new Module("Y형 데스크(6인)", "Y형 데스크를 붙인 6인 구성의 데스크 배치", 5000, 4000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_04B", 6, workSpace),
                new Module("데스크 (6인)", "6인 구성의 일반적인 데스크 배치", 5500, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_05A", 6, workSpace),
                new Module("데스크 (6인/파티션)", "6인 구성의 일반적인 데스크 배치 (중앙 파티션 사용)", 5500, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_05B", 6, workSpace),
                new Module("팀데스크 02", "팀장 1석과 팀원 6인으로 구성된 데스크 배치 (3way 파티션 사용)", 5700, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_06B", 7, workSpace),
                new Module("팀데스크 03", "팀장 1석과 팀원 6인으로 구성된 데스크 배치 (파티션 사용)", 6000, 3000, 10000,
                    List.of("가구 : 우드&패브릭"), "OD_07B", 7, workSpace),
                new Module("핫데스크 01", "바테이블 형태의 협업 업무 공간", 3000, 2000, 10000,
                    List.of("바닥 : 데코타일 | 가구 : 우드&패브릭"), "HD_01", 6, workSpace),
                new Module("핫데스크 02", "빅테이블과 콘센트가 갖춰진 협업 업무용 데스크", 4500, 3000, 10000,
                    List.of("바닥 : 데코타일 | 가구 : 우드&패브릭"), "HD_02", 8, workSpace),
                new Module("핫데스크 03", "빅테이블과 콘센트가 갖춰진 협업 업무용 데스크", 5000, 3500, 10000,
                    List.of("바닥 : 데코타일 | 가구 : 우드&패브릭"), "HD_03", 10, workSpace),
                new Module("폰부스 01", "간단한 통화 및 집중업무가 가능한 폰부스", 1800, 1200, 10000,
                    List.of("벽체 : 패브릭 | 가구 : 우드"), "PB_01", 1, workSpace),
                new Module("폰부스 02", "간단한 통화 및 집중업무가 가능한 폰부스", 2100, 1700, 10000,
                    List.of("벽체 : 패브릭 | 가구 : 우드"), "PB_02", 1, workSpace),

                new Module("포커스팟 01", "데스크를 벗어나 집중업무를 볼 수 있는 1인 집중형 포커스팟", 1400, 1300, 10000,
                    List.of("가구 : 패브릭&우드"), "CB_01", 1, focusBooth),

                new Module("대표이사실 01", "대표이사를 위한 업무공간. 수납장과 VIP 미팅을 위한 공간이 마련되어 있음", 7500, 4500,
                    10000, List.of("바닥 : 카펫타일 | 벽체 : 패브릭&메탈"), "CO_01", 5, executiveRoom),
                new Module("대표이사실 02", "대표이사를 위한 업무공간. 수납장과 VIP 미팅을 위한 공간이 마련되어 있음", 8500, 4500,
                    10000, List.of("바닥 : 카펫타일 | 벽체 : 패브릭&메탈"), "CO_02", 6, executiveRoom),
                new Module("임원실 01", "임원진을 위한 업무공간. 간단한 미팅 테이블 마련되어 있음", 4500, 3000, 10000,
                    List.of("바닥 : 카펫타일 | 벽체 : 패브릭&메탈"), "EO_01", 4, executiveRoom),
                new Module("임원실 02", "임원진을 위한 업무공간. 간단한 미팅 테이블 마련되어 있음", 5500, 3500, 10000,
                    List.of("바닥 : 카펫타일 | 벽체 : 패브릭&메탈"), "EO_02", 4, executiveRoom),
                new Module("임원실 03", "임원진을 위한 업무공간. 간단한 미팅 테이블 마련되어 있음", 5500, 3500, 10000,
                    List.of("바닥 : 카펫타일 | 벽체 : 패브릭&메탈"), "EO_03", 4, executiveRoom),

                new Module("이미지월 01", "오피스 입구 공간으로, 기업의 로고 등을 부착하는 공간", 1200, 1200, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 도장"), "IW_01", 0, imageWall),
                new Module("이미지월 02", "오피스 입구 공간으로, 기업의 로고 등을 부착하는 공간", 1200, 1200, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 도장"), "IW_02", 0, imageWall),

                new Module("인포메이션 데스크 01", "대형오피스의 로비 혹은 법무법인 등 방문객을 안내하기 위한 공간", 4500, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 메탈", "데스크 : 인테리어 필름"), "IF_01", 3, information),

                new Module("대회의실 (12인)", "12인이 한번에 회의를 진행할 수 있는 대형 회의실", 7000, 4000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MLO_01", 12, largeMeetingRoom),
                new Module("대회의실 (13인)", "상석을 중심으로 양쪽으로 좌석이 배치된 대형 회의실", 9000, 5500, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MLO_02", 13, largeMeetingRoom),
                new Module("대회의실 (33인)", "상석을 중심으로 양쪽으로 좌석이 배치된 대형 회의실, 붙박이 좌석을 추가 배치해 33인까지 활용 가능",
                    10000, 6500, 10000, List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MLO_03", 33,
                    largeMeetingRoom),

                new Module("중회의실 (8+8인)", "폴딩도어를 활용해 8인 / 16인 가변적으로 활용 가능한 회의실", 8000, 4500, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MRO_01A", 16, mediumMeetingRoom),
                new Module("중회의실 (6인)", "매립형 TV와 사무용 데스크로 구성된 일반적인 회의실", 4500, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MRO_02", 6, mediumMeetingRoom),
                new Module("중회의실 (8인)", "매립형 TV와 사무용 데스크로 구성된 일반적인 회의실", 4500, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MRO_03", 8, mediumMeetingRoom),
                new Module("중회의실 (이동식 TV)", "이동식 디스플레이와 미팅 데스크로 구성된 회의실", 4000, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MRO_04", 6, mediumMeetingRoom),

                new Module("소회의실", "일반적인 구성의 소규모 회의실", 3000, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MSO_01", 4, smallMeetingRoom),
                new Module("허들룸", "원형 테이블과 TV로 조금 더 캐주얼하고 빠른 미팅이 가능한 미팅공간", 3000, 3000, 10000,
                    List.of("바닥 : 카펫타일", "벽체 : 패브릭"), "MSO_02", 4, smallMeetingRoom),

                new Module("퀵미팅부스 (6인)", "TV와 패브릭월로 구성된 기본형 퀵 미팅 부스", 2800, 2500, 10000,
                    List.of("바닥 : 카펫타일", "가구 : 패브릭&우드"), "QMO_01", 6, quickMeetingBooth),
                new Module("퀵미팅부스 (육각형)", "TV와 패브릭월로 구성된 육각형 퀵 미팅 부스", 3000, 2800, 10000,
                    List.of("바닥 : 카펫타일", "가구 : 패브릭&우드"), "QMO_02", 6, quickMeetingBooth),
                new Module("퀵미팅부스 (2인)", "패브릭부스와 간이 테이블로 구성된 2인 퀵 미팅 팟", 2400, 1300, 10000,
                    List.of("바닥 : 카펫타일", "가구 : 패브릭&우드"), "QMO_03", 2, quickMeetingBooth)

            );

            modules.forEach(module -> {
                String serialNumber = module.getSerialNumber();
                String folderPath = "modules/" + serialNumber + "/";
                module.setIsoImage(getImageByType(folderPath, serialNumber, "ISO"));
                module.setTopImage(getImageByType(folderPath, serialNumber, "TOP"));
            });

            moduleRepository.saveAll(modules);
        }
    }

    private Image getImageByType(String folderPath, String serialNumber, String type) {
        List<String> keys = getS3ImagesFromFolder("moonwalk-project", folderPath);
        String imageKey = keys.stream()
            .filter(key -> key.contains(serialNumber + "_" + type))
            .filter(key -> key.matches(".*\\.(png|jpg|jpeg|gif)$"))
            .findFirst()
            .orElse(null);

        return imageKey != null ? new Image(getS3ObjectUrl("moonwalk-project", imageKey)) : null;
    }

    private List<String> getS3ImagesFromFolder(String bucketName, String folderPath) {
        ListObjectsV2Request request = new ListObjectsV2Request()
            .withBucketName(bucketName)
            .withPrefix(folderPath);

        ListObjectsV2Result result = amazonS3.listObjectsV2(request);
        return result.getObjectSummaries().stream()
            .map(S3ObjectSummary::getKey)
            .collect(Collectors.toList());
    }

    private String getS3ObjectUrl(String bucketName, String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }
}
