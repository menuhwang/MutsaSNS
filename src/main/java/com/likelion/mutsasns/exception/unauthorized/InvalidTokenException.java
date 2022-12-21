package com.likelion.mutsasns.exception.unauthorized;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_TOKEN;

public class InvalidTokenException extends AbstractBaseException {
    public InvalidTokenException() {
        super(INVALID_TOKEN);
    }
}
