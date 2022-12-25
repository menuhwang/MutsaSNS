package com.likelion.mutsasns.exception.badrequest;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_UPDATE_USER_ROLE;

public class UpdateUserRoleException extends AbstractBaseException {
    public UpdateUserRoleException() {
        super(INVALID_UPDATE_USER_ROLE);
    }
}
