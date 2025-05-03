package com.hackathon.reservation.reservation_mvp.apipayload.code;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 응답 페이로드의 코드, 메시지, 성공 여부, (선택적) HTTP 상태 코드를 담는 DTO.
 *
 * <p>JSON 직렬화 시 {@code httpStatus} 가 {@code null} 이면 출력되지 않습니다.
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseCodeDto {
    /** API 호출 성공 여부. */
    private final boolean isSuccess;

    /** 비즈니스/에러 코드 (예: COMMON200, STORE4001 등). */
    private final String code;

    /** 사용자에게 노출할 메시지. */
    private final String message;

    /**
     * HTTP 응답 상태 코드.
     *
     * <p>예외 핸들러 등에서 필요할 때만 세팅하며, 일반 성공 응답에는 포함되지 않습니다.
     */
    private final HttpStatus httpStatus;
}