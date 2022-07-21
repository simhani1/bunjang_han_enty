package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.src.comment.model.GetCommentRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentProvider {

    final private CommentDao commentDao;

    @Autowired
    public CommentProvider(CommentDao commentDao){
        this.commentDao = commentDao;
    }

    public List<GetCommentRes> getComments(int productId) throws BaseException {
        try {
            List<GetCommentRes> getCommentRes = commentDao.getComments(productId);
            return getCommentRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }

    }
}
