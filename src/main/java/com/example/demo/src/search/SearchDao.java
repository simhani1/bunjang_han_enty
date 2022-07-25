package com.example.demo.src.search;

import com.example.demo.src.productImg.model.GetProductImgRes;
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
        String getStarQuery = "select avg(star) from review where productId="+productId;
        Double star = this.jdbcTemplate.queryForObject(getStarQuery, Double.class);

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
                        rs.getTimestamp("time")),
                productId);
    }
//    @Transactional
//    // 회원가입
//    public int createUser(PostUserReq postUserReq) {
//        String createUserQuery;
//        Object[] createUserParams;
//        // 프로필 사진 지정하지 않은 경우
//        if(postUserReq.getProfileImgUrl().equals("")){
//            createUserQuery = "insert into user (id, pwd, nickname, location, phoneNum) VALUES (?,?,?,?,?)";
//            createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
//        }
//        // 프로필 사진 지정한 경우
//        else{
//            createUserQuery = "insert into user (id, pwd, nickname, profileImgUrl, location, phoneNum) VALUES (?,?,?,?,?,?)";
//            createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getProfileImgUrl(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
//        }
//        this.jdbcTemplate.update(createUserQuery, createUserParams);
//        String lastInsertIdQuery = "select count(*) from user";
//        String createShopQuery = "insert\n" +
//                "    into shop (userId)\n" +
//                "    values (?)";
//        String createShopParams = lastInsertIdQuery;
//        this.jdbcTemplate.update(createShopQuery, jdbcTemplate.queryForObject(createShopParams, int.class));
//        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userId를 반환한다.
//    }
//
//    // 로그인 - 비밀번호 체크
//    public User getPwd(PostLoginReq postLoginReq) {
//        String getPwdQuery = "select userId, pwd from user where id = ?";
//        String getPwdParams = postLoginReq.getId(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs, rowNum) -> new User(
//                        rs.getInt("userId"),
//                        rs.getString("pwd")
//                ),
//                getPwdParams
//        );
//    }
//
//    // 사용자 정보 수정(폰번호/성별/생일)
////    public int modifyInfo(PatchUserReq patchUserReq) {
////        String modifyInfoQuery = "";
////        Object [] modifyInfoParams = new Object[]{};
////        if(patchUserReq.getKey().equals("phoneNum")){
////            modifyInfoQuery = "update user set phoneNum = ? where userId = ? ";
////            modifyInfoParams = new Object[]{patchUserReq.getPhoneNum(), patchUserReq.getUserId()};
////        }
////        else if(patchUserReq.getKey().equals("gender")){
////            modifyInfoQuery = "update user set gender = ? where userId = ? ";
////            modifyInfoParams = new Object[]{patchUserReq.isGender(), patchUserReq.getUserId()};
////        }
////        else if(patchUserReq.getKey().equals("birth")){
////            modifyInfoQuery = "update user set birth = ? where userId = ? ";
////            modifyInfoParams = new Object[]{patchUserReq.getBirth(), patchUserReq.getUserId()};
////        }
////        return this.jdbcTemplate.update(modifyInfoQuery, modifyInfoParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
////    }
//    // 폰번호 수정
//    public int modifyPhoneNum(int userId, String phoneNum) {
//        String modifyPhoneNumQuery = "update user set phoneNum = ? where userId = ? ";
//        Object [] modifyPhoneNumParams = new Object[]{phoneNum, userId};
//        return this.jdbcTemplate.update(modifyPhoneNumQuery, modifyPhoneNumParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//    // 성별 수정
//    public int modifyGender(int userId, boolean gender) {
//        String modifyGenderQuery = "update user set gender = ? where userId = ? ";
//        Object [] modifyGenderParams = new Object[]{gender, userId};
//        return this.jdbcTemplate.update(modifyGenderQuery, modifyGenderParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//    // 생일 수정
//    public int modifyBirth(int userId, String birth) {
//        String modifyGenderQuery = "update user set birth = ? where userId = ? ";
//        Object [] modifyGenderParams = new Object[]{birth, userId};
//        return this.jdbcTemplate.update(modifyGenderQuery, modifyGenderParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//    // 회원탈
//    public int withdrawl(int userId, boolean status) {
//        String withdrawlQuery = "update user set status = ? where userId = ? ";
//        Object [] withdrawlParams = new Object[]{status, userId};
//        return this.jdbcTemplate.update(withdrawlQuery, withdrawlParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//    // 해당 유저의 판매중/예약중/판매완료 물건 id값을 저장
//    public List<GetProductIdRes> getProductId(int userId, String condition){
//        // 해당 사용자가 등록한 판매글 id를 리스트에 저장
//        String getProductIdQuery = "select productId from product where userId = ? and `condition` = ?";
//        Object[] getProductIdParams = new Object[]{userId,condition};
//        List<GetProductIdRes> getProductId = this.jdbcTemplate.query(getProductIdQuery,
//                (rs, rowNum) -> new GetProductIdRes(
//                        rs.getInt("productId")),
//                getProductIdParams);
//        return getProductId;
//    }
//
//    // 해당 유저의 판매중/예약중/판매완료 물건 조회
//    public GetUserProductRes getUserProductRes(int userId, int productId, String condition) {
//        // 해당 상품의 사진 다 넘기기
//        String getProductImgQuery = "select\n" +
//                "    productImgUrl\n" +
//                "from productImg\n" +
//                "inner join product on productImg.productId = product.productId\n" +
//                "where product.`condition` = ? and product.userId = ? and productImg.productId = ?";
//        Object[] getProductImgParam = new Object[]{condition, userId, productId};
//        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
//                (rs,rowNum) -> new GetProductImgRes(
//                        rs.getString("productImgUrl")),
//                getProductImgParam);
//        // 나머지 정보 넘기기
//        String getUserProductQuery = "select\n" +
//                "    product.productId as 'productId',\n" +
//                "    product.pay as 'pay',\n" +
//                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
//                "        then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
//                "        when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
//                "            then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
//                "        when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
//                "            then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
//                "        else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
//                "        end as 'updatedAt',\n" +
//                "    product.title as 'title',\n" +
//                "    product.price as 'price',\n" +
//                "    product.userId as 'userId',\n" +
//                "    product.productId as 'productId'\n" +
//                "from product\n" +
//                "where product.userId = ? and product.productId = ? and product.`condition`= ?\n" +
//                "order by product.updatedAt desc";
//        Object[] getUserProductParams = new Object[]{userId, productId, condition};
//        return this.jdbcTemplate.queryForObject(getUserProductQuery,
//                (rs, rowNum) -> new GetUserProductRes(
//                        getProductImg,
//                        rs.getInt("productId"),
//                        rs.getInt("userId"),
//                        rs.getBoolean("pay"),
//                        rs.getString("updatedAt"),
//                        rs.getString("title"),
//                        rs.getInt("price")),
//                getUserProductParams);
//    }
//
//    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
//    public GetMyPageRes getMyPage(int userId) {
//        String getMyPageQuery = "select\n" +
//                "    count(*) as 'heartCnt',\n" +
//                "    (select count(*) from review\n" +
//                "     inner join product on product.productId = review.productId and product.userId = ?) as 'reviewCnt',\n" +
//                "    (select count(*) from followList where followList.followUserId = ? and followList.status = true) as 'followerCnt',\n" +
//                "    (select count(*) from followList where followList.userId = ? and followList.status = true) as 'followingCnt'\n" +
//                "from heartList\n" +
//                "where userId = ? and heartList.status = true"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
//        Object[] getMyPageParams = new Object[]{userId, userId, userId, userId};
//        return this.jdbcTemplate.queryForObject(getMyPageQuery,
//                (rs, rowNum) -> new GetMyPageRes(
//                        rs.getInt("heartCnt"),
//                        rs.getInt("reviewCnt"),
//                        rs.getInt("followerCnt"),
//                        rs.getInt("followingCnt")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getMyPageParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
//    // 상점후기 조회
//    public GetShopReviewRes getShopReview(int productId) {
//        String getShopReviewQuery = "select\n" +
//                "    user.profileImgUrl as 'profileImgUrl',\n" +
//                "    user.nickname as 'nickname',\n" +
//                "    review.star as 'star',\n" +
//                "    review.reviewContents as 'reviewContents',\n" +
//                "    product.productId as 'productId',\n" +
//                "    product.title as 'title',\n" +
//                "    case when timestampdiff(second , review.updatedAt, current_timestamp) <60\n" +
//                "           then concat(timestampdiff(second, review.updatedAt, current_timestamp),' 초 전')\n" +
//                "           when timestampdiff(minute , review.updatedAt, current_timestamp) <60\n" +
//                "               then concat(timestampdiff(minute, review.updatedAt, current_timestamp),' 분 전')\n" +
//                "           when timestampdiff(hour , review.updatedAt, current_timestamp) <24\n" +
//                "               then concat(timestampdiff(hour, review.updatedAt, current_timestamp),' 시간 전')\n" +
//                "           else concat(datediff(current_timestamp, review.updatedAt),' 일 전')\n" +
//                "           end as 'updatedAt',\n" +
//                "    review.updatedAt as 'time'\n" +
//                "from user\n" +
//                "inner join product on product.buyerId = user.userId\n" +
//                "left join review on review.productId = ?\n" +
//                "where product.productId = ?\n" +
//                "order by review.updatedAt desc"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
//        Object[] getShopReviewParams = new Object[]{productId, productId};
//        return this.jdbcTemplate.queryForObject(getShopReviewQuery,
//                (rs, rowNum) -> new GetShopReviewRes(
//                        rs.getString("profileImgUrl"),
//                        rs.getString("nickname"),
//                        rs.getDouble("star"),
//                        rs.getString("reviewContents"),
//                        rs.getInt("productId"),
//                        rs.getString("title"),
//                        rs.getString("updatedAt"),
//                        rs.getTimestamp("time")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getShopReviewParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
//    // 찜하기(최초 찜하기)
//    public int addHeartList(int userId, int productId, boolean status) {
//        String addHeartListQuery = "insert into heartList (userId, productId, status) VALUES (?,?,?)";
//        Object[] addHeartListParmas = new Object[]{userId, productId, status};
//        return this.jdbcTemplate.update(addHeartListQuery, addHeartListParmas);
//    }
//
//    // 찜하기 / 찜해제
//    public int addHeartList_modify(int userId, int productId, boolean status) {
//        String addHeartListQuery = "update heartList set status = ? where userId = ? and productId = ? ";
//        Object[] addHeartListParmas = new Object[]{status, userId, productId};
//        return this.jdbcTemplate.update(addHeartListQuery, addHeartListParmas);
//    }
//
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
}
