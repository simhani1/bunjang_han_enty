package com.example.demo.src.account;

import com.example.demo.src.account.model.GetAccountRes;
import com.example.demo.src.account.model.PatchAccountReq;
import com.example.demo.src.account.model.PostAccountReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.List;

@Repository

public class AccountDao {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    @Transactional
    // 계좌 추가
    public int createAccount(int userId, PostAccountReq postAccountReq) {
        String lastInsertIdQuery = "select count(*) from accountList";
        int lastInsertId = this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class) + 1;
        String createAccountQuery = "insert into accountList (accountId, userid, name, bankId, accountNum, standard) VALUES (?,?,?,?,?,?)";
        Object[] createAccountParams = new Object[]{lastInsertId, userId, postAccountReq.getName(), postAccountReq.getBankId(), postAccountReq.getAccountNum(), postAccountReq.isStandard()};
        this.jdbcTemplate.update(createAccountQuery, createAccountParams);
        return lastInsertId; // accountId 반환


    }

    // 계좌 조회하기
    public List<GetAccountRes> getAllAccount(int userId) {
        String getAllAccountQuery = "select\n" +
                "    accountList.standard as 'standard',\n" +
                "    bank.bankImgUrl as 'bankImgUrl',\n" +
                "    bank.bankName as 'bankName',\n" +
                "    accountList.accountNum as 'accountNum',\n" +
                "    accountList.name as 'name'\n" +
                "from accountList\n" +
                "inner join bank on accountList.bankId = bank.bankId\n" +
                "where accountList.userId = ?";
        int getAllAccountParams = userId;
        return this.jdbcTemplate.query(getAllAccountQuery,
                (rs, rowNum) -> new GetAccountRes(
                        rs.getBoolean("standard"),
                        rs.getString("bankImgUrl"),
                        rs.getString("bankName"),
                        rs.getString("accountNum"),
                        rs.getString("name")),
                getAllAccountParams);
    }

    // 계좌 삭제
    public int deleteAccount(int userId, int accountId) {
        String deleteAccountQuery = "delete from accountList where accountId = ? and userId = ?";
        Object [] deleteAccountParams = new Object[]{accountId, userId};
        return this.jdbcTemplate.update(deleteAccountQuery, deleteAccountParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 계좌 정보 수정
    public int modifyAccount(int userId, int accountId, PatchAccountReq patchAccountReq) {
        String modifyAccountQuery = "update accountList set name = ?, bankId = ?, accountNum = ?, standard = ? where accountId = ? and userId = ? ";
        Object [] modifyAccountParams = new Object[]{patchAccountReq.getName(), patchAccountReq.getBankId(), patchAccountReq.getAccountNum(), patchAccountReq.isStandard(), accountId, userId};
        return this.jdbcTemplate.update(modifyAccountQuery, modifyAccountParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 해당 은행 존재하는지 체크
    public int checkBankId(int bankId) {
        String checkBankIdQuery = "select exists(select bankId from bank where bankId = ?)";
        int checkBankIdParams = bankId;
        return this.jdbcTemplate.queryForObject(checkBankIdQuery,
                int.class,
                checkBankIdParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 계좌번호 중복성 체크
    public int checkAccountNum(String accountNum) {
        String checkAccountNumQuery = "select exists(select accountNum from accountList where accountNum = ?)";
        String checkAccountNumParams = accountNum;
        return this.jdbcTemplate.queryForObject(checkAccountNumQuery,
                int.class,
                checkAccountNumParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 해당 유저가 등록한 계좌 개수 체크(최대 2개까지)
    public int checkAccountCnt(int userId) {
        String checkAccountCntQuery = "select count(*) from accountList where userId = ?";
        int checkAccountCntParams = userId;
        return this.jdbcTemplate.queryForObject(checkAccountCntQuery,
                int.class,
                checkAccountCntParams);  // 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 기존 계좌 기본 계좌 해제하기
    public void changeStandard(int userId) {
        String changeStandardQuery = "update accountList set standard = false where userId = ? and standard = true";
        int changeStandardParams = userId;
        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
    }

    // 삭제하고 남는 계좌를 기본계좌로 설정하기
    public void changeStandard(int userId, int accountId) {
        String changeStandardQuery = "update accountList set standard = true where userId = ? and accountId != ?";
        Object[] changeStandardParams = new Object[]{userId, accountId};
        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
    }

    // 수정하는 계좌의 기본계좌 설정과 반대로
    public void changeStandard_modify(int userId, int accountId, boolean standard) {
        String changeStandardQuery = "update accountList set standard = ? where userId = ? and accountId != ?";
        Object[] changeStandardParams = new Object[]{standard, userId, accountId};
        this.jdbcTemplate.update(changeStandardQuery, changeStandardParams);
    }

    // 해당 계좌가 존재하는지 & 본인의 계좌가 맞는지 체크
    public int checkAccountExist(int userId, int accountId) {
        String changeStandardQuery = "select exists(select accountId from accountList where userId = ? and accountId = ?)";
        Object[] changeStandardParams = new Object[]{userId, accountId};
        return this.jdbcTemplate.queryForObject(changeStandardQuery, int.class, changeStandardParams);
    }
}
