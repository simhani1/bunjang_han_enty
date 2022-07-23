package com.example.demo.src.chat;

import com.example.demo.src.chat.model.PostChatReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int sendMessage(int userId, int roomId, PostChatReq postChatReq){
        String sendMessageQuery = "insert into chattingMessage (userId, chatRoomId, message, messageType) values (?,?,?,?)";
        Object[] sendMessageParams = new Object[]{userId, roomId, postChatReq.getMessage(), postChatReq.getMessageType()};

        return this.jdbcTemplate.update(sendMessageQuery, sendMessageParams);
    }
}
