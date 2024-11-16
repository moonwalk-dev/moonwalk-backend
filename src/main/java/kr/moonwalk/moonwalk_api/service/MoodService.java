package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Image;
import kr.moonwalk.moonwalk_api.domain.Mood;
import kr.moonwalk.moonwalk_api.dto.MoodResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodDto;
import kr.moonwalk.moonwalk_api.dto.mood.MoodListResponseDto;
import kr.moonwalk.moonwalk_api.exception.notfound.MoodNotFoundException;
import kr.moonwalk.moonwalk_api.repository.MoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MoodService {

    private final MoodRepository moodRepository;

    @Transactional(readOnly = true)
    public MoodListResponseDto getAllMoods() {
        List<MoodDto> moods = moodRepository.findAll().stream()
            .map(mood -> new MoodDto(
                mood.getId(),
                mood.getName(),
                mood.getCoverImage() != null ? mood.getCoverImage().getImageUrl() : null
            ))
            .collect(Collectors.toList());

        return new MoodListResponseDto(moods);
    }

    @Transactional(readOnly = true)
    public MoodResponseDto getInfo(Long moodId) {
        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        String coverImageUrl = (mood.getCoverImage() != null) ? mood.getCoverImage().getImageUrl() : null;

        List<String> detailImageUrls = mood.getDetailImages().stream()
            .map(Image::getImageUrl)
            .collect(Collectors.toList());

        return new MoodResponseDto(
            mood.getId(),
            mood.getName(),
            mood.getDescription(),
            coverImageUrl,
            detailImageUrls
        );
    }

}
