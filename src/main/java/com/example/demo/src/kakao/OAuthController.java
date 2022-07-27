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
    public String kakaoCallback(@RequestParam String code) throws BaseException {
        String access_Token = oAuthService.getKakaoAccessToken(code);
        String email = oAuthService.createKakaoUser(access_Token);
        return "access_Token : " + access_Token + "</br>email : " + email;
    }
//
//    @PostMapping("/kakao")
//    public String createKakaoUser(@)

}
