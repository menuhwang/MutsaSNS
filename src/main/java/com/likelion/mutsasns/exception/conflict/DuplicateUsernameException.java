package com.likelion.mutsasns.exception.conflict;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.DUPLICATED_USERNAME;

public class DuplicateUsernameException extends AbstractBaseException {
    public DuplicateUsernameException() {
        super(DUPLICATED_USERNAME);
    }
}
