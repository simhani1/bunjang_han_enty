package com.example.demo.src.product;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PatchProductReq;
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

    /**
     * 상품 등록
     * @param userId
     * @param postProductReq
     * @return
     */
    public int createProduct(int userId, PostProductReq postProductReq){

        int productId;
        String createProductQuery;
        String lastInsertIdQuery;
        String insertProductImgQuery;
        String insertProductTagQuery;
        Object[] createProductParams;
        Object[] insertProductImgParams;
        Object[] insertProductTagParams;

        createProductQuery =
                "insert into product " +
                "(userId, title, firstCategoryId, lastCategoryId, price, contents, amount, isUsed, changeable, pay, shippingFee) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        createProductParams = new Object[]{userId, postProductReq.getTitle(), postProductReq.getFirstCategoryId(), postProductReq.getLastCategoryId(), postProductReq.getPrice(), postProductReq.getContents(), postProductReq.getAmount(), postProductReq.getIsUsed(), postProductReq.getChangeable(), postProductReq.getPay(), postProductReq.getShippingFee()};

        this.jdbcTemplate.update(createProductQuery, createProductParams);

        lastInsertIdQuery =
                "select count(*) " +
                "from product";
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
     *
     */
    public GetProductRes getProductById(int userId, int productId){

        String dateFormatQuery =
                "case when timestampdiff(second , product.updatedAt, current_timestamp) <60 " +
                "then concat(timestampdiff(second, product.updatedAt, current_timestamp),'초 전') " +
                "when timestampdiff(minute , product.updatedAt, current_timestamp) <60 " +
                "then concat(timestampdiff(minute, product.updatedAt, current_timestamp),'분 전') " +
                "when timestampdiff(hour , product.updatedAt, current_timestamp) <24 " +
                "then concat(timestampdiff(hour, product.updatedAt, current_timestamp),'시간 전') " +
                "when timestampdiff(day , product.updatedAt, current_timestamp) <365 " +
                "then concat(timestampdiff(day, product.updatedAt, current_timestamp),'일 전') " +
                "else concat(timestampdiff(year, current_timestamp, product.updatedAt),' 년 전') ";

        String getProductByIdQuery =
                "select product.productId, product.condition, product.price, product.pay, product.title, user.location, " +
                        dateFormatQuery + "end as 'updatedAt', " +
                        "product.isUsed, product.amount, product.shippingFee, " +
                        "product.changeable, product.contents, lastCategory.lastCategoryImgUrl, lastCategory.lastCategory, " +
                        "user.profileImgUrl, user.nickname " +
                        "from product " +
                        "left join user on user.userId = product.userId " +
                        "left join lastCategory on product.lastCategoryId = lastCategory.lastCategoryId " +
                        "where product.productId = ? " +
                        "order by product.updatedAt desc";
        int getProductByIdParams = productId;


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


        return this.jdbcTemplate.queryForObject(getProductByIdQuery,
                (rs, rowNum) -> new GetProductRes(
                        rs.getInt("productId"),
                        rs.getString("condition"),
                        getProductImg,
                        rs.getInt("price"),
                        rs.getBoolean("pay"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("updatedAt"),
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


    /**
     * 상품 마지막 번호 추출
     * @return
     */
    public int getLastProductId(){
        String getLastProductIdQuery = "select count(*) from product";
        return this.jdbcTemplate.queryForObject(getLastProductIdQuery, int.class);
    }


    //    private List<String> productImgs;
    //    private String title;
    //    private int firstCategoryId;
    //    private int lastCategoryId;
    //    private List<String> tags;
    //    private int price;
    //    private String contents;
    //    private int amount;
    //    private Boolean isUsed;
    //    private Boolean changeable;
    //    private Boolean pay;
    //    private Boolean shippingFee;

    public int modifyProduct(int userId, int productId, PatchProductReq patchProductReq){

        String modifyProductQuery;
        String selectProductImgIdQuery;
        String selectTagIdQuery;
        String deleteProductImgQuery;
        String modifyProductImgQuery;
        String deleteTagsQuery;
        String modifyTagsQuery;
        Object[] modifyProductParams;
        Object[] modifyProductImgParams;
        Object[] modifyProductTagParams;
        List<Integer> productImgId;
        List<Integer> tagId;

        // 상품 항목 수정
        modifyProductQuery = "update product set title=?, firstCategoryId=?, lastCategoryId=?, price=?, contents=?, amount=?, isUsed=?, changeable=?, pay=?, shippingFee=? where productId=? and userId=?";
        modifyProductParams = new Object[]{patchProductReq.getTitle(), patchProductReq.getFirstCategoryId(), patchProductReq.getLastCategoryId(), patchProductReq.getPrice(), patchProductReq.getContents(), patchProductReq.getAmount(), patchProductReq.getIsUsed(), patchProductReq.getChangeable(), patchProductReq.getPay(), patchProductReq.getShippingFee(), productId, userId};

        // Img, tag Id 추출
        selectProductImgIdQuery = "select productImgId from productImg where productId="+productId;
        productImgId = this.jdbcTemplate.queryForList(selectProductImgIdQuery, int.class);
        selectTagIdQuery = "select productTagId from productTag where productId="+productId;
        tagId = this.jdbcTemplate.queryForList(selectTagIdQuery,int.class);

        System.out.println(productImgId.get(0));
        // 삭제 후 다시 삽입
        deleteProductImgQuery = "delete from productImg where productImgId=?";
        modifyProductImgQuery = "insert into productImg (productId, productImgUrl) values (?,?)";
        deleteTagsQuery = "delete from productTag where productTagId=?";
        modifyTagsQuery = "insert into productTag (productId, tagContents) values (?,?)";


        // delete 작업
        for(int i = 0; i < productImgId.size(); i++){
            this.jdbcTemplate.update(deleteProductImgQuery,productImgId.get(i));
        }
        for(int i = 0; i < tagId.size(); i++){
            this.jdbcTemplate.update(deleteTagsQuery,tagId.get(i));
        }

        // 다시 insert 작업
        for(int i = 0; i < patchProductReq.getProductImgs().size(); i++){
            modifyProductImgParams = new Object[]{productId, patchProductReq.getProductImgs().get(i)};
            this.jdbcTemplate.update(modifyProductImgQuery,modifyProductImgParams);
        }
        for(int i = 0; i < patchProductReq.getTags().size(); i++){
            modifyProductTagParams = new Object[]{productId, patchProductReq.getTags().get(i)};
            this.jdbcTemplate.update(modifyTagsQuery,modifyProductTagParams);
        }



        return this.jdbcTemplate.update(modifyProductQuery,modifyProductParams);

    }

    /**
     * 상품 상태변경
     * @param userId
     * @param productId
     * @param condition
     * @return
     */
    public int modifyProductCondition(int userId, int productId, String condition){
        String modifyProductConditionQuery = "update product set product.condition=? where productId=? and userId=?";
        Object[] modifyProductConditionParams = new Object[]{condition, productId, userId};

        return this.jdbcTemplate.update(modifyProductConditionQuery, modifyProductConditionParams);
    }

    /**
     * 상품 삭제 여부
     * @param userId
     * @param productId
     * @param isDeleted
     * @return
     */
    public int modifyProductIsDeleted(int userId, int productId, Boolean isDeleted){
        String modifyProductIsDeletedQuery = "update product set isDeleted=? where productId=? and userId=?";
        Object[] modifyProductIsDeletedParams = new Object[]{isDeleted, productId, userId};

        return this.jdbcTemplate.update(modifyProductIsDeletedQuery, modifyProductIsDeletedParams);
    }
}
