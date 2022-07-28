package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.src.kakao.model.PostAutoLoginReq;
import com.example.demo.src.kakao.model.PostAutoLoginRes;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OAuthProvider {
    private final OAuthDao oAuthDao;

    public OAuthProvider(OAuthDao oAuthDao){
        this.oAuthDao = oAuthDao;
    }

    // 폰번호가 이미있는지 체크
    public int checkExistsPhoneNum(String phoneNum){
        return (oAuthDao.checkExistsPhoneNum(phoneNum));
    }
}
