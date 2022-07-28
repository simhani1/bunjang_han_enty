package com.example.demo.src.block;

import com.example.demo.config.BaseException;
import com.example.demo.src.block.model.Block;
import com.example.demo.src.block.model.DeleteBlockRes;
import com.example.demo.src.block.model.PostBlockRes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class BlockService {
    private final BlockDao blockDao;
    private final BlockProvider blockProvider;

    public BlockService(BlockDao blockDao, BlockProvider blockProvider){
        this.blockDao = blockDao;
        this.blockProvider = blockProvider;
    }

    // 유저 차단
    @Transactional
    public PostBlockRes addBlockList(int userId, int blockUserId) throws BaseException {
        // 이미 차단한 유저인지
        if(blockProvider.checkExistsBlockUser(userId, blockUserId) == 1){
            throw new BaseException(BLOCKED_USER);
        }
        // 유저 테이블에 존재하는 유저인지
        if(blockProvider.checkExistsUser(blockUserId) != 1){
            throw new BaseException(NO_EXISTED_USER);
        }
        try{
            PostBlockRes postBlockRes = blockDao.addBlockList(userId, blockUserId);
            return postBlockRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public DeleteBlockRes deleteBlockUser(int userId, int blockUserId) throws BaseException {
        // 차단 목록에 없으면
        if(blockProvider.checkExistsBlockUser(userId, blockUserId) != 1){
            throw new BaseException(NO_BLOCKED_USER);
        }
        // 유저 테이블에 존재하는 유저인지
        if(blockProvider.checkExistsUser(blockUserId) != 1){
            throw new BaseException(NO_EXISTED_USER);
        }
        try{
            Block block = new Block();
            if(blockDao.deleteBlockUser(userId, blockUserId) == 1){
                block = new Block(userId, blockUserId);
            }
            DeleteBlockRes deleteBlockRes = new DeleteBlockRes(block.getUserId(), block.getBlockUserId());
            return deleteBlockRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
