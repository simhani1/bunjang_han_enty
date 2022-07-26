package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class SearchService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final SearchDao searchDao;
    private final SearchProvider searchProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public SearchService(SearchDao searchDao, SearchProvider searchProvider, JwtService jwtService) {
        this.searchDao = searchDao;
        this.searchProvider = searchProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************

    // 최근 검색어 전체 삭제
    public void removeKeywordsLog(int userId) throws BaseException {
        try {
            int result = searchDao.removeKeywordsLog(userId); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(FAILED_TO_DELETE_KEYWORDS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
//    // 회원 가입
//    public PostSearchRes createUser(PostUserReq postUserReq) throws BaseException {
//        // 아이디 중복확인
//        if (userProvider.checkId(postUserReq.getId()) == 1) {
//            throw new BaseException(EXISTS_ID);
//        }
//        // 닉네임 중복확인
//        if (userProvider.checkNickname(postUserReq.getNickname()) == 1) {
//            throw new BaseException(EXISTS_NICKNAME);
//        }
//        // 전화번호 중복확인
//        if (userProvider.checkPhoneNum(postUserReq.getPhoneNum()) == 1) {
//            throw new BaseException(EXISTS_PHONENUM);
//        }
//        String encryptPwd;
//        try {
//            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
//            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
//            encryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPwd()); // 암호화코드
//            postUserReq.setPassword(encryptPwd);
//        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
//            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
//        }
//        try {
//            int userId = userDao.createUser(postUserReq);
//            String jwt = jwtService.createJwt(userId);
//            return new PostUserRes(userId,jwt);
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 폰번호 수정
//    public void modifyPhoneNum(int userId, String phoneNum) throws BaseException {
//        try {
//            int result = userDao.modifyPhoneNum(userId, phoneNum); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
//            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
//                throw new BaseException(MODIFY_FAIL_INFO);
//            }
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 성별 수정
//    public void modifyGender(int userId, boolean gender) throws BaseException {
//        try {
//            int result = userDao.modifyGender(userId, gender); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
//            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
//                throw new BaseException(MODIFY_FAIL_INFO);
//            }
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//    // 회원탈퇴
//    public void withdrawl(int userId, boolean status) throws BaseException {
//        try {
//            int result = userDao.withdrawl(userId, status); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
//            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
//                throw new BaseException(WITHDRAWL_FAIL);
//            }
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
