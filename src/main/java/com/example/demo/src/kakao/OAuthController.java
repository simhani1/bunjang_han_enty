package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/users")
public class OAuthController {

    final private OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService){
        this.oAuthService = oAuthService;
    }

    @ResponseBody
    @GetMapping("/kakao")
    public void kakaoCallback(@RequestParam String code) throws BaseException {
        String access_Token = oAuthService.getKakaoAccessToken(code);
        oAuthService.createKakaoUser(access_Token);
    }

    // /kakao/
    // /kakao/login ㅇㅈ
//    고칠건 따로 없고 -> user회원가입으로 넘기고 만들어지는 userId 똑같이 jwt 만들면된다. 이거맞죠
//    랑, accesstoken은 어따 kakao 로그인이 -> 연결하면됨 ㅇㅋ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

//    일단 그럼 url로 달라고 해야죠
//    다른 서버랑 얘기한게 http://njeat.shop/image/newphoto.jpg 이렇게 링크 만들수잇다고
//    image 미리 저 이름의 사진등록할때 링크로 받는거에요
//    image/미리 저장해놓고 서버 폴더
}
