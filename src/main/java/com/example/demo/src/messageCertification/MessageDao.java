package com.example.demo.src.messageCertification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

@Repository

public class MessageDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    // 인증번호 저장
    public int saveCode(String phoneNum, String code) {
        String lastIdQuery = "select count(*) from certCode";
        int lastInsertId = this.jdbcTemplate.queryForObject(lastIdQuery, int.class) + 1;
        String saveCodeQuery = "insert into certCode (codeId, phoneNum, code) VALUES (?,?,?)";  // 인증번호는 한번 사용되고 삭제되기 때문에 id값을 계산해줘야 한다.
        Object[] saveCodeParams= new Object[]{lastInsertId, phoneNum, code};
        return this.jdbcTemplate.update(saveCodeQuery,  saveCodeParams);
    }

    // 인증번호와 전화번호 일대일 체크
    public boolean checkCode(String phoneNum, String code){
        String checkCodeQuery = "select exists(select codeId from certCode where phoneNum = ? and code = ?)";
        Object[] checkCodeParams = new Object[]{phoneNum, code};
        return this.jdbcTemplate.queryForObject(checkCodeQuery,
                boolean.class,
                checkCodeParams);  // 일치하는 경우 true
    }

    // 인증정보 삭제
    public int removeCertInfo(String phoneNum, String code) {
        String removeCertInfoQuery = "delete from certCode where phoneNum = ? and code = ? ";
        Object[] removeCertInfoParams = new Object[]{phoneNum, code};
        return this.jdbcTemplate.update(removeCertInfoQuery, removeCertInfoParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }
}
