package com.example.demo.src.address;

import com.example.demo.src.account.model.GetAccountRes;
import com.example.demo.src.account.model.PatchAccountReq;
import com.example.demo.src.account.model.PostAccountReq;
import com.example.demo.src.address.model.GetAddressRes;
import com.example.demo.src.address.model.PatchAddressReq;
import com.example.demo.src.address.model.PostAddressReq;
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
public class AddressDao {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    @Transactional
    // 배송지 추가
    public int createAddress(int userId, PostAddressReq postAddressReq) {
        String lastInsertIdQuery = "select count(*) from address";
        int lastInsertId = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class) + 1;
        String createAddressQuery = "insert into address (addressId, userId, name, phoneNum, address, detailAddress, standard) VALUES (?,?,?,?,?,?,?)";
        Object[] createAddressParams = new Object[]{lastInsertId, userId, postAddressReq.getName(), postAddressReq.getPhoneNum(), postAddressReq.getAddress(), postAddressReq.getDetailAddress(), postAddressReq.isStandard()};
        this.jdbcTemplate.update(createAddressQuery, createAddressParams);
        return lastInsertId; // addressId 반환
    }

    // 배송지 조회
    public List<GetAddressRes> getAllAddress(int userId) {
        String getAllAddressQuery = "select\n" +
                "    standard as 'standard',\n" +
                "    name as 'name',\n" +
                "    address as 'address',\n" +
                "    detailAddress as 'detailAddress',\n" +
                "    phoneNum as 'phoneNum'\n" +
                "from address\n" +
                "where userId = ?\n" +
                "order by standard DESC";
        int getAllAddressParams = userId;
        return this.jdbcTemplate.query(getAllAddressQuery,
                (rs, rowNum) -> new GetAddressRes(
                        rs.getBoolean("standard"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("detailAddress"),
                        rs.getString("phoneNum")),
                getAllAddressParams);
    }

    // 배송지 삭제
    public int deleteAddress(int userId, int addressId) {
        String deleteAddressQuery = "delete from address where addressId = ? and userId = ?";
        Object [] deleteAddressParams = new Object[]{addressId, userId};
        return this.jdbcTemplate.update(deleteAddressQuery, deleteAddressParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 배송지 정보 수정
    public int modifyAddress(int userId, int addressId, PatchAddressReq patchAddressReq) {
        String modifyAddressQuery = "update address set name = ?, phoneNum = ?, address = ?, detailAddress = ?, standard = ? where addressId = ? and userId = ? ";
        Object [] modifyAddressParams = new Object[]{patchAddressReq.getName(), patchAddressReq.getPhoneNum(), patchAddressReq.getAddress(), patchAddressReq.getDetailAddress(), patchAddressReq.isStandard(), addressId, userId};
        return this.jdbcTemplate.update(modifyAddressQuery, modifyAddressParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
////    // 로그인 - 비밀번호 체크
////    public User getPwd(PostLoginReq postLoginReq) {
////        String getPwdQuery = "select userId, pwd from user where id = ?";
////        String getPwdParams = postLoginReq.getId(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.
////
////        return this.jdbcTemplate.queryForObject(getPwdQuery,
////                (rs, rowNum) -> new User(
////                        rs.getInt("userId"),
////                        rs.getString("pwd")
////                ),
////                getPwdParams
////        );
////    }
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
////    // 폰번호 수정
////    public int modifyPhoneNum(int userId, String phoneNum) {
////        String modifyPhoneNumQuery = "update user set phoneNum = ? where userId = ? ";
////        Object [] modifyPhoneNumParams = new Object[]{phoneNum, userId};
////        return this.jdbcTemplate.update(modifyPhoneNumQuery, modifyPhoneNumParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
////    }
////
////
////
////    // 회원탈
////    public int withdrawl(int userId, boolean status) {
////        String withdrawlQuery = "update user set status = ? where userId = ? ";
////        Object [] withdrawlParams = new Object[]{status, userId};
////        return this.jdbcTemplate.update(withdrawlQuery, withdrawlParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
////    }
//
////    // User 테이블에 존재하는 전체 유저들의 정보 조회
////    public List<GetUserRes> getUsers() {
////        String getUsersQuery = "select * from User"; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
////        return this.jdbcTemplate.query(getUsersQuery,
////                (rs, rowNum) -> new GetUserRes(
////                        rs.getInt("userId"),
////                        rs.getString("nickname"),
////                        rs.getString("Email"),
////                        rs.getString("password")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
////        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
////    }
////
////    // 해당 nickname을 갖는 유저들의 정보 조회
////    public List<GetUserRes> getUsersByNickname(String nickname) {
////        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
////        String getUsersByNicknameParams = nickname;
////        return this.jdbcTemplate.query(getUsersByNicknameQuery,
////                (rs, rowNum) -> new GetUserRes(
////                        rs.getInt("userId"),
////                        rs.getString("nickname"),
////                        rs.getString("Email"),
////                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
////                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
////    }
////
////    // 해당 userIdx를 갖는 유저조회
////    public GetUserRes getUser(int userId) {
////        String getUserQuery = "select * from User where userId = ?"; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
////        int getUserParams = userId;
////        return this.jdbcTemplate.queryForObject(getUserQuery,
////                (rs, rowNum) -> new GetUserRes(
////                        rs.getInt("userId"),
////                        rs.getString("nickname"),
////                        rs.getString("Email"),
////                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
////                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
////    }
//
//    //    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
////
////    // 해당 아이디 중복성 체크
////    public int checkId(String id) {
////        String checkIdQuery = "select exists(select id from user where id = ?)";
////        String checkIdParams = id;
////        return this.jdbcTemplate.queryForObject(checkIdQuery,
////                int.class,
////                checkIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
////    }
////
////    // 해당 닉네임 중복성 체크
////    public int checkNickname(String nickname) {
////        String checkNicknameQuery = "select exists(select nickname from user where nickname = ?)";
////        String checkNicknameParams = nickname;
////        return this.jdbcTemplate.queryForObject(checkNicknameQuery,
////                int.class,
////                checkNicknameParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
////    }
////
//    // 해당 은행 존재하는지 체크
//    public int checkBankId(int bankId) {
//        String checkBankIdQuery = "select exists(select bankId from bank where bankId = ?)";
//        int checkBankIdParams = bankId;
//        return this.jdbcTemplate.queryForObject(checkBankIdQuery,
//                int.class,
//                checkBankIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
//    }
//
    // 해당 배송지가 이미 존재하는지 검사(배송지 추가)
    public int checkAddressExist(int userId, PostAddressReq postAddressReq) {
        String checkAddressExistQuery = "select exists(\n" +
                "    select addressId\n" +
                "    from address\n" +
                "    where userId = ?\n" +
                "      and name = ?\n" +
                "      and phoneNum = ?\n" +
                "      and address = ?\n" +
                "      and detailAddress = ?)";
        Object[] checkAddressExistParams = new Object[]{userId, postAddressReq.getName(), postAddressReq.getPhoneNum(), postAddressReq.getAddress(), postAddressReq.getDetailAddress()};
        return this.jdbcTemplate.queryForObject(checkAddressExistQuery,
                int.class,
                checkAddressExistParams);  // 이미 존재하는 주소라면 1을 반환
    }

    // 해당 배송지가 이미 존재하는지 검사(배송지 삭제)
    public int checkAddressExist_delete(int userId, int addressId) {
        String checkAddressExistQuery = "select exists(select addressId from address where userId = ? and addressId = ?)";
        Object[] checkAddressExistParams = new Object[]{userId, addressId};
        return this.jdbcTemplate.queryForObject(checkAddressExistQuery,
                int.class,
                checkAddressExistParams);  // 이미 존재하는 주소라면 1을 반환
    }

    // 해당 배송지가 이미 존재하는지 검사(배송지 수정)
    public int checkAddressExist_modify(int userId, PatchAddressReq patchAddressReq) {
        String checkAddressExistQuery = "select exists(\n" +
                "    select addressId\n" +
                "    from address\n" +
                "    where userId = ?\n" +
                "      and name = ?\n" +
                "      and phoneNum = ?\n" +
                "      and address = ?\n" +
                "      and detailAddress = ?)";
        Object[] checkAddressExistParams = new Object[]{userId, patchAddressReq.getName(), patchAddressReq.getPhoneNum(), patchAddressReq.getAddress(), patchAddressReq.getDetailAddress()};
        return this.jdbcTemplate.queryForObject(checkAddressExistQuery,
                int.class,
                checkAddressExistParams);  // 이미 존재하는 주소라면 1을 반환
    }

    // 해당 유저가 등록한 주소 개수 체크(최대 3개까지)
    public int checkAddressCnt(int userId) {
        String checkAddressCntQuery = "select count(*) from address where userId = ?";
        int checkAddressCntParams = userId;
        return this.jdbcTemplate.queryForObject(checkAddressCntQuery,
                int.class,
                checkAddressCntParams);
    }
//
//    // 기존 계좌 기본 계좌 해제하기
//    public void changeStandard(int userId) {
//        String changeStandardQuery = "update accountList set standard = false where userId = ? and standard = true";
//        int changeStandardParams = userId;
//        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
//    }
//
    // 나머지 주소 기본배송지 설정 해제
    public void changeStandard(int userId) {
        String changeStandardQuery = "update address set standard = false where userId = ?";
        Object[] changeStandardParams = new Object[]{userId};
        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
    }
//
//    // 수정하는 계좌의 기본계좌 설정과 반대로
//    public void changeStandard_modify(int userId, int accountId, boolean standard) {
//        String changeStandardQuery = "update accountList set standard = ? where userId = ? and accountId != ?";
//        Object[] changeStandardParams = new Object[]{standard, userId, accountId};
//        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
//    }
//
//    // 해당 계좌가 존재하는지 & 본인의 계좌가 맞는지 체크
//    public int checkAccountExist(int userId, int accountId) {
//        String changeStandardQuery = "select exists(select accountId from accountList where userId = ? and accountId = ?)";
//        Object[] changeStandardParams = new Object[]{userId, accountId};
//        return this.jdbcTemplate.queryForObject(changeStandardQuery, int.class, changeStandardParams);
//    }
////
////    // 탈퇴한 유저인지 체크
////    public String checkStatus(String id) {
////        String checkStatusQuery = "select status from user where id = ?";
////        String checkStatusParams = id;
////        return this.jdbcTemplate.queryForObject(checkStatusQuery,
////                String.class,
////                checkStatusParams);  // 쿼리문의 결과(활동중: active, 비활성: inactive)를 문자열로 반환
////    }
}
