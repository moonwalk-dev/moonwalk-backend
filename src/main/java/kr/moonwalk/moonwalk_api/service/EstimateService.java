package kr.moonwalk.moonwalk_api.service;

import java.util.List;
import java.util.stream.Collectors;
import kr.moonwalk.moonwalk_api.domain.Cart;
import kr.moonwalk.moonwalk_api.domain.Estimate;
import kr.moonwalk.moonwalk_api.domain.Module;
import kr.moonwalk.moonwalk_api.domain.User;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartListResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.EstimateCreateDto;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.repository.CartRepository;
import kr.moonwalk.moonwalk_api.repository.EstimateRepository;
import kr.moonwalk.moonwalk_api.repository.ModuleRepository;
import kr.moonwalk.moonwalk_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EstimateService {

    private final UserRepository userRepository;
    private final EstimateRepository estimateRepository;
    private final ModuleRepository moduleRepository;
    private final CartRepository cartRepository;

    @Transactional
    public EstimateCreateDto createEstimate() {
        User user = getCurrentAuthenticatedUser();

        Estimate estimate = new Estimate(user);
        Estimate savedEstimate = estimateRepository.save(estimate);

        return new EstimateCreateDto(savedEstimate.getId());
    }

    @Transactional
    public CartAddResponseDto addModule(Long estimateId, CartAddDto cartAddDto) {
        Module module = moduleRepository.findById(cartAddDto.getModuleId())
            .orElseThrow(() -> new ModuleNotFoundException("모듈을 찾을 수 없습니다."));
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("간단 견적을 찾을 수 없습니다."));

        Cart cart = cartRepository.findByEstimateAndModule(estimate, module)
            .orElseGet(() -> new Cart(estimate, module, cartAddDto.getQuantity()));

        if (cart.getId() != null) {
            cart.setQuantity(cartAddDto.getQuantity());
        }

        cartRepository.save(cart);

        return new CartAddResponseDto(module.getId(), module.getName(), estimate.getId(), cart.getQuantity());
    }

    private User getCurrentAuthenticatedUser() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userEmail = userDetails.getUsername();

        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Transactional
    public void deleteCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new CartNotFoundException("해당 카트 항목을 찾을 수 없습니다."));

        cartRepository.delete(cart);
    }

    @Transactional(readOnly = true)
    public CartListResponseDto getFilteredCarts(Long estimateId, List<Long> categoryIds) {
        Estimate estimate = estimateRepository.findById(estimateId)
            .orElseThrow(() -> new EstimateNotFoundException("견적을 찾을 수 없습니다."));

        List<CartResponseDto> carts = estimate.getCarts().stream()
            .filter(cart -> categoryIds == null || categoryIds.isEmpty() ||
                categoryIds.contains(cart.getModule().getCategory().getId()))
            .map(cart -> new CartResponseDto(
                cart.getId(),
                cart.getEstimate().getId(),
                cart.getModule().getId(),
                cart.getModule().getName(),
                cart.getModule().getCapacity(),
                cart.getModule().getSerialNumber(),
                cart.getModule().getIsoImage().getImageUrl(),
                cart.getQuantity()))
            .collect(Collectors.toList());

        return new CartListResponseDto(carts);
    }
}
