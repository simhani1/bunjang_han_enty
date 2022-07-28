package com.example.demo.src.messageCertification;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.messageCertification.model.GetCertCodeRes;
import com.example.demo.src.messageCertification.model.GetCertRes;
import com.example.demo.src.user.UserProvider;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Random;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexCertCode;
import static com.example.demo.utils.ValidationRegex.isRegexTelephoneNum;

@RestController
@RequestMapping("/app/messages")
public class MessageController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final MessageProvider messageProvider;
    @Autowired
    private final MessageService messageService;
    @Autowired
    private final JwtService jwtService;

    @Autowired
    private final UserProvider userProvider;


    public MessageController(MessageProvider messageProvider, MessageService messageService, JwtService jwtService, UserProvider userProvider) {
        this.messageProvider = messageProvider;
        this.messageService = messageService;
        this.jwtService = jwtService;
        this.userProvider = userProvider;
    }

    // ******************************************************************************

    // 문자 전송하기
    @ResponseBody
    @GetMapping("/code")
    public BaseResponse<GetCertCodeRes> sendSms(@RequestParam String phoneNum) throws BaseException {
        try {
            Random rand  = new Random();
            String code = "";
            for(int i=0; i<6; i++) {
                String ran = Integer.toString(rand.nextInt(10));
                code += ran;
            }
            GetCertCodeRes getCertNumRes = new GetCertCodeRes(false);
            // 전화번호 형식 체크
            if(isRegexTelephoneNum(phoneNum)){
                return new BaseResponse<>(INVALID_PHONENUMBER, getCertNumRes);
            }
            // 이미 가입한 정보에 존재하는 경우
            if(userProvider.checkPhoneNum(phoneNum) == 1) {
                return new BaseResponse<>(EXISTS_PHONENUM, getCertNumRes);
            }
            messageService.certifiedPhoneNumber(phoneNum,code);  // 핸드폰으로 numstr을 문자로 전송한다
            getCertNumRes.setPhoneNum(phoneNum);
            getCertNumRes.setCertNum(code);
            getCertNumRes.setCertificated(true);
            messageService.saveCode(phoneNum, code);
            return new BaseResponse<>(getCertNumRes);
        }
        catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @Transactional
    // 인증번호 입력받아 인증하기
    @ResponseBody
    @PostMapping("/code/{phoneNum}")
    public BaseResponse<GetCertRes> checkCertNum (@PathVariable String phoneNum, @RequestParam String code) throws BaseException {
        try{
            // 전화번호 형식 체크
            if(isRegexTelephoneNum(phoneNum)){
                throw new BaseException(INVALID_PHONENUMBER);
            }
            // 인증번호 형식 체크
            if(isRegexCertCode(code)){
                throw new BaseException(INVALID_CERTCODE);
            }
            GetCertRes getCertRes = messageProvider.checkCode(phoneNum,code);
            // 성공한 경우
            if(getCertRes.isCertificated()){
                messageService.removeCertInfo(phoneNum, code);
                return new BaseResponse<>(SUCCESS_TO_CERTIFICATE, getCertRes);
            }
            else{
                messageService.removeCertInfo(phoneNum, code);
                return new BaseResponse<>(FAILED_TO_CERTIFICATE, getCertRes);
            }
        }
        catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
