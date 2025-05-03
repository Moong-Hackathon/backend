package com.hackathon.reservation.reservation_mvp.apipayload.exception;

import com.hackathon.reservation.reservation_mvp.apipayload.ApiResponse;
import com.hackathon.reservation.reservation_mvp.apipayload.code.status.ErrorStatus;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ResponseCodeDto;
import jakarta.validation.ConstraintViolationException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 컨트롤러 전역에서 발생한 예외를 잡아서 {@link ApiResponse} 형태로 변환해 줍니다.
 */
@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    /**
     * {@link MethodArgumentNotValidException} 처리.
     *
     * <p>요청 DTO 검증 실패 시, 필드별 에러 메시지를 맵으로 묶어서 응답합니다.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String field = fieldError.getField();
            String msg = Optional.ofNullable(fieldError.getDefaultMessage()).orElse("");
            errors.merge(field, msg, (oldV, newV) -> oldV + ", " + newV);
        }

        ResponseCodeDto code = ErrorStatus.BAD_REQUEST.get();
        ApiResponse<Object> body =
                ApiResponse.onFailure(code.getCode(), code.getMessage(), errors);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@link ConstraintViolationException} 처리.
     *
     * <p>URI 템플릿 변수나 {@code @Validated} 파라미터 검증 실패 시 호출됩니다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleConstraintViolation(
            ConstraintViolationException ex) {
        String raw = ex.getConstraintViolations().stream()
                .map(v -> v.getMessage())
                .findFirst()
                .orElse("_BAD_REQUEST");
        ErrorStatus status = ErrorStatus.valueOf(raw);
        ResponseCodeDto code = status.get();
        ApiResponse<Object> body =
                ApiResponse.onFailure(code.getCode(), code.getMessage(), null);
        return new ResponseEntity<>(body, status.getHttpStatus());
    }

    /**
     * {@link GeneralException} 처리.
     *
     * <p>비즈니스 로직 단계에서 던진 예외를 ResponseCodeDto 로 변환하여 반환합니다.
     */
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<Object>> handleGeneral(GeneralException ex) {
        ResponseCodeDto code = ex.getResponseCodeWithStatus();
        ApiResponse<Object> body =
                ApiResponse.onFailure(code.getCode(), code.getMessage(), null);
        return new ResponseEntity<>(body, code.getHttpStatus());
    }

    /**
     * 그 외 모든 예외 처리.
     *
     * <p>예상치 못한 예외 발생 시 500 에러로 감싸서 반환합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleAll(Exception ex) {
        log.error("Unhandled exception", ex);
        ResponseCodeDto code =
                ErrorStatus.INTERNAL_SERVER_ERROR.getWithHttpStatus();
        ApiResponse<Object> body =
                ApiResponse.onFailure(code.getCode(), code.getMessage(), null);
        return new ResponseEntity<>(body, code.getHttpStatus());
    }
}