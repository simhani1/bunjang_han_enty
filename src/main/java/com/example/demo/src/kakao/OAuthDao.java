package com.example.demo.src.kakao;

import com.example.demo.src.kakao.model.GetUserIdRes;
import com.example.demo.src.kakao.model.PostAutoLoginReq;
import com.example.demo.src.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository
public class OAuthDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // phoneNum db에 있는지 체크
    public int checkExistsPhoneNum(String phoneNum){
        String comparePhoneNumQuery = "select exists(select userId from user where status=true and phoneNum=?)";
        String comparePhoneNumParam = phoneNum;

        return this.jdbcTemplate.queryForObject(comparePhoneNumQuery, int.class, comparePhoneNumParam);
    }

    // phoneNum로 유저번호 찾기
    public GetUserIdRes getUserByPhoneNum(String phoneNum){
        String getUserByPhoneNumQuery = "select userId from user where phoneNum=?";
        String getUserByphoneNumParam = phoneNum;

        return this.jdbcTemplate.queryForObject(getUserByPhoneNumQuery,
                (rs, rowNum) -> new GetUserIdRes(
                        rs.getInt("userId")),
                getUserByphoneNumParam);
    }

//    public int checkUserIdJwtForLogin(PostAutoLoginReq postAutoLoginReq){
//        String checkUserJwtForLoginQuery = "select exists(select userId from user where userId=? and jwtToken=?)";
//        Object[] checkUserJwtForLoginParam = new Object[]{postAutoLoginReq.getUserId(), postAutoLoginReq.getJwt()};
//
//        return this.jdbcTemplate.queryForObject(checkUserJwtForLoginQuery, int.class, checkUserJwtForLoginParam);
//    }
}
