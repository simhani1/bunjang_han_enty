package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.GetProductIdRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.hibernate.hql.internal.antlr.HqlSqlBaseWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class UserProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


    // 로그인
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        // 존재하지 않는 아이디인지 체크
        if(userDao.checkId(postLoginReq.getId()) == 0){
            throw new BaseException(NO_EXISTED_ID);
        }
        // 탈퇴한 유저인지 체크
        if(userDao.checkStatus(postLoginReq.getId()).equals("inactive")){
            throw new BaseException(NOT_ACTIVE_USER);
        }
        User user = userDao.getPwd(postLoginReq);
        String decryptPwd;
        try {
            decryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPwd()); // 복호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        if (postLoginReq.getPwd().equals(decryptPwd)) { //비말번호가 일치한다면 userId를 가져온다.
            int userId = user.getUserId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // 본인의 판매중 상품 조회
    public List<GetUserProductRes> getUserProductRes_sel(int userId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자가 판매중인 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId, "sel");
            for(GetProductIdRes obj : productsId){
                getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "sel"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 본인의 예약중 상품 조회
    public List<GetUserProductRes> getUserProductRes_res(int userId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자의 예약중인 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId,"res");
            for(GetProductIdRes obj : productsId){
                getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "res"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 본인의 판매완료 상품 조회
    public List<GetUserProductRes> getUserProductRes_fin(int userId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자의 판매완료 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId, "fin");
            for(GetProductIdRes obj : productsId){
                getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "fin"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    // 찜하기
//    public PostHeartRes addHeartList(int userId, int productId) throws BaseException {
//        // 본인의 물건인 경우 찜하기 불가능
//        if(userDao.checkProductOwner(userId, productId)){
//            throw new BaseException();
//        }
//        User user = userDao.getPwd(postLoginReq);
//        String decryptPwd;
//        try {
//            decryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPwd()); // 복호화
//            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//        }
//        if (postLoginReq.getPwd().equals(decryptPwd)) { //비말번호가 일치한다면 userId를 가져온다.
//            int userId = user.getUserId();
//            String jwt = jwtService.createJwt(userId);
//            return new PostLoginRes(userId,jwt);
//        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//    }

    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
    // 해당 아이디 중복성 체크
    public int checkId(String id) throws BaseException {
        try {
            return userDao.checkId(id);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 닉네임 중복성 체크
    public int checkNickname(String nickname) throws BaseException {
        try {
            return userDao.checkNickname(nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 전화번호 중복성 체크
    public int checkPhoneNum(String phoneNum) throws BaseException {
        try {
            return userDao.checkPhoneNum(phoneNum);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
