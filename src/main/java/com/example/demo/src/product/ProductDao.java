package com.example.demo.src.product;

import com.example.demo.src.lastCategory.model.GetLastCategoryRes;
import com.example.demo.src.product.model.GetProductRes;
import com.example.demo.src.product.model.PatchProductReq;
import com.example.demo.src.product.model.PostProductReq;
import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.tag.model.GetTagRes;
import com.example.demo.src.user.model.GetUserRes;
//import com.sun.org.apache.xpath.internal.operations.Bool;
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
        String FormatData = "product.updatedAt";
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

        String getProductByIdQuery =
                "select product.productId, user.userId, product.condition, product.price, product.pay, product.title, user.location, " +
                        dateFormatQuery + "'updatedAt', " +
                        "product.isUsed, product.amount, product.shippingFee, " +
                        "product.changeable, product.contents, " +
                        "firstCategory.firstCategoryId, firstCategory.firstCategoryImgUrl, firstCategory.firstCategory, " +
                        "lastCategory.lastCategoryId, lastCategory.lastCategoryImgUrl, lastCategory.lastCategory, " +
                        "user.profileImgUrl, user.nickname, product.updatedAt as 'time' " +
                        "from product " +
                        "left join user on user.userId = product.userId " +
                        "left join firstCategory on product.firstCategoryId = firstCategory.firstCategoryId " +
                        "left join lastCategory on product.lastCategoryId = lastCategory.lastCategoryId " +
                        "where product.productId = ? and product.isDeleted=false";
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

        String getUserIdQuery = "select userId from product where productId="+productId;
        int productUserId = this.jdbcTemplate.queryForObject(getUserIdQuery, int.class);
        // Get star
        String getStarQuery =
                "select avg(review.star) " +
                "from review " +
                "left join " +
                "(select productId " +
                "from product " +
                "where userId=?) as A on review.productId = A.productId " +
                "where review.productId = A.productId";
        List<Double> star = this.jdbcTemplate.queryForList(getStarQuery, Double.class, productUserId);

        if(star.get(0) == null){
            star.set(0,0.0);
        }
        Double downStar = Math.floor(star.get(0) * 10) / 10.0;

        // Get follow
        String getFollowerNumQuery = "select count(userId) from followList where userId="+productUserId+" and status = true";
        int follower = this.jdbcTemplate.queryForObject(getFollowerNumQuery, int.class);

        // Get follow status
        String getFollowStatusQuery = "select status from followList where followUserId="+userId+" and userId="+productUserId;
        List<Boolean> follow = this.jdbcTemplate.queryForList(getFollowStatusQuery, boolean.class);

        // follow의 값이 null일때 해결
        if (follow.size() == 0){
            follow.add(false);
        }

        // Get CommentCount
        String getCommentCountQuery = "select count(productId) from comment where productId="+productId+" and isDeleted=0";
        int commentCount = this.jdbcTemplate.queryForObject(getCommentCountQuery,int.class);

        // Get heart
        String getHeartStatusQuery = "select status from heartList where userId="+userId+" and productId="+productId;
        List<Boolean> heart = this.jdbcTemplate.queryForList(getHeartStatusQuery, boolean.class);

        if (heart.size() == 0){
            heart.add(false);
        }
        return this.jdbcTemplate.queryForObject(getProductByIdQuery,
                (rs, rowNum) -> new GetProductRes(
                        rs.getInt("productId"),
                        rs.getInt("userId"),
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
                        // 여기부터
                        rs.getInt("firstCategoryId"),
                        rs.getString("firstCategoryImgUrl"),
                        rs.getString("firstCategory"),
                        rs.getInt("lastCategoryId"),
                        //여기까지
                        rs.getString("lastCategoryImgUrl"),
                        rs.getString("lastCategory"),
                        getTag,
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        downStar,
                        follower,
                        follow.get(0),
                        commentCount,
                        heart.get(0),
                        rs.getTimestamp("time")),
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

    // 삭제 안된 상품 최신순
    public List<Integer> getExistsProductIdListReCent(){
        String getProductIdListQuery = "select productId from product where isDeleted=false order by updatedAt desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class);
    }

    // 삭제 안된 상품 낮은가격순
    public List<Integer> getExistsProductIdListAscend(){
        String getProductIdListQuery = "select productId from product where isDeleted=false order by price";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class);
    }

    // 삭제 안된 상품 높은가격순
    public List<Integer> getExistsProductIdListDescend(){
        String getProductIdListQuery = "select productId from product where isDeleted=false order by price desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class);
    }

    // 삭제 안된 상품 최신순 by firstCategory
    public List<Integer> getExistsProductIdListReCentByFirstCategoryId(int firstCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and firstCategoryId = ? order by updatedAt desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, firstCategoryId);
    }

    // 삭제 안된 상품 낮은가격순 by firstCategory
    public List<Integer> getExistsProductIdListAscendByFirstCategoryId(int firstCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and firstCategoryId = ? order by price";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, firstCategoryId);
    }

    // 삭제 안된 상품 높은가격순 by firstCategory
    public List<Integer> getExistsProductIdListDescendByFirstCategoryId(int firstCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and firstCategoryId = ? order by price desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, firstCategoryId);
    }

    // 삭제 안된 상품 최신순 by lastCategory
    public List<Integer> getExistsProductIdListReCentByLastCategoryId(int lastCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and lastCategoryId = ? order by updatedAt desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, lastCategoryId);
    }

    // 삭제 안된 상품 낮은가격순 by lastCategory
    public List<Integer> getExistsProductIdListAscendByLastCategoryId(int lastCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and lastCategoryId = ? order by price";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, lastCategoryId);
    }

    // 삭제 안된 상품 높은가격순 by lastCategory
    public List<Integer> getExistsProductIdListDescendByLastCategoryId(int lastCategoryId){
        String getProductIdListQuery = "select productId from product where isDeleted=false and lastCategoryId = ? order by price desc";
        return this.jdbcTemplate.queryForList(getProductIdListQuery, int.class, lastCategoryId);
    }
    // 상품 수정
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
     * @return
     */
    public int modifyProductIsDeleted(int userId, int productId){
        String modifyProductIsDeletedQuery = "update product set isDeleted=true where productId=? and userId=?";
        Object[] modifyProductIsDeletedParams = new Object[]{productId, userId};

        return this.jdbcTemplate.update(modifyProductIsDeletedQuery, modifyProductIsDeletedParams);
    }

    public int flexProduct(int userId, int productId){
        String flexProductQuery = "update product set product.condition='fin', buyer="+userId+" where productId="+productId+" and product.condition != 'fin'";
        return this.jdbcTemplate.update(flexProductQuery);
    }
    // 상품 UP하기
    public int upProductById(int productId){
        String upProductByIdQuery = "update product set updatedAt=current_timestamp where productId=?";
        int upProductByIdParam = productId;

        return this.jdbcTemplate.update(upProductByIdQuery,upProductByIdParam);
    }

    // 상품 주인인지 확인
    public int checkExistsUserOwnProduct(int userId, int productId){
        String checkExistsUserOwnProductQuery = "select exists(select productId from product where userId=? and productId=?)";
        Object[] checkExistsUserOwnProductParams = new Object[]{userId, productId};

        return this.jdbcTemplate.queryForObject(checkExistsUserOwnProductQuery,int.class, checkExistsUserOwnProductParams);
    }
    // 상품이 존재하는지, 판매중인지
    public int checkExistsSellProduct(int productId){
        String checkExistsSellProductQuery = "select exists(select productId from product where productId=? and isDeleted=false and product.condition='sel')";
        int checkExistsSellProductParams = productId;

        return this.jdbcTemplate.queryForObject(checkExistsSellProductQuery, int.class, checkExistsSellProductParams);
    }
    public Boolean getProductIsDeleted(int productId){
        String getProductIsDeleted = "select product.isDeleted from product where productId="+productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeleted,Boolean.class);
    }

    public List<Integer> getProductIdList(int userId){
        String getProductIdListByUserId = "select productId from product where userId="+ userId +" order by productId desc";
        return this.jdbcTemplate.queryForList(getProductIdListByUserId, Integer.class);
    }

    public int getExistProductCount(){
        String getSellProductCountQuery = "select count(*) from product where isDeleted=false";
        return this.jdbcTemplate.queryForObject(getSellProductCountQuery, int.class);
    }

    public int getExistCategoryProductCount(int firstCategoryId){
        String getExistCategoryProductCountQuery = "select count(*) from product where isDeleted=false and firstCategoryId ="+firstCategoryId;
        return this.jdbcTemplate.queryForObject(getExistCategoryProductCountQuery, int.class);
    }

    public int getExistLastCategoryProductCount(int lastCategoryId){
        String getExistLastCategoryProductCountQuery = "select count(*) from product where isDeleted=false and lastCategoryId ="+lastCategoryId;
        return this.jdbcTemplate.queryForObject(getExistLastCategoryProductCountQuery, int.class);
    }


}
