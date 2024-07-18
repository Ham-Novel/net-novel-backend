package com.ham.netnovel.comment.service;

import com.ham.netnovel.comment.dto.CommentCreateDto;

public interface CommentService {



    boolean createComment(CommentCreateDto commentCreateDto);

    //조회


}
