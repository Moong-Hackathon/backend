package com.hackathon.reservation.reservation_mvp.apipayload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.hackathon.reservation.reservation_mvp.apipayload.code.BaseResponseCode;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ResponseCodeDto;
import com.hackathon.reservation.reservation_mvp.apipayload.code.status.SuccessStatus;
import lombok.Getter;

/**
 * 모든 API 응답을 감싸는 공통 포맷.
 *
 * <p>{@code isSuccess}, {@code code}, {@code message} 는 항상 출력되며,
 * {@code result}는 null 이면 직렬화에서 제외된다.
 *
 * @param <T> 실제 응답 페이로드 타입
 */
@Getter
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final boolean isSuccess;

    private final String code;
    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;

    private ApiResponse(
            boolean isSuccess,
            String code,
            String message,
            T result) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
        this.result = result;
    }

    /**
     * 성공 응답을 생성한다.
     *
     * @param result 실제 페이로드
     * @param <T> 페이로드 타입
     * @return ApiResponse with COMMON200
     */
    public static <T> ApiResponse<T> ofSuccess(T result) {
        ResponseCodeDto rc = SuccessStatus.OK.get();
        return new ApiResponse<>(true, rc.getCode(), rc.getMessage(), result);
    }

    /**
     * 실패 응답을 생성한다.
     *
     * @param code 사용자에게 노출할 코드
     * @param message 사용자에게 노출할 메시지
     * @param data 추가로 전달할 에러 상세
     * @param <T> data 타입
     * @return ApiResponse failure
     */
    public static <T> ApiResponse<T> ofFailure(
            String code,
            String message,
            T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}