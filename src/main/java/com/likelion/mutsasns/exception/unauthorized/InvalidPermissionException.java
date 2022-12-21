package com.likelion.mutsasns.exception.unauthorized;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_PERMISSION;

public class InvalidPermissionException extends AbstractBaseException {
    public InvalidPermissionException() {
        super(INVALID_PERMISSION);
    }
}
