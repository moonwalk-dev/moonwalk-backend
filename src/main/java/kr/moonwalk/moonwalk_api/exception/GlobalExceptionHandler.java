package kr.moonwalk.moonwalk_api.exception;

import java.util.HashMap;
import java.util.Map;
import kr.moonwalk.moonwalk_api.exception.auth.InvalidRefreshTokenException;
import kr.moonwalk.moonwalk_api.exception.notfound.CartNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.CategoryNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.EstimateNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.GuideNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.MoodNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectModuleNotFoundException;
import kr.moonwalk.moonwalk_api.exception.notfound.ProjectNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex,
        WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "ALREADY_EXISTS");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex,
        WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("잘못된 이메일 또는 비밀번호입니다.", "BAD_CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse("서버 오류가 발생했습니다.", "INTERNAL_SERVER_ERROR");
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRefreshTokenException(
        InvalidRefreshTokenException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INVALID_REFRESH_TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
        CategoryNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "CATEGORY_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ModuleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleModuleNotFoundException(
        ModuleNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "MODULE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(EstimateNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEstimateNotFoundException(
        EstimateNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "ESTIMATE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCartNotFoundException(
        CartNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "CART_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MoodNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleMoodNotFoundException(
        MoodNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "MOOD_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(GuideNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleGuideNotFoundException(
        MoodNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "GUIDE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(
        ProjectNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "PROJECT_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(ProjectModuleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectModuleNotFoundException(
        ProjectModuleNotFoundException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "PROJECT_MODULE_NOT_FOUND");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "ILLEGAL_ARGUMENT");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "ACCESS_DENIED");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
        MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return ResponseEntity.badRequest().body("파일 크기가 허용된 최대 크기(25MB)를 초과했습니다!");
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolationException(
        DataIntegrityViolationException ex) {
        String message = "이 모듈은 다른 곳에서 사용중이어서 삭제할 수 없습니다.";
        ErrorResponse errorResponse = new ErrorResponse(message, "MODULE_IN_USE");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
}
