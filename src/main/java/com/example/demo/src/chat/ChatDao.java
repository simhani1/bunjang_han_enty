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

    public List<GetChatRes> getChatList(int roomId){
        String FormatData = "chattingMessage.createdAt";
        String dateFormatQuery =
                "case when timestampdiff(second , "+FormatData+", current_timestamp) <60 " +
                        "then concat(timestampdiff(second, "+FormatData+", current_timestamp),'초 전') " +
                        "when timestampdiff(minute , "+FormatData+", current_timestamp) <60 " +
                        "then concat(timestampdiff(minute, "+FormatData+", current_timestamp),'분 전') " +
                        "when timestampdiff(hour , "+FormatData+", current_timestamp) <24 " +
                        "then concat(timestampdiff(hour, "+FormatData+", current_timestamp),'시간 전') " +
                        "when timestampdiff(day , "+FormatData+", current_timestamp) <365 " +
                        "then concat(timestampdiff(day, "+FormatData+", current_timestamp),'일 전') " +
                        "else concat(timestampdiff(year, current_timestamp, "+FormatData+"),' 년 전') end as ";

        String getChatListQuery =
                "select userId, chatRoomId, message, messageType, " +
                dateFormatQuery + "'createdAt' from chattingMessage where chatRoomId=?";
        int getChatListParams = roomId;

        return this.jdbcTemplate.query(getChatListQuery,
                (rs, rowNum) -> new GetChatRes(
                        rs.getInt("userId"),
                        rs.getInt("chatRoomId"),
                        rs.getString("message"),
                        rs.getString("messageType"),
                        rs.getString("createdAt")),
                getChatListParams);
    }

    // 채팅방에 유저가 존재하는지 체크
    public int checkUserExistRoom(int userId, int roomId){
        String checkUserExistRoomQuery = "select exists(select userId from chattingMessage where userId=? and chatRoomId=?)";
        Object[] checkUserExistRoomParams = new Object[]{userId, roomId};

        System.out.println(this.jdbcTemplate.queryForObject(checkUserExistRoomQuery, int.class, checkUserExistRoomParams));
        return this.jdbcTemplate.queryForObject(checkUserExistRoomQuery, int.class, checkUserExistRoomParams);
    }
}
