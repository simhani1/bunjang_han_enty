package com.example.demo.src.follow;

import com.example.demo.src.follow.model.GetFollowerRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class FollowDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    //새로운 팔로우
    public int follow(int userId, int followUserId){
        String followQuery = "insert into followList (userId, followUserId) values(?,?)";
        Object[] followParams = new Object[]{followUserId, userId};

        return this.jdbcTemplate.update(followQuery, followParams);
    }

    //팔로우 상태 바꾸기
    public int switchFollowStatus(int userId, int followUserId, boolean status){
        String switchFollowStatusQuery = "update followList set status=? where userId = ? and followUserId = ?";
        Object[] switchFollowStatusParams = new Object[]{status, userId, followUserId};

        return this.jdbcTemplate.update(switchFollowStatusQuery, switchFollowStatusParams);
    }

    public List<GetFollowerRes> getFollowers(int userId){
        String getFollowersQuery =
                "select user.userId, user.profileImgUrl, user.nickname, B.productCount, C.followerNum " +
                "from user " +
                "left join " +
                "(select followUserId " +
                "from followList " +
                "where userId=? and status=true) as A on user.userId = A.followUserId " +
                "left join " +
                "(select userId, count(productId) as 'productCount' " +
                "from product " +
                "group by userId) as B on B.userId = A.followUserId " +
                "left join " +
                "(select userId, count(followUserId) as 'followerNum' " +
                "from followList " +
                "group by userId) as C on C.userId = A.followUserId " +
                "where user.userId = A.followUserId";
        int getFollowerParam = userId;




        return this.jdbcTemplate.query(getFollowersQuery,
                (rs, rowNum) -> new GetFollowerRes(
                        rs.getInt("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getInt("productCount"),
                        rs.getInt("followerNum")),
                getFollowerParam);
    }

    public int countFollower(int userId){
        String countFollowerQuery = "select count(followUserId) from followList where userId=?";
        int countFollowerParam = userId;

        return this.jdbcTemplate.queryForObject(countFollowerQuery, int.class, countFollowerParam);
    }
    // 이미 팔로우 한 유저인지 체크
    public int checkFollowStatus(int userId, int followUserId){
        String checkFollowStatusQuery = "select exists(select followId from followList where userId=? and followUserId=?)";
        Object[] checkFollowStatusParams = new Object[]{followUserId, userId};

        return this.jdbcTemplate.queryForObject(checkFollowStatusQuery, int.class, checkFollowStatusParams);
    }

    // 유저 테이블에 존재하는 유저인지 체크
    public int checkExistsUser(int userId){
        String checkExistsUserQuery = "select exists(select userId from user where userId=? and status=true)";
        return this.jdbcTemplate.queryForObject(checkExistsUserQuery, int.class, userId);
    }
}
