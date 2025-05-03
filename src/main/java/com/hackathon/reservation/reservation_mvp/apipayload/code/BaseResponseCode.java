package com.hackathon.reservation.reservation_mvp.apipayload.code;

/**
 * ResponseCodeDto 생성용 인터페이스.
 *
 * <p>구현체는 API 응답 시 사용될 비즈니스/에러 코드를 정의하고,
 * DTO 생성 메서드를 제공해야 합니다.
 */
public interface BaseResponseCode {
    /**
     * HTTP Status 헤더 없이, 응답 바디 페이로드에만 포함할 DTO를 반환합니다.
     *
     * @return 성공/실패 여부와 코드·메시지를 담은 {@link ResponseCodeDto}
     */
    ResponseCodeDto get();

    /**
     * HTTP Status 헤더 설정용으로, {@link ResponseCodeDto#getHttpStatus()} 도 포함한 DTO를 반환합니다.
     *
     * @return {@link ResponseCodeDto} (httpStatus 포함)
     */
    ResponseCodeDto getWithHttpStatus();
}