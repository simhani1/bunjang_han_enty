package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
import com.example.demo.src.product.ProductDao;
import com.example.demo.src.product.ProductProvider;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class CommentService {

    final private CommentDao commentDao;
    final private CommentProvider commentProvider;
    private final ProductProvider productProvider;
    private final ProductDao productDao;

    public CommentService(CommentDao commentDao, CommentProvider commentProvider,
                          ProductProvider productProvider, ProductDao productDao){
        this.commentProvider = commentProvider;
        this.commentDao = commentDao;
        this.productProvider = productProvider;
        this.productDao = productDao;
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
            if (userId < 0){
                throw new BaseException(NO_EXISTED_USER);
            }
            if (productId < 0 || productId > productProvider.getLastProductId()){
                throw new BaseException(NO_EXISTED_PRODUCT);
            }
            if (productDao.getProductIsDeleted(productId)){
                throw new BaseException(DELETED_PRODUCT);
            }
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
        if (commentId < 0 || commentId > commentProvider.getCommentLastId()){
            throw new BaseException(NEGATIVE_COMMENT_ID);
        }
        if (userId < 0){
            throw new BaseException(NO_EXISTED_USER);
        }
        if (commentDao.isDeletedComment(commentId)){
            throw new BaseException(DELETED_COMMENT);
        }
        try{
            // TODO: productId의 status가 이미 isDeleted == true라면 validation
            int result = commentDao.deleteComment(commentId, userId);

        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);

        }
    }

}
