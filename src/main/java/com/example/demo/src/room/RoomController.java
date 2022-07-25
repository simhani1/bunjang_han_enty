package com.example.demo.src.room;

import com.example.demo.src.chat.ChatProvider;
import com.example.demo.src.room.model.PostRoomRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/chat")
public class RoomController {

    private final RoomProvider roomProvider;
    private final RoomService roomService;
    private final RoomDao roomDao;
    private final ChatProvider chatProvider;
    private final JwtService jwtService;

    @Autowired
    public RoomController(RoomProvider roomProvider, RoomService roomService, RoomDao roomDao, ChatProvider chatProvider, JwtService jwtService){
        this.roomProvider = roomProvider;
        this.roomService = roomService;
        this.roomDao = roomDao;
        this.chatProvider = chatProvider;
        this.jwtService = jwtService;
    }

//    @PostMapping("/{userId}/{productId")
//    public PostRoomRes startChat(@PathVariable("userId") int userId,
//                                 @PathVariable("productId") int productId){
//        try{
//
//        } catch {
//
//        }
//    }

}
