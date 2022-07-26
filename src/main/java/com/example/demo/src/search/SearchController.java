package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.search.model.GetKeywordsLogRes;
import com.example.demo.src.search.model.GetProductByKeywordRes;
import com.example.demo.src.user.model.PatchUserReq;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/search")
@Service
public class SearchController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final SearchProvider searchProvider;
    @Autowired
    private final SearchService searchService;
    @Autowired
    private final JwtService jwtService;


    public SearchController(SearchProvider searchProvider, SearchService searchService, JwtService jwtService) {
        this.searchProvider = searchProvider;
        this.searchService = searchService;
        this.jwtService = jwtService;
    }

    // ******************************************************************************

    // 검색어로 판매글 검색
    @ResponseBody
    @GetMapping("/{userId}")
    public BaseResponse<List<GetProductByKeywordRes>> GetProductByKeyword (@PathVariable int userId, @RequestParam int page, @RequestParam String keyword, @RequestParam String type) {
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
            List<GetProductByKeywordRes> getProductByKeyword = searchProvider.getProductByKeyword(userId, page, keyword, type);
            if(getProductByKeyword.size() == 0)
                return new BaseResponse<>(EMPTY_RESULT);
            return new BaseResponse<>(getProductByKeyword);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 본인의 검색 내역 조회
    @ResponseBody
    @GetMapping("/log/{userId}")
    public BaseResponse<List<GetKeywordsLogRes>> getKeywordsLog (@PathVariable int userId) {
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
            List<GetKeywordsLogRes> getKeywordsLogList = searchProvider.getKeywordsLog(userId);
            if(getKeywordsLogList.size() == 0)
                return new BaseResponse<>(EMPTY_RESULT);
            return new BaseResponse<>(getKeywordsLogList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 최근 검색어 전체 삭제
    @PatchMapping("/log/{userId}")
    public BaseResponse<String> modifyGender(@PathVariable("userId") int userId) {
        try {
            //////////////////////////////////////  JWT
            //jwt에서 idx 추출
            int userIdByJwt = jwtService.getUserId();
            //userId와 접근한 유저가 같은지 확인
            if(userId != userIdByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //////////////////////////////////////  JWT
            searchService.removeKeywordsLog(userId);
            String result = "최근 검색어가 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 본인의 검색 내역 조회
    @ResponseBody
    @GetMapping("/hot-keywords")
    public BaseResponse<List<GetKeywordsLogRes>> getHotKeywordsLog () {
        try {
            List<GetKeywordsLogRes> getHotKeywordsLogList = searchProvider.getHotKeywordsLog();
            if(getHotKeywordsLogList.size() == 0)
                return new BaseResponse<>(EMPTY_RESULT);
            return new BaseResponse<>(getHotKeywordsLogList);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
//    // 회원 가입
//    @ResponseBody
//    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
//    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
//        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
//        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
//        if(postUserReq.getId().equals("")) {
//            return new BaseResponse<>(EMPTY_ID);
//        }
//        if(postUserReq.getPwd().equals((""))){
//            return new BaseResponse<>(EMPTY_PWD);
//        }
//        if(postUserReq.getNickname().equals("")){
//            return new BaseResponse<>(EMPTY_NICKNAME);
//        }
//        if(postUserReq.getLocation().equals("")){
//            return new BaseResponse<>(EMPTY_LOCATION);
//        }
//        if (postUserReq.getPhoneNum().equals("")) {
//            return new BaseResponse<>(USERS_EMPTY_PHONENUMBER);
//        }
//        // 폰번호 자릿수 체크
//        if (isRegexTelephoneNum(postUserReq.getPhoneNum())) {
//            return new BaseResponse<>(INVALID_PHONENUMBER);
//        }
//        try {
//            PostUserRes postUserRes = userService.createUser(postUserReq);
//            return new BaseResponse<>(SIGN_UP_SUCCESS, postUserRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
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
//
//    // 본인의 예약중 상품 조회
//    @ResponseBody
//    @GetMapping("/reservation/{userId}")
//    public BaseResponse<List<GetUserProductRes>> getUserProductRes_res (@PathVariable int userId) {
//        try {
//            // 해당 회원이 맞는지 검사
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            List<GetUserProductRes> getUserProductRes = userProvider.getUserProductRes_res(userId);
//            return new BaseResponse<>(getUserProductRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    // 본인의 판매완료 상품 조회
//    @ResponseBody
//    @GetMapping("/sold-out/{userId}")
//    public BaseResponse<List<GetUserProductRes>> getUserProductRes_fin (@PathVariable int userId) {
//        try {
//            // 해당 회원이 맞는지 검사
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            List<GetUserProductRes> getUserProductRes = userProvider.getUserProductRes_fin(userId);
//            return new BaseResponse<>(getUserProductRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
//    @ResponseBody
//    @GetMapping("/my-page/{userId}")
//    public BaseResponse<GetMyPageRes> getMyPage (@PathVariable int userId) {
//        try {
//            // 해당 회원이 맞는지 검사
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            GetMyPageRes getMyPageRes = userProvider.getMyPage(userId);
//            return new BaseResponse<>(getMyPageRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    // 상점후기 조회
//    @ResponseBody
//    @GetMapping("/shop/review/{userId}")
//    public BaseResponse<List<GetShopReviewRes>> getShopReview (@PathVariable int userId) {
//        try {
//            // 해당 회원이 맞는지 검사
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            List<GetShopReviewRes> getShopReview = userProvider.getShopReview(userId);
//            return new BaseResponse<>(getShopReview);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }
//
//    // 찜하기
//    @PostMapping("/heart-list/{userId}/{productId}")
//    public BaseResponse<PostHeartRes> addHeartList(@PathVariable int userId, @PathVariable int productId, @RequestBody PostHeartReq postHeartReq) {
//        try {
//            // 해당 회원이 맞는지 검사
//            //////////////////////////////////////  JWT
//            //jwt에서 idx 추출
//            int userIdByJwt = jwtService.getUserId();
//            //userId와 접근한 유저가 같은지 확인
//            if (userId != userIdByJwt) {
//                return new BaseResponse<>(INVALID_USER_JWT);
//            }
//            //////////////////////////////////////  JWT
//            PostHeartRes postHeartRes = userProvider.addHeartList(userId, productId, postHeartReq.isStatus());
//            return new BaseResponse<>(REQ_HEARTLIST_SUCCESS, postHeartRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>(exception.getStatus());
//        }
//    }
}
