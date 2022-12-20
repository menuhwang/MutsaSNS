package com.likelion.mutsasns.exception.conflict;

import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.ErrorCode;

public abstract class AbstractConflictException extends AbstractBaseException {

    public AbstractConflictException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
