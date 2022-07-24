package com.example.demo.src.address;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.account.AccountProvider;
import com.example.demo.src.account.AccountService;
import com.example.demo.src.account.model.*;
import com.example.demo.src.address.model.*;
import com.example.demo.src.user.UserProvider;
import com.example.demo.src.user.UserService;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.config.BaseResponseStatus.INVALID_USER_JWT;
import static com.example.demo.utils.ValidationRegex.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
// @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
//  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
//  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
// @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/app/address")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용

public class AddressController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final AddressProvider addressProvider;
    @Autowired
    private final AddressService addressService;
    @Autowired
    private final JwtService jwtService;


    public AddressController(AddressProvider addressProvider, AddressService addressService, JwtService jwtService) {
        this.addressProvider = addressProvider;
        this.addressService = addressService;
        this.jwtService = jwtService;
    }

    // ******************************************************************************

    // 배송지 추가
    @ResponseBody
    @PostMapping("/{userId}")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostAddressRes> createAddress(@PathVariable("userId") int userId, @RequestBody Address address) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            // 이름 체크
            if(address.getName().equals("")) {
                return new BaseResponse<>(INVALID_NAME);
            }
            // 전화번호 자릿수 체크
            if(isRegexTelephoneNum(address.getPhoneNum())){
                return new BaseResponse<>(INVALID_PHONENUMBER);
            }
            // 주소 체크
            if(address.getAddress().equals(("")) || address.getDetailAddress().equals("")){
                return new BaseResponse<>(EMPTY_LOCATION);
            }
            PostAddressReq postAddressReq = new PostAddressReq(address.getName(), address.getPhoneNum(), address.getAddress(), address.getDetailAddress(), address.isStandard());
            PostAddressRes postAddressRes = addressService.createAddress(userId, postAddressReq);
            return new BaseResponse<>(ADD_ADDRESS_SUCCESS, postAddressRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 배송지 조회하기
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetAddressRes>> getAllAddress(@PathVariable("userId") int userId) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            List<GetAddressRes> getAllAddressRes = addressProvider.getAllAddress(userId);
            return new BaseResponse<>(getAllAddressRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    // 배송지 삭제하기
    @DeleteMapping("/{userId}/{addressId}")
    public BaseResponse<String> deleteAddress (@PathVariable("userId") int userId, @PathVariable("addressId") int addressId) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            addressService.deleteAddress(userId, addressId);
            String result = "배송지 정보가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 배송지 수정
    @PatchMapping("/{userId}/{addressId}")
    public BaseResponse<String> modifyAddress(@PathVariable("userId") int userId, @PathVariable("addressId") int addressId, @RequestBody Address address) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            // 이름 체크
            if(address.getName().equals("")) {
                return new BaseResponse<>(INVALID_NAME);
            }
            // 전화번호 자릿수 체크
            if(isRegexTelephoneNum(address.getPhoneNum())){
                return new BaseResponse<>(INVALID_PHONENUMBER);
            }
            // 주소 체크
            if(address.getAddress().equals(("")) || address.getDetailAddress().equals("")){
                return new BaseResponse<>(EMPTY_LOCATION);
            }
            PatchAddressReq patchAddressReq = new PatchAddressReq(address.getName(), address.getPhoneNum(), address.getAddress(), address.getDetailAddress(), address.isStandard());
            addressService.modifyAddress(userId, addressId, patchAddressReq);
            String result = "배송지 정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
//
//    // 로그인
//    @ResponseBody
//    @PostMapping("/log-in")
//    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
//        try {
//            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
//            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
//            if(postLoginReq.getId().equals("")) {
//                return new BaseResponse<>(EMPTY_ID);
//            }
//            if(postLoginReq.getPwd().equals("")) {
//                return new BaseResponse<>(EMPTY_PWD);
//            }
//            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
//            return new BaseResponse<>(LOG_IN_SUCCESS, postLoginRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
//
//    // 폰번호 수정
//    @PatchMapping("/phoneNum/{userId}")
//    public BaseResponse<String> modifyPhoneNum(@PathVariable("userId") int userId, @RequestBody PatchUserPhoneNum patchUserPhoneNum) {
//        try {
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if(userId != userIdByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            // 폰번호 자릿수 체크
//            if (isRegexTelephoneNum(patchUserPhoneNum.getPhoneNum())) {
//                return new BaseResponse<>(INVALID_PHONENUMBER);
//            }
//            if(userProvider.checkPhoneNum(patchUserPhoneNum.getPhoneNum()) == 1){
//                return new BaseResponse<>(EXISTS_PHONENUM);
//            }
//            userService.modifyPhoneNum(userId, patchUserPhoneNum.getPhoneNum());
//            String result = "전화번호가 수정되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//
//    // 생일 수정
//    @PatchMapping("/birth/{userId}")
//    public BaseResponse<String> modifyBirth(@PathVariable("userId") int userId, @RequestBody PatchUserBirth patchUserBirth) {
//        try {
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if(userId != userIdByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            // 날짜 포맷 확인
//            if(!validationDate(patchUserBirth.getBirth())){
//                throw new BaseException(INVALID_DATE);
//            }
//            userService.modifyBirth(userId, patchUserBirth.getBirth());
//            String result = "생일이 수정되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    // 회원탈퇴
//    @PatchMapping("/withdrawl/{userId}")
//    public BaseResponse<String> withdrawl(@PathVariable("userId") int userId, @RequestBody PatchWithdrawl patchWithdrawl) {
//        try {
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if(userId != userIdByJwt){
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            userService.withdrawl(userId, patchWithdrawl.isStatus());
//            String result = "탈퇴처리 되었습니다.";
//            return new BaseResponse<>(result);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

//    /**
//     * 모든 회원들의  조회 API
//     * [GET] /users
//     *
//     * 또는
//     *
//     * 해당 닉네임을 같는 유저들의 정보 조회 API
//     * [GET] /users? NickName=
//     */
//    //Query String
//    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
//    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
//    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
//    // GET 방식의 요청을 매핑하기 위한 어노테이션
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickname) {
//        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
//        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
//        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
//        try {
//            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
//            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
//            return new BaseResponse<>(getUsersRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
}
