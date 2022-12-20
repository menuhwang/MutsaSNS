package com.likelion.mutsasns.exception.conflict;

import static com.likelion.mutsasns.exception.ErrorCode.DUPLICATED_USERNAME;

public class DuplicateUsernameException extends AbstractConflictException {
    public DuplicateUsernameException() {
        super(DUPLICATED_USERNAME);
    }
}
