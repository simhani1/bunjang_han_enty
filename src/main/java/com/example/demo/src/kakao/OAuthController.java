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

//        System.out.println("access_Token" + access_Token);

        oAuthService.createKakaoUser(access_Token);
    }
}
