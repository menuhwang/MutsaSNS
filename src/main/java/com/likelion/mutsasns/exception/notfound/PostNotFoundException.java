package com.likelion.mutsasns.exception.notfound;

import com.likelion.mutsasns.exception.AbstractBaseException;

import static com.likelion.mutsasns.exception.ErrorCode.POST_NOT_FOUND;

public class PostNotFoundException extends AbstractBaseException {
    public PostNotFoundException() {
        super(POST_NOT_FOUND);
    }
}
