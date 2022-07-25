package com.example.demo.src.chat;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.chat.model.GetChatRes;
import com.example.demo.src.chat.model.PostChatReq;
import com.example.demo.src.chat.model.PostChatRes;
import com.example.demo.src.product.ProductProvider;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/chat")
public class ChatController {

    private final ChatProvider chatProvider;
    private final ChatService chatService;
    private final ChatDao chatDao;
    private final JwtService jwtService;

    @Autowired
    public ChatController(ChatProvider chatProvider, ChatService chatService, ChatDao chatDao, JwtService jwtService){
        this.chatProvider = chatProvider;
        this.chatService = chatService;
        this.chatDao = chatDao;
        this.jwtService = jwtService;
    }

    /**
     * 메세지 전송
     * @param userId
     * @param roomId
     * @return
     */
    @PostMapping("/{userId}/{chatRoomId}")
    public BaseResponse<PostChatRes> sendMessage(@PathVariable("userId") int userId,
                                                 @PathVariable("chatRoomId") int roomId,
                                                 @RequestBody PostChatReq postChatReq){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            // roomId 가 음수 일 때
            if(roomId <= 0){
                return new BaseResponse<>(REQUEST_REJECT_ROOM_ID);
            }
            // 메세지가 문자열이 아닐 때
            if(postChatReq.getMessage().getClass() != String.class){
                return new BaseResponse<>(REQUEST_REJECT_MESSAGE_CLASS);
            }
            // 메세지 타입이 문자열이 아닐 때
            if(postChatReq.getMessageType().getClass() != String.class){
                return new BaseResponse<>(REQUEST_REJECT_MESSAGE_TYPE_CLASS);
            }
            // 메세지가 비어있을 때
            if(postChatReq.getMessage().equals("")){
                return new BaseResponse<>(EMPTY_MESSAGE);
            }
            // 메세지 타입이 비어있을 때
            if(postChatReq.getMessageType().equals("")){
                return new BaseResponse<>(EMPTY_MESSAGE_TYPE);
            }
            PostChatRes postChatRes = chatService.sendMessage(userId, roomId, postChatReq);
            return new BaseResponse<>(SEND_MESSAGE_SUCCESS,postChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @GetMapping("/{chatRoomId}")
    public BaseResponse<GetChatRes> getLastChatMessageType(@PathVariable("chatRoomId") int roomId){
        try{
            GetChatRes getChatRes = chatProvider.getLastMessageType(roomId);
            return new BaseResponse<>(getChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @GetMapping("/{userId}/{chatRoomId}")
    public BaseResponse<List<GetChatRes>> getChatList(@PathVariable("userId") int userId,
                                                      @PathVariable("chatRoomId") int roomId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(roomId <= 0){
                return new BaseResponse<>(REQUEST_REJECT_ROOM_ID);
            }
            List<GetChatRes> getChatRes = chatProvider.getChatList(userId, roomId);
            return new BaseResponse<>(BROWSE_ROOM_SUCCESS, getChatRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

}
