package com.example.demo.src.messageCertification;

import com.example.demo.config.BaseException;
import com.example.demo.src.messageCertification.model.GetCertRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service

public class MessageProvider {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final MessageDao messageDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public MessageProvider(MessageDao messageDao, JwtService jwtService) {
        this.messageDao = messageDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************

    // 인증번호와 전화번호 일대일 체크
    public GetCertRes checkCode(String phoneNum, String code) throws BaseException {
        try {
            GetCertRes getCerRes = new GetCertRes(phoneNum, true);
            // 인증에 실패하는 경우
            if(!messageDao.checkCode(phoneNum, code)){
                getCerRes.setCertificated(false);
            }
            return getCerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
