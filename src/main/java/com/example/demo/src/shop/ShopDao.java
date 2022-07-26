package com.example.demo.src.shop;

import com.example.demo.src.shop.model.PatchShopReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class ShopDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 상점 수정
    public int modifyShops(int userId, PatchShopReq patchShopReq){
        String modifyShopUserQuery = "update user set profileImgUrl=?, nickname=? where userId=?";
        String modifyShopsQuery = "update shop set startTimeId=?, endTimeId=?, introduce=?, policy=?, caution=? where userId=?";

        Object[] modifyShopUserParams = new Object[]{patchShopReq.getProfileImgUrl(), patchShopReq.getNickname(), userId};
        Object[] modifyShopsParams = new Object[]{patchShopReq.getStartTimeId(), patchShopReq.getEndTimeId(), patchShopReq.getIntroduce(), patchShopReq.getPolicy(), patchShopReq.getCaution(), userId};
        int userResult = this.jdbcTemplate.update(modifyShopUserQuery, modifyShopUserParams);
        int shopResult = this.jdbcTemplate.update(modifyShopsQuery,modifyShopsParams);

        if(userResult == 1 && shopResult == 1){
            return 1;
        }
        return 0;
    }

    // 닉네임 수정 중복확인
    public int checkExistsModifyNickname(int userId, String nickname){
        String checkExistsModifyNicknameQuery = "select exists(select nickname from user where userId!=? and nickname=?)";
        Object[] checkExistsModifyNicknameParams = new Object[]{userId, nickname};

        return this.jdbcTemplate.queryForObject(checkExistsModifyNicknameQuery, int.class, checkExistsModifyNicknameParams);
    }
}
