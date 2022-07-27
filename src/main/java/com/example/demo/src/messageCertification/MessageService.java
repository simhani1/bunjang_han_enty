package com.example.demo.src.messageCertification;


import com.example.demo.src.user.model.PostUserReq;
import com.example.demo.utils.JwtService;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service

public class MessageService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final MessageDao messageDao;
    private final MessageProvider messageProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    @Autowired //readme 참고
    public MessageService(MessageDao messageDao, MessageProvider messageProvider, JwtService jwtService) {
        this.messageDao = messageDao;
        this.messageProvider = messageProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************

    public void certifiedPhoneNumber(String phoneNumber, String cerNum) {
        String api_key = "NCSUR5IYIRLN0TRX";
        String api_secret = "9U5XQXLLDL7JPVRITYRDRWSXRMNLPFQK";
        Message coolsms = new Message(api_key, api_secret);
        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);    // 수신전화번호
        params.put("from", "01090788948");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "번개장터 인증번호는" + "["+cerNum+"]" + "입니다. 본인이 요청하지 않은 경우 ");
        params.put("app_version", "test app 4.1"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());
        } catch (CoolsmsException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCode());
        }
    }
}
