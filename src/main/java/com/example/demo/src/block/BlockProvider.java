package com.example.demo.src.block;

import com.example.demo.config.BaseException;
import com.example.demo.src.block.model.GetBlockRes;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class BlockProvider {
    private final BlockDao blockDao;

    public BlockProvider(BlockDao blockDao){
        this.blockDao = blockDao;
    }

    // 차단 목록 조회
    List<GetBlockRes> getBlockListByUserId(int userId) throws BaseException {
        try{
            List<GetBlockRes> getBlockRes = blockDao.getBlockListByUserId(userId);
            return getBlockRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 이미 차단된 유저인지 확인
    public int checkExistsBlockUser(int userId, int blockUserId){
        return blockDao.checkExistsBlockUser(userId, blockUserId);
    }

    // 유저 테이블에 존재하는 유저인지 확인
    public int checkExistsUser(int userId){
        return blockDao.checkExistsUser(userId);
    }
}
