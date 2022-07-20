package com.example.demo.src.account;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.User;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AccountProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final AccountDao accountDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public AccountProvider(AccountDao accountDao, JwtService jwtService) {
        this.accountDao = accountDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


//    // 로그인
//    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
//        // 존재하지 않는 아이디인지 체크
//        if(userDao.checkId(postLoginReq.getId()) == 0){
//            throw new BaseException(NO_EXISTED_ID);
//        }
//        // 탈퇴한 유저인지 체크
//        if(userDao.checkStatus(postLoginReq.getId()).equals("inactive")){
//            throw new BaseException(NOT_ACTIVE_USER);
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
//
//    // User들의 정보를 조회
//    public List<GetUserRes> getUsers() throws BaseException {
//        try {
//            List<GetUserRes> getUserRes = userDao.getUsers();
//            return getUserRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 해당 nickname을 갖는 User들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) throws BaseException {
//        try {
//            List<GetUserRes> getUsersRes = userDao.getUsersByNickname(nickname);
//            return getUsersRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    // 해당 userIdx를 갖는 User의 정보 조회
//    public GetUserRes getUser(int userId) throws BaseException {
//        try {
//            GetUserRes getUserRes = userDao.getUser(userId);
//            return getUserRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

//    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
//    // 해당 아이디 중복성 체크
//    public int checkId(String id) throws BaseException {
//        try {
//            return userDao.checkId(id);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
    // 해당 은행이 존재하는지 체크
    public int checkBankId(int bankId) throws BaseException {
        try {
            return accountDao.checkBankId(bankId);  // 존재하면 1을 반환
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 유저가 등록한 계좌 개수 체크(최대 2개까지)
    public int checkAccountCnt(int userId) throws BaseException {
        try {
            return accountDao.checkAccountCnt(userId);  // 계좌의 개수를 반환
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 전화번호 중복성 체크
    public int checkAccountNum(String accountNum) throws BaseException {
        try {
            return accountDao.checkAccountNum(accountNum);  // 이미 있으면 1을 반환
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
