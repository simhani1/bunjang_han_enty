package com.example.demo.src.messageCertification;

import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
