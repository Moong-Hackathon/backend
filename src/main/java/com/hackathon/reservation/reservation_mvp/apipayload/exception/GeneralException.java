package com.hackathon.reservation.reservation_mvp.apipayload.exception;

import com.hackathon.reservation.reservation_mvp.apipayload.code.BaseErrorCode;
import com.hackathon.reservation.reservation_mvp.apipayload.code.ErrorReasonDto;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {
    private final BaseErrorCode code;

    public GeneralException(BaseErrorCode code) {
        super(code.getReason().getMessage());
        this.code = code;
    }

    public ErrorReasonDto getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDto getErrorReasonHttpStatus() {
        return this.code.getReasonHttpStatus();
    }
}

