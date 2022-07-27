package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.follow.model.GetFollowerRes;
import com.example.demo.src.follow.model.GetFollowingRes;
import com.example.demo.src.follow.model.PostFollowReq;
import com.example.demo.src.follow.model.PostFollowRes;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/app/follows")
public class FollowController {
    private final FollowProvider followProvider;
    private final FollowService followService;
    private final FollowDao followDao;
    private final JwtService jwtService;

    @Autowired
    public FollowController(FollowProvider followProvider, FollowService followService, FollowDao followDao, JwtService jwtService){
        this.followProvider = followProvider;
        this.followService = followService;
        this.followDao = followDao;
        this.jwtService = jwtService;
    }

    // 팔로우하기
    @PostMapping("/{userId}/{followUserId}")
    public BaseResponse<PostFollowRes> follow(@PathVariable("userId") int userId,
                                              @PathVariable("followUserId") int followUserId,
                                              @RequestBody PostFollowReq postFollowReq){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            // 본인을 팔로우할때
            if(userId == followUserId){
                return new BaseResponse<>(CAN_NOT_FOLLOW_MYSELF);
            }
            // 팔로우 번호가 음수일 때
            if(followUserId <= 0){
                return new BaseResponse<>(REQUEST_REJECT_FOLLOW_ID);
            }
            // postReq.status가 빈값일때
            if(postFollowReq.getStatus() == null){
                return new BaseResponse<>(EMPTY_FOLLOW_STATUS);
            }

            PostFollowRes postFollowRes = followService.follow(userId, followUserId, postFollowReq.getStatus());
            return new BaseResponse<>(FOLLOW_SUCCESS, postFollowRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팔로워 조회
    @GetMapping("/to-me/{userId}")
    public BaseResponse<List<GetFollowerRes>> getFollowers(@PathVariable("userId") int userId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowerRes> getFollowerRes = followProvider.getFollowers(userId);
            return new BaseResponse<>(GET_FOLLOWER_LIST_SUCCESS,getFollowerRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //팔로잉 조회
    @GetMapping("/to-you/{userId}")
    public BaseResponse<List<GetFollowingRes>> getFollowings(@PathVariable("userId") int userId){
        try{
            if(userId != jwtService.getUserId()){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetFollowingRes> getFollowingRes = followProvider.getFollowings(userId);
            return new BaseResponse<>(GET_FOLLOWING_LIST_SUCCESS,getFollowingRes);
        } catch (BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
