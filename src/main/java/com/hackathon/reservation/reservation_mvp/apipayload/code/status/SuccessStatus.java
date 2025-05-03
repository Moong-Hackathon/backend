package com.hackathon.reservation.reservation_mvp.apipayload.code.status;

import com.hackathon.reservation.reservation_mvp.apipayload.code.BaseResponseCode;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ResponseCodeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 성공 응답에 사용할 HTTP 상태, 비즈니스 코드, 메시지를 정의한다.
 */
@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseResponseCode {

    /**
     * 공통 성공 (200 OK)
     */
    OK(HttpStatus.OK, "COMMON200", "성공입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ResponseCodeDto get() {
        return ResponseCodeDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ResponseCodeDto getWithHttpStatus() {
        return ResponseCodeDto.builder()
                .isSuccess(true)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}