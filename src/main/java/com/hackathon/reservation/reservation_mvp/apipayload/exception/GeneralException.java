package com.hackathon.reservation.reservation_mvp.apipayload.exception;

import com.hackathon.reservation.reservation_mvp.apipayload.code.BaseResponseCode;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ResponseCodeDto;

/**
 * 비즈니스 로직 처리 중 오류가 발생했을 때 던지는 예외 클래스.
 */
public class GeneralException extends RuntimeException {

    private final BaseResponseCode code;

    /**
     * 지정된 {@link BaseResponseCode} 로 예외를 생성합니다.
     *
     * @param code 오류 코드(enum)
     */
    public GeneralException(BaseResponseCode code) {
        super(code.get().getMessage());
        this.code = code;
    }

    /**
     * HTTP 상태 코드 없이, 응답 바디에만 포함할 {@link ResponseCodeDto} 를 반환합니다.
     *
     * @return {@link ResponseCodeDto}
     */
    public ResponseCodeDto getResponseCode() {
        return code.get();
    }

    /**
     * HTTP 상태 코드도 포함한 {@link ResponseCodeDto} 를 반환합니다.
     *
     * @return {@link ResponseCodeDto}
     */
    public ResponseCodeDto getResponseCodeWithStatus() {
        return code.getWithHttpStatus();
    }
}