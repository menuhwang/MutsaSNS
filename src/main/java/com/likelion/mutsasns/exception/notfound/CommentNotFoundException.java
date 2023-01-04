package com.likelion.mutsasns.exception.notfound;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.COMMENT_NOT_FOUND;

public class CommentNotFoundException extends AbstractBaseException {
    public CommentNotFoundException() {
        super(COMMENT_NOT_FOUND);
    }
}
