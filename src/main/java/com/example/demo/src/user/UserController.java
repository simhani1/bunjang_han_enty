package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.*;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/app/users")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService;


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    // ******************************************************************************

    // 회원 가입
    @ResponseBody
    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        if(postUserReq.getNickname().equals("")){
            return new BaseResponse<>(EMPTY_NICKNAME);
        }
        if(postUserReq.getLocation().equals("")){
            return new BaseResponse<>(EMPTY_LOCATION);
        }
        if (postUserReq.getPhoneNum().equals("")) {
            return new BaseResponse<>(USERS_EMPTY_PHONENUMBER);
        }
        // 폰번호 자릿수 체크
        if (isRegexTelephoneNum(postUserReq.getPhoneNum())) {
            return new BaseResponse<>(INVALID_PHONENUMBER);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(SIGN_UP_SUCCESS, postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 로그인
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            if(postLoginReq.getId().equals("")) {
                return new BaseResponse<>(EMPTY_ID);
            }
            if(postLoginReq.getPwd().equals("")) {
                return new BaseResponse<>(EMPTY_PWD);
            }
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(LOG_IN_SUCCESS, postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 폰번호 수정
    @PatchMapping("/phoneNum/{userId}")
    public BaseResponse<String> modifyPhoneNum(@PathVariable("userId") int userId, @RequestBody PatchUserPhoneNum patchUserPhoneNum) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            // 폰번호 자릿수 체크
            if (isRegexTelephoneNum(patchUserPhoneNum.getPhoneNum())) {
                return new BaseResponse<>(INVALID_PHONENUMBER);
            }
            if(userProvider.checkPhoneNum(patchUserPhoneNum.getPhoneNum()) == 1){
                return new BaseResponse<>(EXISTS_PHONENUM);
            }
            userService.modifyPhoneNum(userId, patchUserPhoneNum.getPhoneNum());
            String result = "전화번호가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 성별 수정
    @PatchMapping("/gender/{userId}")
    public BaseResponse<String> modifyGender(@PathVariable("userId") int userId, @RequestBody PatchUserReq patchUserReq) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            userService.modifyGender(userId, patchUserReq.isGender());
            String result = "성별이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 생일 수정
    @PatchMapping("/birth/{userId}")
    public BaseResponse<String> modifyBirth(@PathVariable("userId") int userId, @RequestBody PatchUserBirth patchUserBirth) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            // 날짜 포맷 확인
            if(!validationDate(patchUserBirth.getBirth())){
                throw new BaseException(INVALID_DATE);
            }
            userService.modifyBirth(userId, patchUserBirth.getBirth());
            String result = "생일이 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 회원탈퇴
    @PatchMapping("/withdrawl/{userId}")
    public BaseResponse<String> withdrawl(@PathVariable("userId") int userId, @RequestBody PatchWithdrawl patchWithdrawl) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            userService.withdrawl(userId, patchWithdrawl.isStatus());
            String result = "탈퇴처리 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 본인의 판매중 상품 조회
    @ResponseBody
    @GetMapping("/sell/{userId}/{otherId}")
    public BaseResponse<List<GetUserProductRes>> getUserProductRes_sel (@PathVariable int userId, @PathVariable int otherId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            List<GetUserProductRes> getUserProductRes = userProvider.getUserProductRes_sel(userId, otherId);
            if(getUserProductRes.isEmpty())
                return new BaseResponse<>(EMPTY_RESULT, getUserProductRes);
            return new BaseResponse<>(getUserProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 본인의 예약중 상품 조회
    @ResponseBody
    @GetMapping("/reservation/{userId}")
    public BaseResponse<List<GetUserProductRes>> getUserProductRes_res (@PathVariable int userId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            List<GetUserProductRes> getUserProductRes = userProvider.getUserProductRes_res(userId);
            if(getUserProductRes.isEmpty())
                return new BaseResponse<>(EMPTY_RESULT, getUserProductRes);
            return new BaseResponse<>(getUserProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 본인의 판매완료 상품 조회
    @ResponseBody
    @GetMapping("/sold-out/{userId}")
    public BaseResponse<List<GetUserProductRes>> getUserProductRes_fin (@PathVariable int userId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            List<GetUserProductRes> getUserProductRes = userProvider.getUserProductRes_fin(userId);
            if(getUserProductRes.isEmpty())
                return new BaseResponse<>(EMPTY_RESULT, getUserProductRes);
            return new BaseResponse<>(getUserProductRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
    @ResponseBody
    @GetMapping("/my-page/{userId}")
    public BaseResponse<GetMyPageRes> getMyPage (@PathVariable int userId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            GetMyPageRes getMyPageRes = userProvider.getMyPage(userId);
            return new BaseResponse<>(getMyPageRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 상점후기 작성
    @ResponseBody
    @PostMapping("/shop/reviews/{userId}")
    public BaseResponse<String> postShopReview (@PathVariable int userId, @RequestBody PostShopReviewReq postShopReviewReq) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            ////////////////////////////////////  JWT
            userService.postShopReview(userId, postShopReviewReq);
            String result = "리뷰가 작성되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 상점후기 조회
    @ResponseBody
    @GetMapping("/shop/reviews/{userId}")
    public BaseResponse<List<GetShopReviewRes>> getShopReview (@PathVariable int userId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
            //////////////////////////////////////  JWT
            List<GetShopReviewRes> getShopReview = userProvider.getShopReview(userId);
            if(getShopReview.isEmpty())
                return new BaseResponse<>(EMPTY_RESULT);
            return new BaseResponse<>(getShopReview);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 찜하기
    @PostMapping("/heart-list/{userId}/{productId}")
    public BaseResponse<PostHeartRes> addHeartList(@PathVariable int userId, @PathVariable int productId, @RequestBody PostHeartReq postHeartReq) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            PostHeartRes postHeartRes = userProvider.addHeartList(userId, productId, postHeartReq.isStatus());
            return new BaseResponse<>(REQ_HEARTLIST_SUCCESS, postHeartRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 찜목록 조회
    @ResponseBody
    @GetMapping("/heart-list/{userId}")
    public BaseResponse<List<GetHeartProductsRes>> getHeartProducts (@PathVariable int userId) {
        try {
            // 해당 회원이 맞는지 검사
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if (userId != userIdByJwt) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            List<GetHeartProductsRes> getHeartProducts = userProvider.getHeartProducts(userId);
            if(getHeartProducts.isEmpty())
                return new BaseResponse<>(EMPTY_RESULT);
            return new BaseResponse<>(getHeartProducts);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
