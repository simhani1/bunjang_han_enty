package com.example.demo.src.account;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.account.model.DeleteAccountRes;
import com.example.demo.src.account.model.PostAccountReq;
import com.example.demo.src.account.model.PostAccountRes;
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
public class AccountService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final AccountDao accountDao;
    private final AccountProvider accountProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public AccountService(AccountDao accountDao, AccountProvider accountProvider, JwtService jwtService) {
        this.accountDao = accountDao;
        this.accountProvider = accountProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************

    // 계좌 추가
    public PostAccountRes createAccount(int userId, PostAccountReq postAccountReq) throws BaseException {
        // 계좌가 2개인 경우
        if(accountProvider.checkAccountCnt(userId) == 2){
            throw new BaseException(MAX_ACCOUNT_CNT);
        }
        else if(accountProvider.checkAccountCnt(userId) == 1){
            // 기본계좌로 설정 요청이 들어온 경우
            if(postAccountReq.isStandard()){
                accountDao.changeStandard(userId);
            }
        }
        else if(accountProvider.checkAccountCnt(userId) == 0){
            postAccountReq.setStandard(true);
        }
        // 계좌번호 중복확인
        if (accountProvider.checkAccountNum(postAccountReq.getAccountNum()) == 1) {
            throw new BaseException(EXISTS_ACCOUNT);
        }
        // 은행 목록에 있는지 체크
        if(accountProvider.checkBankId(postAccountReq.getBankId()) == 0) {
            throw new BaseException(INVALID_BANKID);
        }
        try {
            int accountId = accountDao.createAccount(userId, postAccountReq);
            return new PostAccountRes(userId, accountId);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 계좌 삭제
    public DeleteAccountRes deleteAccount(int userId, int accountId) throws BaseException {
        // 계좌가 2개인 경우
        if(accountProvider.checkAccountCnt(userId) == 2){
            accountDao.changeStandard(userId, accountId);
        }
        else if(accountProvider.checkAccountCnt(userId) == 0){
            throw new BaseException(EMPTY_ACCOUNT);
        }
        // 주어진 계좌가 등록된 계좌인지 체크
        if(accountProvider.checkAccountExist(userId, accountId) == 0){
            throw new BaseException(INVALID_ACCOUNTID);
        }
        try {
            int result = accountDao.deleteAccount(userId, accountId); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if(result == 1)
                return new DeleteAccountRes("계좌가 삭제되었습니다.");
            else
                return new DeleteAccountRes("계좌삭제에 실패하였습니다.");
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }
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
//    // 성별 수정
//    public void modifyBirth(int userId, String birth) throws BaseException {
//        try {
//            int result = userDao.modifyBirth(userId, birth); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
//            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
//                throw new BaseException(MODIFY_FAIL_INFO);
//            }
//        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
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
