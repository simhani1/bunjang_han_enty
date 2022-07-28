package com.example.demo.src.search;

import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.search.model.GetKeywordsLogRes;
import com.example.demo.src.search.model.GetProductByKeywordRes;
import com.example.demo.src.tag.model.GetTagRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Repository
@Service
public class SearchDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    //  // 검색어로 판매글 검색
    public GetProductByKeywordRes getProductByKeyword(int userId, int productId){
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

        String getProductByKeywordQuery =
                "select product.productId, user.userId, product.condition, product.price, product.pay, product.title, user.location, " +
                        dateFormatQuery + "'updatedAt', " +
                        "product.isUsed, product.amount, product.shippingFee, " +
                        "product.changeable, product.contents, " +
                        "firstCategory.firstCategoryId, firstCategory.firstCategoryImgUrl, firstCategory.firstCategory, " +
                        "lastCategory.lastCategoryId, lastCategory.lastCategoryImgUrl, lastCategory.lastCategory, " +
                        "user.profileImgUrl, user.nickname, product.updatedAt as 'time'\n" +
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

        // Get star
        String getStarQuery =
                "select avg(star) " +
                        "from review " +
                        "left join (select productId from product where userId = (select userId from product where productId="+productId+")) " +
                        "as A on review.productId = A.productId " +
                        "where A.productId = review.productId";
        Double star = this.jdbcTemplate.queryForObject(getStarQuery, Double.class);

        Double downStar = Math.floor(star * 10) / 10.0;

        // Get follow
        String getUserIdQuery = "select userId from product where productId="+productId;
        int productUserId = this.jdbcTemplate.queryForObject(getUserIdQuery, int.class);

        String getFollowerNumQuery = "select count(userId) from followList where userId="+productUserId+" and status = 1";
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

        return this.jdbcTemplate.queryForObject(getProductByKeywordQuery,
                (rs, rowNum) -> new GetProductByKeywordRes(
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
                        star,
                        follower,
                        follow.get(0),
                        commentCount,
                        heart.get(0),
                        rs.getTimestamp("time")),
                productId);
    }

    // 검색할때 입력한 검색어 저장
    public void saveKeywordsLog (int userId, String keyword) {
        String svaveKeywordsLogQuery = "insert into keywordsLog (userId, keyword) values (?, ?)";
        Object[] saveKeywordsLogParams = new Object[]{userId, keyword};
        this.jdbcTemplate.update(svaveKeywordsLogQuery, saveKeywordsLogParams);
    }

    // 검색 내역이 있는지 체크
    public boolean existKeywordsLog (int userId, String keyword) {
        String existKeywordsLogQuery = "select exists(select logId from keywordsLog where keywordsLog.userId = ? and keywordsLog.keyword = ? and keywordsLog.isDeleted = false)";
        Object[] existKeywordsLogParams = new Object[]{userId, keyword};
        return this.jdbcTemplate.queryForObject(existKeywordsLogQuery, boolean.class, existKeywordsLogParams); // 이미 내역이 있으면 true
    }

    // 검색 내역 조회(최신 검색어 6개)
    public List<GetKeywordsLogRes> getKeywordsLog(int userId) {
        String getKeywordsLogQuery = "select\n" +
                "    keyword\n" +
                "from keywordsLog\n" +
                "where userId = ? and isDeleted = false\n" +
                "order by logId desc\n" +
                "limit 6";
        int getKeywordsLogParams = userId;
        return this.jdbcTemplate.query(getKeywordsLogQuery,
                (rs, rowNum) -> new GetKeywordsLogRes(
                        rs.getString("keyword")
                ),
                getKeywordsLogParams);
    }

    // 최근 검색어 전체 삭제
    public int removeKeywordsLog(int userId) {
        String removeKeywordsLogQuery = "update keywordsLog set isDeleted = true where userId = ?";
        int removeKeywordsLogParams = userId;
        return this.jdbcTemplate.update(removeKeywordsLogQuery, removeKeywordsLogParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 인기 검색어 조회
    public List<GetKeywordsLogRes> getHotKeywordsLog() {
        String getHotKeywordsLogQuery = "select keyword, keywordId from hotKeyword";
        return this.jdbcTemplate.query(getHotKeywordsLogQuery,
                (rs, rowNum) -> new GetKeywordsLogRes(
                        rs.getString("keyword"),
                        rs.getInt("keywordId")
                ));
    }

//    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////

    // 해당 글이 키워드를 포함하는지 여부 체크
    public boolean checkProductByKeyword(int productId, String keyword){
        String getProductByKeywordQuery = "select exists(select productId from product where productId = ? and (title REGEXP(?) or contents REGEXP(?)))";
        Object[] getProductByKeywordParams = new Object[]{productId, keyword, keyword};
        return this.jdbcTemplate.queryForObject(getProductByKeywordQuery,
                boolean.class,
                getProductByKeywordParams);
    }

    // 해당 글이 삭제됐는지 체크
    public boolean checkProductIsDeleted(int productId){
        String getProductIsDeletedQuery = "select product.isDeleted from product where productId = ?";
        int getProductIsDeletedParams = productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeletedQuery, boolean.class, getProductIsDeletedParams);  // true: 삭제  false: 삭제x
    }

    // 판매완료 물건인지 체크
    public boolean checkProductCondition(int productId){
        String getProductIsDeletedQuery = "select exists(select productId from product where productId = ? and `condition` = 'fin')";
        int getProductIsDeletedParams = productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeletedQuery, boolean.class, getProductIsDeletedParams);  // true: 판매완료
    }

    // 마지막 id값 반환
    public int getLastProductId(){
        String getLastProductIdQuery = "select count(*) from product";
        return this.jdbcTemplate.queryForObject(getLastProductIdQuery, int.class);
    }

    // 삭제되지 않고 keyword를 포함하는 물건 d값 배열로 저장
    public List<Integer> getExistProductsIdByKeyword(int userId, String keyword) {
        String getExistProductsIdQuery = "select productId from product where isDeleted = false and userId != ? and (title REGEXP(?) or contents REGEXP(?))";
        Object[] getExistProductsIdParams = new Object[]{userId, keyword, keyword};
        return this.jdbcTemplate.queryForList(getExistProductsIdQuery, int.class, getExistProductsIdParams);
    }
}