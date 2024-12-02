package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Cart;
import kr.moonwalk.moonwalk_api.domain.Category.Type;
import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.Mood;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartListResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.EstimateCreateDto;
import kr.moonwalk.moonwalk_api.dto.estimate.EstimateResponseDto;
import kr.moonwalk.moonwalk_api.dto.mood.EstimateMoodResponseDto;
import kr.moonwalk.moonwalk_api.dto.mypage.EstimateInfoDto;
import kr.moonwalk.moonwalk_api.dto.mypage.EstimateInfoListDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.MoodNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CartRepository;
import kr.moonwalk.moonwalk_api.repository.CategoryRepository;
import kr.moonwalk.moonwalk_api.repository.EstimateRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.repository.MoodRepository;
import kr.moonwalk.moonwalk_api.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final EstimateRepository estimateRepository;
    private final ModuleRepository moduleRepository;
    private final CartRepository cartRepository;
    private final CategoryRepository categoryRepository;
    private final MoodRepository moodRepository;
    private final AuthService authService;


    @Transactional
    public EstimateCreateDto createEstimate() {
        User user = authService.getCurrentAuthenticatedUser();

        Estimate estimate = new Estimate(user);
        Estimate savedEstimate = estimateRepository.save(estimate);

        return new EstimateCreateDto(savedEstimate.getId());
    }

    @Transactional
    public CartAddResponseDto addModule(Long estimateId, CartAddDto cartAddDto) {
        Module module = moduleRepository.findById(cartAddDto.getModuleId())
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByEstimateAndModule(estimate, module)
            .orElseGet(() -> new Cart(estimate, module, cartAddDto.getQuantity()));

        if (cart.getId() != null) {
            cart.setQuantity(cartAddDto.getQuantity());
        }

        cartRepository.save(cart);

        estimate.updateTotalPrice();
        estimateRepository.save(estimate);

        return new CartAddResponseDto(module.getId(), module.getName(), estimate.getId(), cart.getQuantity());
    }

    @Transactional
    public void deleteCart(Long estimateId, Long cartId) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("해당 카트 항목을 찾을 수 없습니다."));

        estimate.getCarts().remove(cart);
        cartRepository.delete(cart);
    }

    @Transactional(readOnly = true)
    public CartListResponseDto getFilteredCarts(Long estimateId, List<String> categoryNames) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        List<Long> categoryIds = categoryNames != null && !categoryNames.isEmpty()
            ? categoryRepository.findIdsByNameInAndType(categoryNames, Type.TYPE_MODULE)
            : null;

        List<CartResponseDto> carts = estimate.getCarts().stream()
            .filter(cart -> categoryIds == null || categoryIds.contains(cart.getModule().getCategory().getId()))
            .map(cart -> new CartResponseDto(
                cart.getId(),
                cart.getEstimate().getId(),
                cart.getModule().getId(),
                cart.getModule().getName(),
                cart.getModule().getCapacity(),
                cart.getModule().getSerialNumber(),
                cart.getModule().getIsoImage() != null ? cart.getModule().getIsoImage().getImageUrl() : null,
                cart.getQuantity()))
            .collect(Collectors.toList());

        return new CartListResponseDto(carts);
    }


    @Transactional
    public EstimateMoodResponseDto setMood(Long estimateId, Long moodId) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        Mood mood = moodRepository.findById(moodId)
            .orElseThrow(() -> new MoodNotFoundException("무드를 찾을 수 없습니다."));

        estimate.setMood(mood);
        estimateRepository.save(estimate);

        return new EstimateMoodResponseDto(estimate.getId(), mood.getId(), mood.getName(),
            mood.getCoverImage().getImageUrl());

    }

    @Transactional(readOnly = true)
    public EstimateResponseDto getInfo(Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        Long moodId = estimate.getMood() != null ? estimate.getMood().getId() : null;
        String moodName = estimate.getMood() != null ? estimate.getMood().getName() : "무드가 설정되지 않았습니다.";
        String coverImage = estimate.getMood() != null ? estimate.getMood().getCoverImage().getImageUrl() : null;

        return new EstimateResponseDto(
            estimate.getId(),
            moodId,
            moodName,
            coverImage,
            estimate.getTotalPrice(),
            estimate.getTitle(),
            estimate.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public EstimateInfoListDto getEstimates() {

        User user = authService.getCurrentAuthenticatedUser();

        List<EstimateInfoDto> estimates = user.getEstimates().stream().map(
                estimate -> new EstimateInfoDto(estimate.getId(), estimate.getTitle(),
                    estimate.getTotalPrice(), estimate.getCreatedAt(), estimate.getMood() != null ? estimate.getMood().getId()
                    : null))
            .collect(Collectors.toList());

        return new EstimateInfoListDto(estimates);
    }

    @Transactional
    public void deleteEstimate(Long estimateId) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        User user = estimate.getUser();
        user.getEstimates().remove(estimate);
        estimateRepository.delete(estimate);

    }

}
