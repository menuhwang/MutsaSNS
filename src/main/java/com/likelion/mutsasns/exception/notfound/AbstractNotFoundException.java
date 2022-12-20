package com.likelion.mutsasns.exception.notfound;

import com.likelion.mutsasns.exception.AbstractBaseException;
import com.likelion.mutsasns.exception.ErrorCode;

public abstract class AbstractNotFoundException extends AbstractBaseException {
    public AbstractNotFoundException(ErrorCode errorCode) {
        super(errorCode.getHttpStatus(), errorCode.getMessage());
    }
}
