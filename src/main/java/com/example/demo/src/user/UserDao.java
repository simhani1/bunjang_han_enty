package com.example.demo.src.user;


import com.example.demo.src.product.model.GetProductIdRes;
import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class UserDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    @Transactional
    // 회원가입
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery;
        Object[] createUserParams;
        // 프로필 사진 지정하지 않은 경우
        if(postUserReq.getProfileImgUrl().equals("")){
            if(postUserReq.getPwd().equals("") || postUserReq.getPwd().isEmpty()){
                createUserQuery = "insert into user (nickname, location, phoneNum) VALUES (?,?,?)";
                createUserParams = new Object[]{postUserReq.getNickname(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
            }
            else{
                createUserQuery = "insert into user (id, pwd, nickname, location, phoneNum) VALUES (?,?,?,?,?)";
                createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
            }
        }
        // 프로필 사진 지정한 경우
        else{
            if(postUserReq.getPwd().equals("") || postUserReq.getPwd().isEmpty()){
                createUserQuery = "insert into user (nickname, profileImgUrl, location, phoneNum) VALUES (?,?,?,?)";
                createUserParams = new Object[]{postUserReq.getNickname(), postUserReq.getProfileImgUrl(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
            }
            else{
                createUserQuery = "insert into user (id, pwd, nickname, profileImgUrl, location, phoneNum) VALUES (?,?,?,?,?,?)";
                createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getProfileImgUrl(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
            }
        }
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        String lastInsertIdQuery = "select count(*) from user";
        String createShopQuery = "insert\n" +
                "    into shop (userId)\n" +
                "    values (?)";
        String createShopParams = lastInsertIdQuery;
        this.jdbcTemplate.update(createShopQuery, jdbcTemplate.queryForObject(createShopParams, int.class));
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userId를 반환한다.
    }

    // 로그인 - 비밀번호 체크
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userId, pwd from user where id = ?";
        String getPwdParams = postLoginReq.getId(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userId"),
                        rs.getString("pwd")
                ),
                getPwdParams
        );
    }

    // 폰번호 수정
    public int modifyPhoneNum(int userId, String phoneNum) {
        String modifyPhoneNumQuery = "update user set phoneNum = ? where userId = ? ";
        Object [] modifyPhoneNumParams = new Object[]{phoneNum, userId};
        return this.jdbcTemplate.update(modifyPhoneNumQuery, modifyPhoneNumParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 성별 수정
    public int modifyGender(int userId, boolean gender) {
        String modifyGenderQuery = "update user set gender = ? where userId = ? ";
        Object [] modifyGenderParams = new Object[]{gender, userId};
        return this.jdbcTemplate.update(modifyGenderQuery, modifyGenderParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 생일 수정
    public int modifyBirth(int userId, String birth) {
        String modifyGenderQuery = "update user set birth = ? where userId = ? ";
        Object [] modifyGenderParams = new Object[]{birth, userId};
        return this.jdbcTemplate.update(modifyGenderQuery, modifyGenderParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 회원탈
    public int withdrawl(int userId, boolean status) {
        String withdrawlQuery = "update user set status = ? where userId = ? ";
        Object [] withdrawlParams = new Object[]{status, userId};
        return this.jdbcTemplate.update(withdrawlQuery, withdrawlParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 해당 유저의 판매중/예약중/판매완료 물건 id값을 저장
    public List<GetProductIdRes> getProductId(int userId, String condition){
        // 해당 사용자가 등록한 판매글 id를 리스트에 저장
        String getProductIdQuery = "select productId from product where userId = ? and `condition` = ?";
        Object[] getProductIdParams = new Object[]{userId,condition};
        List<GetProductIdRes> getProductId = this.jdbcTemplate.query(getProductIdQuery,
                (rs, rowNum) -> new GetProductIdRes(
                        rs.getInt("productId")),
                getProductIdParams);
        return getProductId;
    }

    // 해당 유저의 판매중/예약중/판매완료 물건 조회
    public GetUserProductRes getUserProductRes(int userId, int otherId, int productId, String condition) {
        // 해당 상품의 사진 다 넘기기
        String getProductImgQuery = "select\n" +
                "    productImgUrl\n" +
                "from productImg\n" +
                "inner join product on productImg.productId = product.productId\n" +
                "where product.`condition` = ? and product.userId = ? and productImg.productId = ?";
        Object[] getProductImgParam = new Object[]{condition, otherId, productId};
        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs,rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                getProductImgParam);
        // 나머지 정보 넘기기
        String getUserProductQuery = "select\n" +
                "    (select nickname from user where user.userId = ?) as 'nickname',\n" +
                "    product.productId as 'productId',\n" +
                "    product.pay as 'pay',\n" +
                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
                "        then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
                "        when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
                "            then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
                "        when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
                "            then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
                "        else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
                "        end as 'updatedAt',\n" +
                "    product.title as 'title',\n" +
                "    product.price as 'price',\n" +
                "    product.userId as 'userId',\n" +
                "    product.productId as 'productId',\n" +
                "    (select exists(select heartId from heartList where heartList.userId = ? and heartList.productId = ? and status = true)) as 'heart'\n" +
                "from product\n" +
                "where product.userId = ? and product.productId = ? and product.`condition`= ?\n" +
                "order by product.updatedAt desc";
        Object[] getUserProductParams = new Object[]{userId, userId, productId, otherId, productId, condition};
        return this.jdbcTemplate.queryForObject(getUserProductQuery,
                (rs, rowNum) -> new GetUserProductRes(
                        rs.getString("nickname"),
                        getProductImg,
                        rs.getInt("productId"),
                        rs.getInt("userId"),
                        rs.getBoolean("pay"),
                        rs.getString("updatedAt"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getBoolean("heart")),
                getUserProductParams);
    }

    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
    public GetMyPageRes getMyPage(int userId) {
        String getMyPageQuery = "select\n" +
                "    (select profileImgUrl from user where userId = ?) as 'profileImgUrl',\n" +
                "    (select nickname from user where userId = ?) as 'nickname',\n" +
                "    (select\n" +
                "         avg(star)\n" +
                "     from review\n" +
                "        inner join product on product.productId = review.productId and product.userId = ? where product.isDeleted = false) as 'star',\n" +
                "    count(heartId) as 'heartCnt',\n" +
                "    (select\n" +
                "         count(*)\n" +
                "     from review\n" +
                "        inner join product on product.productId = review.productId and product.userId = ? where product.isDeleted = false) as 'reviewCnt',\n" +
                "    (select count(*) from followList where followList.userId = ? and followList.status = true) as 'followerCnt',\n" +
                "    (select count(*) from followList where followList.followUserId = ? and followList.status = true) as 'followingCnt'\n" +
                "from heartList\n" +
                "inner join product on product.productId = heartList.productId and product.isDeleted = false\n" +
                "where heartList.userId = ? and heartList.status = true"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        Object[] getMyPageParams = new Object[]{userId, userId, userId, userId, userId, userId, userId};
        return this.jdbcTemplate.queryForObject(getMyPageQuery,
                (rs, rowNum) -> new GetMyPageRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getDouble("star"),
                        rs.getInt("heartCnt"),
                        rs.getInt("reviewCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getMyPageParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 상점후기 조회
    public GetShopReviewRes getShopReview(int userId, int productId) {
        String getShopReviewQuery = "select\n" +
                "    user.profileImgUrl as 'profileImgUrl',\n" +
                "    user.nickname as 'nickname',\n" +
                "    user.userID as 'userId',\n" +
                "    review.star as 'star',\n" +
                "    review.reviewContents as 'reviewContents',\n" +
                "    review.productId as 'productId',\n" +
                "    product.title as 'title',\n" +
                "    case when timestampdiff(second , review.updatedAt, current_timestamp) <60\n" +
                "        then concat(timestampdiff(second, review.updatedAt, current_timestamp),'초 전')\n" +
                "        when timestampdiff(minute , review.updatedAt, current_timestamp) <60\n" +
                "            then concat(timestampdiff(minute, review.updatedAt, current_timestamp),'분 전')\n" +
                "        when timestampdiff(hour , review.updatedAt, current_timestamp) <24\n" +
                "            then concat(timestampdiff(hour, review.updatedAt, current_timestamp),'시간 전')\n" +
                "        else concat(datediff(current_timestamp, review.updatedAt),'일 전')\n" +
                "        end as 'updatedAt',\n" +
                "    review.userId as 'userId',\n" +
                "    review.updatedAt as 'time'\n" +
                "from review\n" +
                "inner join product on product.productId = review.productId and product.condition = 'fin' and product.userId = ?\n" +
                "inner join user on review.userId = user.userId\n" +
                "where review.productId = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        Object[] getShopReviewParams = new Object[]{userId, productId};
        return this.jdbcTemplate.queryForObject(getShopReviewQuery,
                (rs, rowNum) -> new GetShopReviewRes(
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getInt("userId"),
                        rs.getDouble("star"),
                        rs.getString("reviewContents"),
                        rs.getInt("productId"),
                        rs.getString("title"),
                        rs.getString("updatedAt"),
                        rs.getTimestamp("time")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getShopReviewParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 찜목록 조회
    public GetHeartProductsRes getHeartProducts(int userId, int productId){
        // 해당 상품의 사진 다 넘기기
        String getProductImgQuery = "select\n" +
                "    productImgUrl\n" +
                "from productImg\n" +
                "inner join product on productImg.productId = product.productId\n" +
                "where productImg.productId = ?";
        int getProductImgParam = productId;
        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs,rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                getProductImgParam);
        // 나머지 정보 저장
        String getHeartProductsQuery = "select\n" +
                "    product.productId as 'productId',\n" +
                "    product.userId as 'userId',\n" +
                "    heartList.status as 'heart',\n" +
                "    product.pay as 'pay',\n" +
                "    product.title as 'title',\n" +
                "    product.price as 'price',\n" +
                "    user.profileImgUrl as 'profileImgUrl',\n" +
                "    user.nickname as 'nickname',\n" +
                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
                "        then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
                "        when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
                "            then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
                "        when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
                "            then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
                "        else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
                "        end as 'updatedAt'\n" +
                "from product\n" +
                "inner join heartList on heartList.productId = product.productId and heartList.userId = ?\n" +
                "inner join user on user.userId = product.userId\n" +
                "where product.productId = ?";
        Object[] getHeartProductsParams = new Object[]{userId, productId};
        return this.jdbcTemplate.queryForObject(getHeartProductsQuery,
                (rs, rowNum) -> new GetHeartProductsRes(
                        getProductImg,
                        rs.getInt("productId"),
                        rs.getInt("userId"),
                        rs.getBoolean("heart"),
                        rs.getBoolean("pay"),
                        rs.getString("title"),
                        rs.getInt("price"),
                        rs.getString("profileImgUrl"),
                        rs.getString("nickname"),
                        rs.getString("updatedAt")),
                getHeartProductsParams
                );
    }

    // 삭제되지 않고 찜한 물건 id값 배열로 저장
    public List<Integer> getHeartProductsId(int userId) {
        String getHeartProductsIdQuery = "select heartList.productId\n" +
                "from heartList\n" +
                "inner join product on product.productId = heartList.productId\n" +
                "where product.isDeleted = false and heartList.userId = ? and heartList.status = true\n" +
                "order by heartList.productId";
        int getHeartProductsIdParams = userId;
        return this.jdbcTemplate.queryForList(getHeartProductsIdQuery, int.class, getHeartProductsIdParams);
    }

    // 찜하기(최초 찜하기)
    public int addHeartList(int userId, int productId, boolean status) {
        String addHeartListQuery = "insert into heartList (userId, productId, status) VALUES (?,?,?)";
        Object[] addHeartListParmas = new Object[]{userId, productId, status};
        return this.jdbcTemplate.update(addHeartListQuery, addHeartListParmas);
    }

    // 찜하기 / 찜해제
    public int addHeartList_modify(int userId, int productId, boolean status) {
        String addHeartListQuery = "update heartList set status = ? where userId = ? and productId = ? ";
        Object[] addHeartListParmas = new Object[]{status, userId, productId};
        return this.jdbcTemplate.update(addHeartListQuery, addHeartListParmas);
    }

    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////

    // 해당 아이디 중복성 체크
    public int checkId(String id) {
        String checkIdQuery = "select exists(select id from user where id = ?)";
        String checkIdParams = id;
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 닉네임 중복성 체크
    public int checkNickname(String nickname) {
        String checkNicknameQuery = "select exists(select nickname from user where nickname = ?)";
        String checkNicknameParams = nickname;
        return this.jdbcTemplate.queryForObject(checkNicknameQuery,
                int.class,
                checkNicknameParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 전화번호 중복성 체크
    public int checkPhoneNum(String phoneNum) {
        String checkPhoneNumQuery = "select exists(select phoneNum from user where phoneNum = ?)";
        String checkPhoneNumParams = phoneNum;
        return this.jdbcTemplate.queryForObject(checkPhoneNumQuery,
                int.class,
                checkPhoneNumParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 이메일 중복성 체크
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from user where email = ?)";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }


    // 탈퇴한 유저인지 체크
    public String checkStatus(String id) {
        String checkStatusQuery = "select status from user where id = ?";
        String checkStatusParams = id;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                String.class,
                checkStatusParams);  // 쿼리문의 결과(활동중: active, 비활성: inactive)를 문자열로 반환
    }

    // 해당 글이 삭제됐는지 체크
    public boolean checkProductIsDeleted(int productId){
        String getProductIsDeletedQuery = "select product.isDeleted from product where productId = ?";
        int getProductIsDeletedParams = productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeletedQuery, boolean.class, getProductIsDeletedParams);  // true: 삭제  false: 삭제x
    }

    // 해당 물건이 존재하는 물건인지 체크
    public boolean checkProductExist(int productId){
        String getProductIsDeletedQuery = "select exists(select productId from product where productId = ?)";
        int getProductIsDeletedParams = productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeletedQuery, boolean.class, getProductIsDeletedParams);  // true: 존재  false: 존재x
    }

    // 판매완료 물건인지 체크
    public boolean checkProductCondition(int productId){
        String getProductIsDeletedQuery = "select exists(select productId from product where productId = ? and `condition` = 'fin')";
        int getProductIsDeletedParams = productId;
        return this.jdbcTemplate.queryForObject(getProductIsDeletedQuery, boolean.class, getProductIsDeletedParams);  // true: 판매완료  false: 판매완료x
    }

    // 본인의 상품인지 체크
    public int checkProductOwner(int userId, int productId){
        String checkProductOwnerQuery = "select exists(select productId from product where userId = ? and productId = ?)";
        Object[] checkProductOwnerParams = new Object[]{userId, productId};
        return this.jdbcTemplate.queryForObject(checkProductOwnerQuery,
                int.class,
                checkProductOwnerParams);  // 본인의 상품인 경우 1반환
    }

    // 이미 찜해둔 물건인지 체크
    public int checkHeartListExists(int userId, int productId){
        String checkHeartListExistsQuery = "select exists(select heartId from heartList where userId = ? and productId = ?)";
        Object[] checkHeartListExistsParams = new Object[]{userId, productId};
        return this.jdbcTemplate.queryForObject(checkHeartListExistsQuery,
                int.class,
                checkHeartListExistsParams);  // 이미 찜해둔 경우 1반환
    }

    // 상점 후기 작성
    public int postShopReview(int userId, PostShopReviewReq postShopReviewReq) {
        String postShopReviewQuery = "insert into review (userId, productId, star, reviewContents) VALUES (?,?,?,?)";
        Object[] postShopReviewParams= new Object[]{userId, postShopReviewReq.getProductId(), postShopReviewReq.getStar(), postShopReviewReq.getReviewContents()};
        return this.jdbcTemplate.update(postShopReviewQuery, postShopReviewParams);
    }

    // 작성된 리뷰가 있는지 체크
    public boolean checkReviewExist(int productId){
        String checkHeartListExistsQuery = "select exists(select reviewId from review where productId = ?)";
        int checkHeartListExistsParams = productId;
        return this.jdbcTemplate.queryForObject(checkHeartListExistsQuery,
                boolean.class,
                checkHeartListExistsParams);  // 이미 찜해둔 경우 1반환
    }

}
