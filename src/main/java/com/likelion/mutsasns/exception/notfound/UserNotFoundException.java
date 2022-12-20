package com.likelion.mutsasns.exception.notfound;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends AbstractBaseException {
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
