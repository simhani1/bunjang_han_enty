package com.example.demo.src.address;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.account.AccountDao;
import com.example.demo.src.account.model.GetAccountRes;
import com.example.demo.src.account.model.PostAccountReq;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.GetUserRes;
import com.example.demo.src.user.model.PostLoginReq;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.src.user.model.User;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class AddressProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final AddressDao addressDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public AddressProvider(AddressDao addressDao, JwtService jwtService) {
        this.addressDao = addressDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************

//    // 계좌 조회하기
//    public List<GetAccountRes> getAllAccount(int userId) throws BaseException {
//        try {
//            List<GetAccountRes> getAllAccountRes = accountDao.getAllAccount(userId);
//            return getAllAccountRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
////
////    // 해당 nickname을 갖는 User들의 정보 조회
////    public List<GetUserRes> getUsersByNickname(String nickname) throws BaseException {
////        try {
////            List<GetUserRes> getUsersRes = userDao.getUsersByNickname(nickname);
////            return getUsersRes;
////        } catch (Exception exception) {
////            throw new BaseException(DATABASE_ERROR);
////        }
////    }
////
////
////    // 해당 userIdx를 갖는 User의 정보 조회
////    public GetUserRes getUser(int userId) throws BaseException {
////        try {
////            GetUserRes getUserRes = userDao.getUser(userId);
////            return getUserRes;
////        } catch (Exception exception) {
////            throw new BaseException(DATABASE_ERROR);
////        }
////    }
//
//    //    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////

    // 해당 유저가 등록한 주소 개수 계산
    public int checkAddressCnt(int userId) throws BaseException {
        try {
            return addressDao.checkAddressCnt(userId);  // 계좌의 개수를 반환
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 배송지 중복성 체크
    public int checkAddressExist(int userId, PostAddressReq postAddressReq) throws BaseException {
        try {
            return addressDao.checkAddressExist(userId, postAddressReq);  // 이미 등록된 배송지일 경우 1을 반환
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
////    // 해당 아이디 중복성 체크
////    public int checkId(String id) throws BaseException {
////        try {
////            return userDao.checkId(id);
////        } catch (Exception exception) {
////            throw new BaseException(DATABASE_ERROR);
////        }
////    }
////
//    // 해당 은행이 존재하는지 체크
//    public int checkBankId(int bankId) throws BaseException {
//        try {
//            return accountDao.checkBankId(bankId);  // 존재하면 1을 반환
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//
//
//    // 해당 계좌가 존재하는지 & 본인의 계좌가 맞는지 체크
//    public int checkAccountExist(int userId, int accountId) throws BaseException {
//        try {
//            return accountDao.checkAccountExist(userId, accountId);  // 본인의 계좌가 맞고 존재한다면 1반환
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
}
