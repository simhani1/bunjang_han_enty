package com.example.demo.src.product;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.GetUserRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ProductDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createProduct(int userId, PostProductReq postProductReq){

        int productId;
        String createProductQuery;
        String lastInsertIdQuery;
        String insertProductImgQuery;
        String insertProductTagQuery;
        Object[] createProductParams;
        Object[] insertProductImgParams;
        Object[] insertProductTagParams;

        createProductQuery = "insert into product (userId, title, firstCategoryId, lastCategoryId, price, contents, amount, isUsed, changeable, pay, shippingFee) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        createProductParams = new Object[]{userId, postProductReq.getTitle(), postProductReq.getFirstCategoryId(), postProductReq.getLastCategoryId(), postProductReq.getPrice(), postProductReq.getContents(), postProductReq.getAmount(), postProductReq.getIsUsed(), postProductReq.getChangeable(), postProductReq.getPay(), postProductReq.getShippingFee()};

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        lastInsertIdQuery = "select count(*) from product";
        productId = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);

        insertProductImgQuery = "insert into productImg (productId, productImgUrl) values (?,?)";
        insertProductTagQuery = "insert into productTag (productId, tagContents) values (?,?)";

        for(int i = 0; i < postProductReq.getProductImgs().size(); i++){
            insertProductImgParams = new Object[]{productId, postProductReq.getProductImgs().get(i)};
            this.jdbcTemplate.update(insertProductImgQuery, insertProductImgParams);
        }

        for(int i = 0; i < postProductReq.getTags().size(); i++){
            insertProductTagParams = new Object[]{productId, postProductReq.getTags().get(i)};
            this.jdbcTemplate.update(insertProductTagQuery, insertProductTagParams);
        }

        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class);
    }

    /**
     *
     * 상품 조회 API
     * [GET] /app/products/id/:productId
     * @return GetProductRes
     */
    public GetProductRes getProductById(int userId, int productId){

        String getProductByIdQuery = "select product.productId, product.condition, product.price, product.pay, product.title, user.location, product.updatedAt, product.isUsed, product.amount, product.shippingFee, product.changeable, product.contents, lastCategory.lastCategoryImgUrl, lastCategory.lastCategory, user.profileImgUrl, user.nickname from product left join user on user.userId = product.userId left join lastCategory on product.lastCategoryId = lastCategory.lastCategoryId where product.productId = ?";
        int getProductByIdParams = productId;


        // Get User
//        String getUserByProductIdQuery =
//                "select user.location, user.profileImgUrl, user.nickname from user left join product on user.userId = product.userId where product.productId = ?";
//
//        GetUserRes getUserRes = this.jdbcTemplate.queryForObject(getUserByProductIdQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userId"),
//                        rs.getString("profileImgUrl"),
//                        rs.getString("nickname")),
//                productId);


        // Get Category
//        String getCategoryInProduct =
//                "select lastCategory.categoryImgUrl, lastCategory.lastCategory from lastCategory left join product on lastCategory.lastCategoryId = product.lastCategoryId where product.productId = ?";
//
//        GetLastCategoryRes getLastCategoryRes = this.jdbcTemplate.queryForObject(getCategoryInProduct,
//                (rs, rowNum) -> new GetLastCategoryRes(
//                        rs.getInt("firstCategoryId"),
//                        rs.getInt("lastCategoryId"),
//                        rs.getString("lastCategory"),
//                        rs.getString("categoryImgUrl")),
//                productId);


        // Get ProductImg
        String getProductImgQuery = "select productImgUrl from productImg where productId = ?";

        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs, rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                productId);

        // Get tag
        String getProductTagQuery = "select tagContents from productTag where productId = ?";

        List<GetTagRes> getTag = this.jdbcTemplate.query(getProductTagQuery,
                (rs, rowNum) -> new GetTagRes(
                        rs.getString("tagContents")),
                productId);

        // Get viewCnt
        String getViewCountQuery = "select count(productId) from view where productId =" + productId;
        int viewCnt = this.jdbcTemplate.queryForObject(getViewCountQuery, int.class);

        // Get heartCnt
        String getHeartCountQuery = "select count(productId) from heartList where productId="+productId+" and status = 1";
        int heartCnt = this.jdbcTemplate.queryForObject(getHeartCountQuery, int.class);

        // Get chatCnt
        String getChatCountQuery = "select count(productId) from chattingRoom where productId="+productId+" and isDeleted=0";
        int chatCnt = this.jdbcTemplate.queryForObject(getChatCountQuery, int.class);

        // Get star
        String getStarQuery = "select avg(star) from review where productId="+productId;
        Double star = this.jdbcTemplate.queryForObject(getStarQuery, Double.class);

        // Get follow
        String getUserIdQuery = "select userId from product where productId="+productId;
        int productUserId = this.jdbcTemplate.queryForObject(getUserIdQuery, int.class);

        String getFollowerNumQuery = "select count(userId) from followList where userId="+productUserId+" and status = 1";
        int follower = this.jdbcTemplate.queryForObject(getFollowerNumQuery, int.class);

        // Get follow status
        String getFollowStatusQuery = "select status from followList where followUserId="+userId+" and userId="+productUserId;
        Boolean follow = this.jdbcTemplate.queryForObject(getFollowStatusQuery, Boolean.class);

        // Get CommentCount
        String getCommentCountQuery = "select count(productId) from comment where productId="+productId+" and isDeleted=0";
        int commentCount = this.jdbcTemplate.queryForObject(getCommentCountQuery,int.class);

//        String getAccountNumQuery = "select count(accountId) from accountList";
//        int accountNum = this.jdbcTemplate.queryForObject(getAccountNumQuery,int.class);

        return this.jdbcTemplate.queryForObject(getProductByIdQuery,
                (rs, rowNum) -> new GetProductRes(
                        rs.getInt("productId"),
                        rs.getString("condition"),
                        getProductImg,
                        rs.getInt("price"),
                        rs.getBoolean("pay"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getTimestamp("updatedAt").toLocalDateTime(),
                        viewCnt,
                        heartCnt,
                        chatCnt,
                        rs.getBoolean("isUsed"),
                        rs.getInt("amount"),
                        rs.getBoolean("shippingFee"),
                        rs.getBoolean("changeable"),
                        rs.getString("contents"),
                        rs.getString("lastCategoryImgUrl"),
                        rs.getString("lastCategory"),
                        getTag,
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        star,
                        follower,
                        follow,
                        commentCount),
                productId);
    }
}
