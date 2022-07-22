package com.example.demo.src.user;


import com.example.demo.src.productImg.model.GetProductImgRes;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.Date;
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
            createUserQuery = "insert into user (id, pwd, nickname, location, phoneNum) VALUES (?,?,?,?,?)";
            createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
        }
        // 프로필 사진 지정한 경우
        else{
            createUserQuery = "insert into user (id, pwd, nickname, profileImgUrl, location, phoneNum) VALUES (?,?,?,?,?,?)";
            createUserParams = new Object[]{postUserReq.getId(), postUserReq.getPwd(), postUserReq.getNickname(), postUserReq.getProfileImgUrl(), postUserReq.getLocation(), postUserReq.getPhoneNum()};
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

    // 사용자 정보 수정(폰번호/성별/생일)
//    public int modifyInfo(PatchUserReq patchUserReq) {
//        String modifyInfoQuery = "";
//        Object [] modifyInfoParams = new Object[]{};
//        if(patchUserReq.getKey().equals("phoneNum")){
//            modifyInfoQuery = "update user set phoneNum = ? where userId = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getPhoneNum(), patchUserReq.getUserId()};
//        }
//        else if(patchUserReq.getKey().equals("gender")){
//            modifyInfoQuery = "update user set gender = ? where userId = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.isGender(), patchUserReq.getUserId()};
//        }
//        else if(patchUserReq.getKey().equals("birth")){
//            modifyInfoQuery = "update user set birth = ? where userId = ? ";
//            modifyInfoParams = new Object[]{patchUserReq.getBirth(), patchUserReq.getUserId()};
//        }
//        return this.jdbcTemplate.update(modifyInfoQuery, modifyInfoParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
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

    // 본인의 판매중 상품 조회
    public List<GetUserProductRes> getUserProductRes_sel(int userId) {
        // 해당 상품의 사진 다 넘기기
        String getProductImgQuery = "select\n" +
                "    productImgUrl\n" +
                "from productImg\n" +
                "inner join product on product.productId = productImg.productId\n" +
                "where product.condition = 'sel' and userId = ?";
        int getProductImgParam = userId;
        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs,rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                getProductImgParam);
        // 나머지 정보 넘기기
        String getUserProductQuery = "select\n" +
                "    product.pay as 'pay',\n" +
                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
                "           then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
                "           when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
                "               then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
                "           when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
                "               then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
                "           else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
                "           end as 'updatedAt',\n" +
                "    product.title as 'title',\n" +
                "    product.price as 'price'\n" +
                "from product\n" +
                "where product.userId = ? and product.`condition`= 'sel'\n" +
                "order by product.updatedAt desc";
        int getUserProductParams = userId;
        return this.jdbcTemplate.query(getUserProductQuery,
                (rs, rowNum) -> new GetUserProductRes(
                        getProductImg,
                        rs.getBoolean("pay"),
                        rs.getString("updatedAt"),
                        rs.getString("title"),
                        rs.getInt("price")),
                getUserProductParams);
    }

    // 본인의 예약중 상품 조회
    public List<GetUserProductRes> getUserProductRes_res(int userId) {
        // 해당 상품의 사진 다 넘기기
        String getProductImgQuery = "select\n" +
                "    productImgUrl\n" +
                "from productImg\n" +
                "inner join product on product.productId = productImg.productId\n" +
                "where product.condition = 'sel' and userId = ?";
        int getProductImgParam = userId;
        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs,rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                getProductImgParam);
        // 나머지 정보 넘기기
        String getUserProductQuery = "select\n" +
                "    product.pay as 'pay',\n" +
                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
                "           then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
                "           when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
                "               then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
                "           when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
                "               then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
                "           else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
                "           end as 'updatedAt',\n" +
                "    product.title as 'title',\n" +
                "    product.price as 'price'\n" +
                "from product\n" +
                "where product.userId = ? and product.`condition`= 'res'\n" +
                "order by product.updatedAt desc";
        int getUserProductParams = userId;
        return this.jdbcTemplate.query(getUserProductQuery,
                (rs, rowNum) -> new GetUserProductRes(
                        getProductImg,
                        rs.getBoolean("pay"),
                        rs.getString("updatedAt"),
                        rs.getString("title"),
                        rs.getInt("price")),
                getUserProductParams);
    }

    // 본인의 판매완료 상품 조회
    public List<GetUserProductRes> getUserProductRes_sold_out(int userId) {
        // 해당 상품의 사진 다 넘기기
        String getProductImgQuery = "select\n" +
                "    productImgUrl\n" +
                "from productImg\n" +
                "inner join product on product.productId = productImg.productId\n" +
                "where product.condition = 'sel' and userId = ?";
        int getProductImgParam = userId;
        List<GetProductImgRes> getProductImg = this.jdbcTemplate.query(getProductImgQuery,
                (rs,rowNum) -> new GetProductImgRes(
                        rs.getString("productImgUrl")),
                getProductImgParam);
        // 나머지 정보 넘기기
        String getUserProductQuery = "select\n" +
                "    product.pay as 'pay',\n" +
                "    case when timestampdiff(second , product.updatedAt, current_timestamp) <60\n" +
                "           then concat(timestampdiff(second, product.updatedAt, current_timestamp),' 초 전')\n" +
                "           when timestampdiff(minute , product.updatedAt, current_timestamp) <60\n" +
                "               then concat(timestampdiff(minute, product.updatedAt, current_timestamp),' 분 전')\n" +
                "           when timestampdiff(hour , product.updatedAt, current_timestamp) <24\n" +
                "               then concat(timestampdiff(hour, product.updatedAt, current_timestamp),' 시간 전')\n" +
                "           else concat(datediff(current_timestamp, product.updatedAt),' 일 전')\n" +
                "           end as 'updatedAt',\n" +
                "    product.title as 'title',\n" +
                "    product.price as 'price'\n" +
                "from product\n" +
                "where product.userId = ? and product.`condition`= 'fin'\n" +
                "order by product.updatedAt desc";
        int getUserProductParams = userId;
        return this.jdbcTemplate.query(getUserProductQuery,
                (rs, rowNum) -> new GetUserProductRes(
                        getProductImg,
                        rs.getBoolean("pay"),
                        rs.getString("updatedAt"),
                        rs.getString("title"),
                        rs.getInt("price")),
                getUserProductParams);
    }
//    // 해당 nickname을 갖는 유저들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) {
//        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
//        String getUsersByNicknameParams = nickname;
//        return this.jdbcTemplate.query(getUsersByNicknameQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userId"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
//    // 해당 userIdx를 갖는 유저조회
//    public GetUserRes getUser(int userId) {
//        String getUserQuery = "select * from User where userId = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
//        int getUserParams = userId;
//        return this.jdbcTemplate.queryForObject(getUserQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userId"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }

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

    // 탈퇴한 유저인지 체크
    public String checkStatus(String id) {
        String checkStatusQuery = "select status from user where id = ?";
        String checkStatusParams = id;
        return this.jdbcTemplate.queryForObject(checkStatusQuery,
                String.class,
                checkStatusParams);  // 쿼리문의 결과(활동중: active, 비활성: inactive)를 문자열로 반환
    }
}
