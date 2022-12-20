package com.likelion.mutsasns.exception.unauthorized;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_PASSWORD;

public class InvalidPasswordException extends AbstractBaseException {
    public InvalidPasswordException() {
        super(INVALID_PASSWORD);
    }
}
