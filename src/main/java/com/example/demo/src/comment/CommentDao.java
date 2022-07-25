package com.example.demo.src.comment;

import com.example.demo.src.comment.model.GetCommentRes;
import com.example.demo.src.comment.model.PostCommentReq;
import com.example.demo.src.comment.model.PostCommentRes;
//import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class CommentDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 댓글 생성
     * [POST] comment
     * @param productId
     * @param userId
     * @param postCommentReq
     * @return lastCommentId
     */
    public int createComment(int productId, int userId, PostCommentReq postCommentReq){
        String createCommentQuery = "insert into comment (productId, userId, contents) values (?,?,?)";
        Object[] createCommentParams = new Object[]{productId, userId, postCommentReq.getContents()};

        this.jdbcTemplate.update(createCommentQuery, createCommentParams);

        String lastInsertCommentIdQuery = "select count(*) from comment";
        return this.jdbcTemplate.queryForObject(lastInsertCommentIdQuery, int.class);
    }

    /**
     * 댓글 조회
     * [GET] comments
     * @param productId
     * @return comments
     */
    public List<GetCommentRes> getComments(int productId){
        String FormatData = "comment.createdAt";
        String dateFormatQuery =
                "case when timestampdiff(second , "+FormatData+", current_timestamp) <60 " +
                        "then concat(timestampdiff(second, "+FormatData+", current_timestamp),'초 전') " +
                        "when timestampdiff(minute , "+FormatData+", current_timestamp) <60 " +
                        "then concat(timestampdiff(minute, "+FormatData+", current_timestamp),'분 전') " +
                        "when timestampdiff(hour , "+FormatData+", current_timestamp) <24 " +
                        "then concat(timestampdiff(hour, "+FormatData+", current_timestamp),'시간 전') " +
                        "when timestampdiff(day , "+FormatData+", current_timestamp) <365 " +
                        "then concat(timestampdiff(day, "+FormatData+", current_timestamp),'일 전') " +
                        "else concat(timestampdiff(year, current_timestamp, "+FormatData+"),' 년 전') end as ";

        String getCommentsQuery =
                "select comment.commentId, comment.productId, comment.userId, user.profileImgUrl, user.nickname, " +
                "comment.contents, " +
                dateFormatQuery + "'createdAt', " +
                "comment.isDeleted " +
                "from comment " +
                "left join user on user.userId = comment.userId " +
                "where comment.isDeleted = false and comment.productId=?";
        int getCommentsParams = productId;


        return this.jdbcTemplate.query(getCommentsQuery,
                (rs, rowNum) -> new GetCommentRes(
                        rs.getInt("commentId"),
                        rs.getInt("productId"),
                        rs.getInt("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("contents"),
                        rs.getString("createdAt"),
                        rs.getBoolean("isDeleted")),
                getCommentsParams);
    }


    /**
     * 댓글 삭제
     * @param commentId
     * @param userId
     * @return
     */
    public int deleteComment(int commentId, int userId){

        String deleteCommentQuery = "update comment set isDeleted = true where commentId=? and userId=?";
        Object[] deleteCommentParams = new Object[]{commentId, userId};

        int result = this.jdbcTemplate.update(deleteCommentQuery,deleteCommentParams);
        System.out.println(result);
        return result;

    }

    public Boolean isDeletedComment(int commentId){
        String IsDeletedComment = "select status from comment where commentId="+commentId;
        return this.jdbcTemplate.queryForObject(IsDeletedComment,Boolean.class);
    }

    public int getCommentLastId(){
        String getCommentLastId = "select count(*) from comment";
        return this.jdbcTemplate.queryForObject(getCommentLastId, int.class);
    }
}
