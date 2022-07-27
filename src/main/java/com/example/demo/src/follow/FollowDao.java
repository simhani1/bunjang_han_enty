package com.example.demo.src.follow;

import com.example.demo.src.follow.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.awt.*;
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

    //팔로잉 목록 조회
    public List<GetFollowingRes> getFollowings(int userId) {
        // 팔로잉 userId List, profileImgUrl, nickname, productCount, followerNum
        String getFollowingsQuery =
                "select user.userId, user.profileImgUrl, user.nickname, B.productCount, C.followerNum " +
                "from user " +
                "left join " +
                "(select userId " +
                "from followList " +
                "where followUserId=? and status=true) as A on user.userId = A.userId " +
                "left join " +
                "(select userId, count(productId) as 'productCount' " +
                "from product " +
                "group by userId) as B on B.userId = A.userId " +
                "left join " +
                "(select userId, count(followUserId) as 'followerNum' " +
                "from followList " +
                "group by userId) as C on C.userId = A.userId " +
                "where user.userId = A.userId";
        int getFollowingsParam = userId;

        // 상품 추출하는 쿼리
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
        //productId 리스트 추출
        String getProductIdByFollowUserIdQuery =
                "select productId " +
                "from product " +
                "where userId=? " +
                "order by productId desc " +
                "limit 3";

        // product가 하나라도 있는지
        String checkExistsProductByUserIdQuery = "select exists(select productId from product where userId=?)";
        // 그 유저의 상품 개수
        String getProductCountQuery = "select count(productId) from product where userId=? and isDeleted=false and product.condition='sel' limit 3";

        // getFollowingUserInfo 리스트
        List<GetFollowingUserInfoRes> getFollowingUserInfoRes =
                this.jdbcTemplate.query(getFollowingsQuery,
                        (rs, rowNum) -> new GetFollowingUserInfoRes(
                                rs.getInt("userId"),
                                rs.getString("profileImgUrl"),
                                rs.getString("nickname"),
                                rs.getInt("productCount"),
                                rs.getInt("followerNum")),
                        getFollowingsParam);
        int userNum = getFollowingUserInfoRes.size();
        int productCount = 0;

        // 사진 리스트
        List<GetFollowingProductRes> getFollowingProductRes = null;
        // 팔로잉 목록
        List<GetFollowingRes> getFollowingRes = new ArrayList<>();
        // 유저 id리스트
        List<Integer> userIdList = new ArrayList<>();
        // 유저 당 상품 번호 리스트
        List<Integer> productIdList;


        for(int i = 0; i < userNum; i++) {
            productIdList = new ArrayList<>();
            getFollowingProductRes = new ArrayList<>();

            userIdList.add(getFollowingUserInfoRes.get(i).getUserId());

            // 그 유저의 상품이 하나라도 있으면
            if (1 == this.jdbcTemplate.queryForObject(checkExistsProductByUserIdQuery, int.class, userIdList.get(i))) {
                productCount = this.jdbcTemplate.queryForObject(getProductCountQuery, int.class, userIdList.get(i));

                // 상품이 3개 보다 많으면
                if (productCount > 3) {
                    // 3개의 productId값만 가져오기
                    for (int j = 0; j < 3; j++) {
                        productIdList.add(this.jdbcTemplate.query(getProductIdByFollowUserIdQuery,
                                (rs, rowNum) -> new GetProductIdRes(
                                        rs.getInt("productId"))
                                , userIdList.get(i)).get(j).getProductId());
                    }
                    // 3개의 productImg만 가져오기
                    for (int k = 0; k < 3; k++) {
                        getFollowingProductRes.add(this.jdbcTemplate.queryForObject(getProductImgPriceQuery,
                                (rs, rowNum) -> new GetFollowingProductRes(
                                        rs.getInt("productId"),
                                        rs.getString("productImgUrl"),
                                        rs.getInt("price")),
                                productIdList.get(k)));
                    }
                    // 상품이 3개보다 적으면
                } else if (productCount < 3) {
                    getFollowingProductRes = new ArrayList<>();

                    // 상품 개수 (productCount) 만큼만 -> 안해주면 오류남
                    for (int j = 0; j < productCount; j++) {
                        productIdList.add(this.jdbcTemplate.query(getProductIdByFollowUserIdQuery,
                                (rs, rowNum) -> new GetProductIdRes(
                                        rs.getInt("productId"))
                                , userIdList.get(i)).get(j).getProductId());
                    }
                    for (int k = 0; k < productCount; k++) {
                        getFollowingProductRes.add(this.jdbcTemplate.queryForObject(getProductImgPriceQuery,
                                (rs, rowNum) -> new GetFollowingProductRes(
                                        rs.getInt("productId"),
                                        rs.getString("productImgUrl"),
                                        rs.getInt("price")),
                                productIdList.get(k)));
                    }
                }

            }
            List<GetFollowingProductRes> finalGetFollowingProductRes = getFollowingProductRes;
            // 유저 한명 끝나면 팔로잉 리스트에 추가
            getFollowingRes.add(this.jdbcTemplate.query(getFollowingsQuery,
                    (rs, rowNum) -> new GetFollowingRes(
                            rs.getInt("userId"),
                            rs.getString("profileImgUrl"),
                            rs.getString("nickname"),
                            rs.getInt("productCount"),
                            rs.getInt("followerNum"),
                            finalGetFollowingProductRes),
                    getFollowingsParam).get(i));
        }

        return getFollowingRes;

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
