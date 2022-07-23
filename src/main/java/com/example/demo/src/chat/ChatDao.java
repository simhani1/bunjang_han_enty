package com.example.demo.src.chat;

import com.example.demo.src.chat.model.GetChatRes;
import com.example.demo.src.chat.model.PostChatReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ChatDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * 메세지 전송
     * @param userId
     * @param roomId
     * @param postChatReq
     * @return
     */
    public int sendMessage(int userId, int roomId, PostChatReq postChatReq){
        String sendMessageQuery = "insert into chattingMessage (userId, chatRoomId, message, messageType) values (?,?,?,?)";
        Object[] sendMessageParams = new Object[]{userId, roomId, postChatReq.getMessage(), postChatReq.getMessageType()};

        return this.jdbcTemplate.update(sendMessageQuery, sendMessageParams);
    }

    public List<GetChatRes> getLastMessageType(int roomId){
        String getLastMessageTypeQuery = "select userId, chatRoomId, message, messageType, createdAt from chattingMessage where chatRoomId=?";
        int getLastMessageTypeParams = roomId;

        return this.jdbcTemplate.query(getLastMessageTypeQuery,
                (rs, rowNum) -> new GetChatRes(
                        rs.getInt("userId"),
                        rs.getInt("chatRoomId"),
                        rs.getString("message"),
                        rs.getString("messageType"),
                        rs.getString("createdAt")),
                getLastMessageTypeParams);
    }
}
