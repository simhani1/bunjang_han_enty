package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.user.model.PostShopReviewReq;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************

    // 회원 가입
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 닉네임 중복확인
        if (userProvider.checkNickname(postUserReq.getNickname()) == 1) {
            throw new BaseException(EXISTS_NICKNAME);
        }
        // 전화번호 중복확인
        if (userProvider.checkPhoneNum(postUserReq.getPhoneNum()) == 1) {
            throw new BaseException(EXISTS_PHONENUM);
        }
        String encryptPwd;
        if(!postUserReq.getPwd().equals("") || !postUserReq.getPwd().isEmpty()) {
            try {
                encryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPwd()); // 암호화코드
                postUserReq.setPassword(encryptPwd);
            } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
                throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
            }
        }
        try {
            int userId = userDao.createUser(postUserReq);
            String jwt = jwtService.createJwt(userId);
            return new PostUserRes(userId,jwt);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 폰번호 수정
    public void modifyPhoneNum(int userId, String phoneNum) throws BaseException {
        try {
            int result = userDao.modifyPhoneNum(userId, phoneNum); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_INFO);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 성별 수정
    public void modifyGender(int userId, boolean gender) throws BaseException {
        try {
            int result = userDao.modifyGender(userId, gender); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_INFO);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 성별 수정
    public void modifyBirth(int userId, String birth) throws BaseException {
        try {
            int result = userDao.modifyBirth(userId, birth); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_INFO);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 회원탈퇴
    public void withdrawl(int userId, boolean status) throws BaseException {
        try {
            int result = userDao.withdrawl(userId, status); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(WITHDRAWL_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 상점후기 작성
    public void postShopReview(int userId, PostShopReviewReq postShopReviewReq) throws BaseException {
        // 해당 물건이 삭제된 경우
        if(userDao.checkProductIsDeleted(postShopReviewReq.getProductId()))
            throw new BaseException(DELETED_PRODUCT);
        // 해당 물건이 존재하는 물건인지 체크
        if(!userDao.checkProductExist(postShopReviewReq.getProductId()))
            throw new BaseException(INVALID_PRODUCTID);
        // 본인의 상품인지 체크
        if(userDao.checkProductOwner(userId, postShopReviewReq.getProductId()) == 1)
            throw new BaseException(CANNOT_REVIEW_YOUR_PRODUCT);
        // 리뷰는 20자 이상 작성해야함
        if(postShopReviewReq.getReviewContents().length() < 20)
            throw new BaseException(WRONG_REVIEW_LENGTH);
        try {
            int result = userDao.postShopReview(userId, postShopReviewReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0)  // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(FAILED_TO_POST_REVIEW);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}