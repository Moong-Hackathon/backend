package com.hackathon.reservation.reservation_mvp.apipayload.code.status;

import com.hackathon.reservation.reservation_mvp.apipayload.code.BaseResponseCode;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ResponseCodeDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * API 호출 중 발생할 수 있는 에러 코드를 정의합니다.
 */
@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseResponseCode {
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    STORE_NOT_FOUND(
            HttpStatus.NOT_FOUND, "STORE4001", "존재하지 않는 가게입니다."),
    RESERVATION_NOT_FOUND(
            HttpStatus.NOT_FOUND, "RESERVATION4001", "존재하지 않는 예약입니다."),
    RESERVATION_STORE_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "RESERVATION4002",
            "예약의 가게 정보가 요청한 가게와 일치하지 않습니다."),
    RESERVATION_IS_NOT_PENDING(
            HttpStatus.BAD_REQUEST,
            "RESERVATION4003",
            "예약 상태가 PENDING 일때만 수락/거절할 수 있습니다"),
    RESERVATION_CANNOT_CANCEL(
            HttpStatus.BAD_REQUEST,
            "RESERVATION4004",
            "예약 상태가 PENDING, AVAILABLE, CONFIRMED 일때만 취소할 수 있습니다"),
    INVALID_RESERVATION_STATUS(
            HttpStatus.BAD_REQUEST, "RESERVATION4005", "존재하지 않는 예약 상태입니다"),
    RESERVATION_MEMBER_MISMATCH(
            HttpStatus.BAD_REQUEST,
            "RESERVATION4006",
            "예약의 멤버가 사용자와 일치하지 않습니다."),
    RESERVATION_IS_NOT_AVAILABLE(
            HttpStatus.BAD_REQUEST,
            "RESERVATION4007",
            "예약 상태가 AVAILABLE 일때만 사용자가 예약을 확정할 수 있습니다");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ResponseCodeDto get() {
        return ResponseCodeDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .build();
    }

    @Override
    public ResponseCodeDto getWithHttpStatus() {
        return ResponseCodeDto.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}