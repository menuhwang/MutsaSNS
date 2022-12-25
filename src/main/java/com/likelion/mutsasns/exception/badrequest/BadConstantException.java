package com.likelion.mutsasns.exception.badrequest;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.BAD_CONSTANT;

public class BadConstantException extends AbstractBaseException {
    public BadConstantException(String message) {
        super(BAD_CONSTANT, message);
    }
}
