package com.example.demo.src.comment;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/comments")
public class CommentController {

    final private JwtService jwtService;
    final private CommentProvider commentProvider;
    final private CommentService commentService;
    final private CommentDao commentDao;

    @Autowired
    public CommentController(JwtService jwtService, CommentProvider commentProvider, CommentService commentService, CommentDao commentDao){
        this.jwtService = jwtService;
        this.commentProvider = commentProvider;
        this.commentService = commentService;
        this.commentDao = commentDao;
    }

    /**
     * 댓글 생성
     * [POST] /app/comment/:productId/:userId
     * @param productId
     * @param userId
     * @param postCommentReq
     * @return PostCommentRes
     */
    @PostMapping("/{productId}/{userId}")
    public BaseResponse<PostCommentRes> createComment(@PathVariable("productId") int productId,
                                                      @PathVariable("userId") int userId,
                                                      @RequestBody PostCommentReq postCommentReq){
        try {
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 댓글 길이 제한
            if(postCommentReq.getContents().length() < 1 && postCommentReq.getContents().length() > 100){
                return new BaseResponse<>(WRONG_CONTENTS_LENGTH);
            }
            PostCommentRes postCommentRes = commentService.createComment(productId, userId, postCommentReq);
            return new BaseResponse<>(POST_COMMENT_SUCCESS, postCommentRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 조회
     * [GET] /app/comments/:productId
     * @param productId
     * @return
     */
    @GetMapping("/{productId}")
    public BaseResponse<List<GetCommentRes>> getComments(@PathVariable int productId){
        try{
            List<GetCommentRes> getCommentRes = commentProvider.getComments(productId);
            return new BaseResponse<>(GET_COMMENTS_SUCCESS, getCommentRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 댓글 삭제
     * @param commentId
     * @param userId
     * @return
     */
    @PatchMapping("/{commentId}/{userId}")
    public BaseResponse<String> deleteComments(@PathVariable int commentId,
                                         @PathVariable int userId){
        try{
            // TODO: commentId값이 음수거나 넘어가는거 validation
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            commentService.deleteComment(commentId, userId);
            return new BaseResponse<>(COMMENT_DELETE_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
