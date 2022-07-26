package com.example.demo.src.follow;

import com.example.demo.src.follow.model.GetFollowerRes;
import com.example.demo.src.follow.model.GetFollowingProductRes;
import com.example.demo.src.follow.model.GetFollowingRes;
import com.example.demo.src.follow.model.GetFollowingUserInfoRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
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

    //팔로워 목록 조회
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

    //팔로잉 목록 조사
    public List<GetFollowingUserInfoRes> getFollowings(int userId) {
        String getFollowersQuery =
                "select user.userId, user.profileImgUrl, user.nickname, B.productCount, C.followerNum " +
                        "from user " +
                        "left join " +
                        "(select userId " +
                        "from followList " +
                        "where folloUserId=? and status=true) as A on user.userId = A.userId " +
                        "left join " +
                        "(select userId, count(productId) as 'productCount' " +
                        "from product " +
                        "group by userId) as B on B.userId = A.userId " +
                        "left join " +
                        "(select userId, count(followUserId) as 'followerNum' " +
                        "from followList " +
                        "group by userId) as C on C.userId = A.userId " +
                        "where user.userId = A.userId";
        int getFollowerParam = userId;

        //productId 리스트 추출
        String getProductIdByFollowUserIdQuery = "select productId, price from product where userId=? order by productId desc";

        // getFollowing 리스트
        List<GetFollowingUserInfoRes> getFollowingUserInfoRes = this.jdbcTemplate.query(getFollowersQuery,
                (rs, rowNum) -> new GetFollowingUserInfoRes(
                        rs.getInt("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getInt("productCount"),
                        rs.getInt("followerNum")),
                getFollowerParam);


        // productImg 하나씩 조회
        String getProductImgPriceQuery =
                "select product.productId, product.price, productImg.productImgId, productImg.productImgUrl " +
                "from product " +
                "left join productImg on product.productId = productImg.productId " +
                "left join (" +
                "select min(productImgId) as 'minImgId' " +
                "from productImg " +
                "where productId = ?" +
                ") as A on productImg.productImgId = A.minImgId " +
                "where productImg.productImgId = A.minImgId";
        this.jdbcTemplate.queryForObject(getProductImgPriceQuery,
                (rs, rowNum) -> new GetFollowingProductRes(
                        rs.getInt("productId"),
                        rs.getString("prouductImgUrl"),
                        rs.getInt("price")),
                5);

        //productList 빈 배열
        List<GetFollowingProductRes> getFollowingProductRes = new ArrayList<GetFollowingProductRes>();

        // following 유저 아이디 개수
        int userNum = getFollowingUserInfoRes.size();

        // following 유저 아이디 목록
        List<Integer> userIdList = new ArrayList<>();
        List<Integer> productIdList = new ArrayList<>();

        for(int i = 0; i < userNum; i++){
            for(int j = 0; j < 3; j++){
                getFollowingProductRes.add(this.jdbcTemplate.queryForObject(getProductImgPriceQuery,
                        (rs, rowNum) -> new GetFollowingProductRes(
                                rs.getInt("productId"),
                                rs.getString("prouductImgUrl"),
                                rs.getInt("price")),
                        this.jdbcTemplate.queryForList(getProductIdByFollowUserIdQuery,int.class, userIdList.get(i)).get(j)));
            }

        }



        return this.jdbcTemplate.query(getFollowersQuery,
                (rs, rowNum) -> new GetFollowingRes(
                        rs.getInt("userId"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getInt("productCount"),
                        rs.getInt("followerNum"),
                        getFollowingProductRes),
                getFollowerParam);

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