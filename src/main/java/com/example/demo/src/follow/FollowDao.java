package com.example.demo.src.follow;

import com.example.demo.src.follow.model.PostFollowRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class FollowDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int follow(int userId, int followUserId){
        String followQuery = "insert into followList (userId, followUserId) values(?,?)";
        Object[] followParams = new Object[]{followUserId, userId};

        return this.jdbcTemplate.update(followQuery, followParams);
    }

    public int switchFollowStatus(int userId, int followUserId, boolean status){
        String switchFollowStatusQuery = "update followList set status=? where userId = ? and followUserId = ?";
        Object[] switchFollowStatusParams = new Object[]{status, userId, followUserId};

        return this.jdbcTemplate.update(switchFollowStatusQuery, switchFollowStatusParams);
    }

    // 이미 팔로우 한 유저인지 체크
    public int checkFollowStatus(int userId, int followUserId){
        String checkFollowStatusQuery = "select exists(select followId from followList where userId=? and followUserId=?)";
        Object[] checkFollowStatusParams = new Object[]{followUserId, userId};

        return this.jdbcTemplate.queryForObject(checkFollowStatusQuery, int.class, checkFollowStatusParams);
    }

    public int checkExistsUser(int userId){
        String checkExistsUserQuery = "select exists(select userId from user where userId=? and status=true)";
        return this.jdbcTemplate.queryForObject(checkExistsUserQuery, int.class, userId);
    }
}
