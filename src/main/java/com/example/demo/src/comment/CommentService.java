package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentService {

    final private CommentDao commentDao;
    final private CommentProvider commentProvider;

    public CommentService(CommentDao commentDao, CommentProvider commentProvider){
        this.commentProvider = commentProvider;
        this.commentDao = commentDao;
    }

    /**
     * 댓글 생성
     * @param productId
     * @param userId
     * @param postCommentReq
     * @return
     * @throws BaseException
     */
    public PostCommentRes createComment(int productId, int userId, PostCommentReq postCommentReq) throws BaseException {
        try{
            int lastCommentId = commentDao.createComment(productId, userId, postCommentReq);
            return new PostCommentRes(lastCommentId);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @param userId
     * @throws BaseException
     */
    public void deleteComment(int commentId, int userId) throws BaseException {
        if(commentDao.deleteComment(commentId, userId) == 0){
            throw new BaseException(INVALID_USER_DELETE_COMMENT);
        }
        try{
            // TODO: productId의 status가 이미 isDeleted == true라면 validation
            int result = commentDao.deleteComment(commentId, userId);

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);

        }
    }

}
