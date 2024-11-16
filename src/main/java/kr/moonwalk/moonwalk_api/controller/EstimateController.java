package kr.moonwalk.moonwalk_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartListResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.CartAddResponseDto;
import kr.moonwalk.moonwalk_api.dto.estimate.EstimateCreateDto;
import kr.moonwalk.moonwalk_api.service.EstimateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/estimates")
public class EstimateController {

    private final EstimateService estimateService;

    @Operation(summary = "간단 견적 생성", description = "헤더에 있는 토큰을 확인하여 사용자 정보를 추출한 후, 해당 사용자의 새로운 간단 견적을 생성합니다.")
    @PostMapping
    public ResponseEntity<EstimateCreateDto> createEstimate() {
        EstimateCreateDto response = estimateService.createEstimate();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "장바구니에 모듈 추가", description = "견적이 없으면 새로운 장바구니를 생성하고 모듈을 추가하며, 이미 존재하는 경우 모듈의 수량을 업데이트합니다.")
    @PostMapping("/{estimateId}")
    public ResponseEntity<CartAddResponseDto> addModule(@PathVariable Long estimateId,
        @Valid @RequestBody CartAddDto cartAddDto) {

        CartAddResponseDto response = estimateService.addModule(estimateId, cartAddDto);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리별 장바구니 리스트 조회",
        description = "견적 ID를 경로로 받고, 선택된 카테고리 ID들로 장바구니를 필터링합니다.")
    @GetMapping("/{estimateId}/carts")
    public ResponseEntity<CartListResponseDto> getFilteredCarts(
        @PathVariable Long estimateId,
        @RequestParam(required = false) List<Long> categoryIds) {

        CartListResponseDto response = estimateService.getFilteredCarts(estimateId, categoryIds);

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "장바구니 항목 삭제", description = "해당 장바구니 항목을 삭제합니다.")
    @DeleteMapping("/{estimateId}/carts/{cartId}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long estimateId, @PathVariable Long cartId) {
        estimateService.deleteCart(cartId);
        return ResponseEntity.noContent().build();
    }

}
