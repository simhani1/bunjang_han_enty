package com.example.demo.src.address;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.account.AccountDao;
import com.example.demo.src.account.AccountProvider;
import com.example.demo.src.account.model.DeleteAccountRes;
import com.example.demo.src.account.model.PatchAccountReq;
import com.example.demo.src.account.model.PostAccountReq;
import com.example.demo.src.account.model.PostAccountRes;
import com.example.demo.src.address.model.PatchAddressReq;
import com.example.demo.src.address.model.PostAddressReq;
import com.example.demo.src.address.model.PostAddressRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.src.user.model.PostUserRes;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexTelephoneNum;

@Service
public class AddressService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final AddressDao addressDao;
    private final AddressProvider addressProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public AddressService(AddressDao addressDao, AddressProvider addressProvider, JwtService jwtService) {
        this.addressDao = addressDao;
        this.addressProvider = addressProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************

    // 배송지 추가
    public PostAddressRes createAddress(int userId, PostAddressReq postAddressReq) throws BaseException {
        // 계좌가 3개인 경우 추가 불가능
        if(addressProvider.checkAddressCnt(userId) == 3){
            throw new BaseException(MAX_ADDRESS_CNT);
        }
        // 1개상 존재하는 경우 기본 주소 설정 체크
        else if(addressProvider.checkAddressCnt(userId) >= 1){
            if(postAddressReq.isStandard()){
                addressDao.changeStandard(userId);
            }
        }
        // 처음 추가하는 경우 무조건 기본 주소로 설정
        else if(addressProvider.checkAddressCnt(userId) == 0){
            postAddressReq.setStandard(true);
        }
        // 이미 등록된 배송지인지 검사(입력되는 모든 정보가 동일한 경우)
        if(addressProvider.checkAddressExist(userId, postAddressReq) == 1){
            throw new BaseException(EXISTS_ADDRESS);
        }
        try {
            int addressId = addressDao.createAddress(userId, postAddressReq);
            return new PostAddressRes(userId, addressId);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 배송지 삭제
    public void deleteAddress(int userId, int addressId) throws BaseException {
        // 삭제할 배송지가 없는 경우
        if(addressProvider.checkAddressCnt(userId) == 0){
            throw new BaseException(EMPTY_ADDRESS);
        }
        // 주어진 배송지가 존재하는 배송지인지 체크
        if(addressProvider.checkAddressExist_delete(userId, addressId) == 0){
            throw new BaseException(INVALID_ADDRESSID);
        }
        try {
            int result = addressDao.deleteAddress(userId, addressId); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if(result == 0){
                throw new BaseException(DELETE_FAIL_ADDRESS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }

    }

//     addressDao.changeStandard_modify(userId, addressId, !patchAddressReq.isStandard());
    // 배송지 수정
    public void modifyAddress(int userId, int addressId, PatchAddressReq patchAddressReq) throws BaseException {
        // 배송지가 2개이상 존재하는 경우
        if(addressProvider.checkAddressCnt(userId) >= 2){
            // 수정하는 배송지를 기본배송지로 지정하는 경우, 나머지 두 배송지의 기본 배송지 설정 해제
            if(patchAddressReq.isStandard()){
                addressDao.changeStandard(userId);
            }
        }
        // 수정한 배송지와 동일한 배송지가 이미 존재하는 경우
        if(addressProvider.checkAddressExist_modify(userId, patchAddressReq) == 1){
            throw new BaseException(EXISTS_ADDRESS);
        }
        try {
            int result = addressDao.modifyAddress(userId, addressId, patchAddressReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_ADDRESS);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
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
