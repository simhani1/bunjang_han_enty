package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userId를 반환한다.
    }

//    // 회원정보 변경
//    public int modifyUserName(PatchUserReq patchUserReq) {
//        String modifyUserNameQuery = "update user set nickname = ? where userId = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
//        Object[] modifyUserNameParams = new Object[]{patchUserReq.getNickname(), patchUserReq.getUserId()}; // 주입될 값들(nickname, userIdx) 순
//
//        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
//    }
//
//
//    // 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
//    public User getPwd(PostLoginReq postLoginReq) {
//        String getPwdQuery = "select userId, password,email,nickname from User where email = ?"; // 해당 email을 만족하는 User의 정보들을 조회한다.
//        String getPwdParams = postLoginReq.getEmail(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.
//
//        return this.jdbcTemplate.queryForObject(getPwdQuery,
//                (rs, rowNum) -> new User(
//                        rs.getInt("userId"),
//                        rs.getString("email"),
//                        rs.getString("password"),
//                        rs.getString("nickname")
//                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getPwdParams
//        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }
//
//    // User 테이블에 존재하는 전체 유저들의 정보 조회
//    public List<GetUserRes> getUsers() {
//        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
//        return this.jdbcTemplate.query(getUsersQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userId"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
//    }
//
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
}
