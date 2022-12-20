package com.likelion.mutsasns.exception.notfound;

import static com.likelion.mutsasns.exception.ErrorCode.USER_NOT_FOUND;

public class UserNotFoundException extends AbstractNotFoundException{
    public UserNotFoundException() {
        super(USER_NOT_FOUND);
    }
}
