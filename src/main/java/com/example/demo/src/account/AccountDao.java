package com.example.demo.src.account;

import com.example.demo.src.account.model.GetAccountRes;
import com.example.demo.src.account.model.PostAccountReq;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;

@Repository

public class AccountDao {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    @Transactional
    // 계좌 추가
    public int createAccount(int userId, PostAccountReq postAccountReq) {
        String createAccountQuery = "insert into accountList (userid, name, bankId, accountNum, standard) VALUES (?,?,?,?,?)";
        Object[] createAccountParams = new Object[]{userId, postAccountReq.getName(), postAccountReq.getBankId(), postAccountReq.getAccountNum(), postAccountReq.isStandard()};
        this.jdbcTemplate.update(createAccountQuery, createAccountParams);
        String lastInsertIdQuery = "select count(*) from accountList";
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // accountId 반환
    }

    // 계좌 조회하기
    public List<GetAccountRes> getAllAccount(int userId) {
        String getAllAccountQuery = "select\n" +
                "    accountList.standard as 'standard',\n" +
                "    bank.bankImgUrl as 'bankImgUrl',\n" +
                "    bank.bankName as 'bankName',\n" +
                "    accountList.accountNum as 'accountNum',\n" +
                "    accountList.name as 'name'\n" +
                "from accountList\n" +
                "inner join bank on accountList.bankId = bank.bankId\n" +
                "where accountList.userId = ?";
        int getAllAccountParams = userId;
        return this.jdbcTemplate.query(getAllAccountQuery,
                (rs, rowNum) -> new GetAccountRes(
                        rs.getBoolean("standard"),
                        rs.getString("bankImgUrl"),
                        rs.getString("bankName"),
                        rs.getString("accountNum"),
                        rs.getString("name")),
                getAllAccountParams);
    }
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

//    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
//
//    // 해당 아이디 중복성 체크
//    public int checkId(String id) {
//        String checkIdQuery = "select exists(select id from user where id = ?)";
//        String checkIdParams = id;
//        return this.jdbcTemplate.queryForObject(checkIdQuery,
//                int.class,
//                checkIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
//    }
//
//    // 해당 닉네임 중복성 체크
//    public int checkNickname(String nickname) {
//        String checkNicknameQuery = "select exists(select nickname from user where nickname = ?)";
//        String checkNicknameParams = nickname;
//        return this.jdbcTemplate.queryForObject(checkNicknameQuery,
//                int.class,
//                checkNicknameParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
//    }
//
    // 해당 은행 존재하는지 체크
    public int checkBankId(int bankId) {
        String checkBankIdQuery = "select exists(select bankId from bank where bankId = ?)";
        int checkBankIdParams = bankId;
        return this.jdbcTemplate.queryForObject(checkBankIdQuery,
                int.class,
                checkBankIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 계좌번호 중복성 체크
    public int checkAccountNum(String accountNum) {
        String checkAccountNumQuery = "select exists(select accountNum from accountList where accountNum = ?)";
        String checkAccountNumParams = accountNum;
        return this.jdbcTemplate.queryForObject(checkAccountNumQuery,
                int.class,
                checkAccountNumParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 유저가 등록한 계좌 개수 체크(최대 2개까지)
    public int checkAccountCnt(int userId) {
        String checkAccountCntQuery = "select count(*) from accountList where userId = ?";
        int checkAccountCntParams = userId;
        return this.jdbcTemplate.queryForObject(checkAccountCntQuery,
                int.class,
                checkAccountCntParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 기존 계좌 기본 계좌 해제하기
    public void changeStandard(int userId) {
        String changeStandardQuery = "update accountList set standard = false where userId = ? and standard = true";
        int changeStandardParams = userId;
        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
    }
//
//    // 탈퇴한 유저인지 체크
//    public String checkStatus(String id) {
//        String checkStatusQuery = "select status from user where id = ?";
//        String checkStatusParams = id;
//        return this.jdbcTemplate.queryForObject(checkStatusQuery,
//                String.class,
//                checkStatusParams);  // 쿼리문의 결과(활동중: active, 비활성: inactive)를 문자열로 반환
//    }
}
