package com.example.demo.src.messageCertification;


import com.example.demo.config.BaseException;
import com.example.demo.utils.JwtService;
import net.nurigo.java_sdk.api.Message;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

import static com.example.demo.config.BaseResponseStatus.*;

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

    // 인증번호 전송
    public void certifiedPhoneNumber(String phoneNumber, String cerNum) throws BaseException {
        String api_key = "NCSUR5IYIRLN0TRX";
        String api_secret = "9U5XQXLLDL7JPVRITYRDRWSXRMNLPFQK";
        Message coolsms = new Message(api_key, api_secret);
        // 4 params(to, from, type, text) are mandatory. must be filled
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("to", phoneNumber);    // 수신전화번호
        params.put("from", "01090788948");    // 발신전화번호. 테스트시에는 발신,수신 둘다 본인 번호로 하면 됨
        params.put("type", "SMS");
        params.put("text", "번개장터 인증번호는" + "["+cerNum+"]" + "입니다. 본인이 요청하지 않은 경우 이 번호로 문의주세요.");
        params.put("app_version", "test app 4.1"); // application name and version

        try {
            JSONObject obj = (JSONObject) coolsms.send(params);
        } catch (CoolsmsException exception) {
            throw new BaseException(FAILED_TO_SEND_MESSAGE);
        }
    }

    // 인증정보 저장
    public void saveCode(String phoenNum, String code) throws BaseException {
        try {
            int result = messageDao.saveCode(phoenNum, code);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(SAVE_FAIL_INFO);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 인증정보 삭제
    public void removeCertInfo(String phoenNum, String code) throws BaseException {
        try {
            int result = messageDao.removeCertInfo(phoenNum, code);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(REMOVE_FAIL_INFO);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
