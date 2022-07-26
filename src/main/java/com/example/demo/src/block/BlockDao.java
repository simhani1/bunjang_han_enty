package com.example.demo.src.block;

import com.example.demo.src.block.model.DeleteBlockRes;
import com.example.demo.src.block.model.GetBlockRes;
import com.example.demo.src.block.model.PostBlockRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class BlockDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 유저 차단
    public PostBlockRes addBlockList(int userId, int blockUserId){
        String addBlockListQuery = "insert into block (userId, blockUserId) values (?,?)";
        String getBlockListQuery = "select * from block where userId=? and blockUserId=?";
        Object[] addBlockListParams = new Object[]{userId, blockUserId};

        this.jdbcTemplate.update(addBlockListQuery, addBlockListParams);
        return this.jdbcTemplate.queryForObject(getBlockListQuery,
                (rs, rowNum) -> new PostBlockRes(
                        rs.getInt("userId"),
                        rs.getInt("blockUserId")),
                addBlockListParams);
    }

    public List<GetBlockRes> getBlockListByUserId(int userId){
        String getBlockListByUserIdQuery =
                "select block.userId, block.blockUserId, user.profileImgUrl, user.nickname, block.createdAt " +
                "from block " +
                "left join user on user.userId = block.blockUserId " +
                "where block.userId = ?";
        int getBlockListByUserIdParam = userId;

        return this.jdbcTemplate.query(getBlockListByUserIdQuery,
                (rs, rowNum) -> new GetBlockRes(
                        rs.getInt("userId"),
                        rs.getInt("blockUserId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getTimestamp("createdAt")),
                getBlockListByUserIdParam);
    }

    // 차단 해제
    public int deleteBlockUser(int userId, int blockUserId){
        String deleteBlockUserQuery = "delete from block where userId=? and blockUserId=?";
        Object[] deleteBlockUserParams = new Object[]{userId, blockUserId};

        return this.jdbcTemplate.update(deleteBlockUserQuery, deleteBlockUserParams);
    }
     // 이미 차단된 유저인지 확인
    public int checkExistsBlockUser(int userId, int blockUserId){
        String getExistsBlockUserQuery = "select exists(select blockId from block where userId=? and blockUserId=?)";
        Object[] getExistsBlockUserParams = new Object[]{userId, blockUserId};

        return this.jdbcTemplate.queryForObject(getExistsBlockUserQuery, int.class, getExistsBlockUserParams);
    }

    // 유저 테이블에 존재하는 유저인지 확인
    public int checkExistsUser(int userId){
        String getExistsUserQuery = "select exists(select userId from user where userId=?)";
        int getExistsUserParam = userId;

        return this.jdbcTemplate.queryForObject(getExistsUserQuery,int.class, getExistsUserParam);
    }
}
