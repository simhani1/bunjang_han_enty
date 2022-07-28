package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.kakao.model.PostAutoLoginReq;
import com.example.demo.src.kakao.model.PostAutoLoginRes;
import com.example.demo.src.kakao.model.PostCheckPhoneReq;
import com.example.demo.src.kakao.model.PostCheckPhoneRes;
import com.example.demo.utils.JwtService;
import org.springframework.web.bind.annotation.*;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexTelephoneNum;

@RestController
@RequestMapping("/app/users")
public class OAuthController {

    private final OAuthService oAuthService;
    private final OAuthProvider oAuthProvider;
    private final JwtService jwtService;

    public OAuthController(OAuthService oAuthService, OAuthProvider oAuthProvider, JwtService jwtService){
        this.oAuthService = oAuthService;
        this.oAuthProvider = oAuthProvider;
        this.jwtService = jwtService;
    }


    @ResponseBody
    @GetMapping("/kakao")
    public String kakaoCallback(@RequestParam String code) throws BaseException {
        String access_Token = oAuthService.getKakaoAccessToken(code);
        return "ACCESS_TOKEN : " + access_Token;
    }

    // 카카오로 본인인증
    @PostMapping("/kakao/certification")
    public BaseResponse<String> createKakaoUser(@RequestParam String access_Token){
        try{
            // access_token이 빈칸일때
            if(access_Token.equals("")){
                return new BaseResponse<>(EMPTY_ACCESS_TOKEN);
            }
            // 받아온 값이 없을 때
            if(oAuthService.createKakaoUser(access_Token).equals("")){
                return new BaseResponse<>(FAILED_KAKAO_CERTIFICATION);
            }
            return new BaseResponse<>(KAKAO_CERTIFICATION_SUCCESS);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 카카오인증 후 폰번호로 회원가입/로그인
    @PostMapping("/kakao/check-phone")
    public BaseResponse<PostCheckPhoneRes> comparePhoneNum(@RequestBody PostCheckPhoneReq postCheckPhoneReq){
        try{
            // 폰번호 자릿수 체크
            if (isRegexTelephoneNum(postCheckPhoneReq.getPhoneNum())) {
                return new BaseResponse<>(INVALID_PHONENUMBER);
            }
            // 폰번호 입력했는지 체크
            if (postCheckPhoneReq.getPhoneNum().equals("")) {
                return new BaseResponse<>(USERS_EMPTY_PHONENUMBER);
            }
            String phoneNum = postCheckPhoneReq.getPhoneNum();
            // db에 입력한 폰번호가 있는지 확인
            PostCheckPhoneRes postCheckPhoneRes = oAuthService.comparePhoneNum(phoneNum);
            // 이미 회원이라면 로그인성공했다는 메세지와 함께 true객체 return
            if(postCheckPhoneRes.getIsMember() == true){
                return new BaseResponse<>(KAKAO_LOGIN_SUCCESS, postCheckPhoneRes);
            }
            // 회원이 아니라면 회원가입을 하라는 메세지와 함께 false객체 return
            else{
                return new BaseResponse<>(NEED_TO_SING_UP, postCheckPhoneRes);
            }
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 자동로그인
//    @PostMapping("/kakao/auto/log-in")
//    public BaseResponse<PostAutoLoginRes> kakaoAutoLogin(@RequestBody PostAutoLoginReq postAutoLoginReq){
//        try{
//            PostAutoLoginRes postAutoLoginRes = oAuthProvider.kakaoAutoLogin(postAutoLoginReq);
//            if(postAutoLoginRes.getIsLoggedIn() == true)
//                return new BaseResponse<>(KAKAO_AUTO_LOGIN_SUCCESS, postAutoLoginRes);
//            else
//                return new BaseResponse<>(KAKAO_AUTO_LOGIN_FAILED, postAutoLoginRes);
//        } catch (BaseException exception){
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }

    // 자동로그인
    @PostMapping("/kakao/auto/log-in/{userId}")
    public BaseResponse<PostAutoLoginRes> kakaoAutoLogin(@PathVariable int userId){
        try{
            // 넘어온 jwt값의 userId와 기존의 userId가 같은지 확인
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(KAKAO_AUTO_LOGIN_FAILED);
            }
            PostAutoLoginRes postAutoLoginRes = new PostAutoLoginRes(true, userId, jwtService.getJwt());
            return new BaseResponse<>(KAKAO_AUTO_LOGIN_SUCCESS, postAutoLoginRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


}
