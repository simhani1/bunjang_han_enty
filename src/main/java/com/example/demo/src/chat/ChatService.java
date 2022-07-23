package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatService {
    private final ChatDao chatDao;

    @Autowired
    public ChatService(ChatDao chatDao){
        this.chatDao = chatDao;
    }

    public PostChatRes sendMessage(int userId, int roomId, PostChatReq postChatReq) throws BaseException {
        // TODO: roomId가 max(roomId) 보다 클때 validation
//        if(roomId > roomProvider.getLastRoomId()){
//            throw new BaseException(NO_EXISTED_ROOM);
//        }
        try {
            PostChatRes postChatRes = new PostChatRes();
            if(1 == chatDao.sendMessage(userId, roomId, postChatReq)){
                postChatRes.setMessage(postChatReq.getMessage());
                postChatRes.setMessageType(postChatReq.getMessageType());
            }
            return postChatRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
