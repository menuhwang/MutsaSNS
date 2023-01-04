package com.likelion.mutsasns.exception.badrequest;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.INVALID_UPDATE_COMMENT;

public class InvalidUpdateCommentException extends AbstractBaseException {
    public InvalidUpdateCommentException() {
        super(INVALID_UPDATE_COMMENT);
    }
}
