package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.src.chat.model.GetChatRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ChatProvider {
    private final ChatDao chatDao;

    @Autowired
    public ChatProvider (ChatDao chatDao){
        this.chatDao = chatDao;
    }

    // 마지막 채팅 타입 가져오기
    public GetChatRes getLastMessageType(int roomId) throws BaseException {
        try{
            List<GetChatRes> getChatRes = chatDao.getChatList(roomId);
            return getChatRes.get(getChatRes.size()-1);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetChatRes> getChatList(int userId, int roomId) throws BaseException{
        try{
            List<GetChatRes> getChatRes = chatDao.getChatList(roomId);
            return getChatRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
