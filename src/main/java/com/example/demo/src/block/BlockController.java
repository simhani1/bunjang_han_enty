package com.example.demo.src.block;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.block.model.DeleteBlockRes;
import com.example.demo.src.block.model.GetBlockRes;
import com.example.demo.src.block.model.PostBlockRes;
import com.example.demo.utils.JwtService;
import lombok.Getter;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/block-lists")
public class BlockController{
    private final BlockProvider blockProvider;
    private final BlockService blockService;
    private final BlockDao blockDao;
    private final JwtService jwtService;

    @Autowired
    public BlockController(BlockProvider blockProvider, BlockService blockService, BlockDao blockDao, JwtService jwtService){
        this.blockProvider = blockProvider;
        this.blockService = blockService;
        this.blockDao = blockDao;
        this.jwtService = jwtService;
    }

    // 유저 차단
    @PostMapping("/{userId}/{blockUserId}")
    public BaseResponse<PostBlockRes> addBlockList(@PathVariable int userId,
                                                   @PathVariable int blockUserId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(blockUserId <= 0){
                return new BaseResponse<>(INVALID_BLOCK_USER_ID);
            }
            PostBlockRes postBlockRes = blockService.addBlockList(userId, blockUserId);
            return new BaseResponse<>(BLOCK_USER_SUCCESS,postBlockRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 차단한 유저 목록
    @GetMapping("/{userId}")
    public BaseResponse<List<GetBlockRes>> getBlockListByUserId(@PathVariable int userId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetBlockRes> getBlockRes = blockProvider.getBlockListByUserId(userId);
            return new BaseResponse<>(GET_BLOCK_LIST_SUCCESS,getBlockRes);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // 차단 해제
    @DeleteMapping("/{userId}/{blockUserId}")
    public BaseResponse<DeleteBlockRes> deleteBlockUser(@PathVariable int userId,
                                                        @PathVariable int blockUserId){
        try {
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            if(blockUserId <= 0){
                return new BaseResponse<>(INVALID_BLOCK_USER_ID);
            }
            DeleteBlockRes deleteBlockUser = blockService.deleteBlockUser(userId, blockUserId);
            return new BaseResponse<>(DELETE_BLOCK_USER_SUCCESS,deleteBlockUser);
        } catch (BaseException exception){
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
