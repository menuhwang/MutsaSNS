package com.likelion.mutsasns.exception.unauthorized;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.USER_NOT_LOGGED_IN;

public class UserNotLoggedInException extends AbstractBaseException {
    public UserNotLoggedInException() {
        super(USER_NOT_LOGGED_IN);
    }
}
