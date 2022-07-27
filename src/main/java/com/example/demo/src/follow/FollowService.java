package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.PostFollowRes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FollowService {
    private final FollowDao followDao;

    public FollowService(FollowDao followDao){
        this.followDao = followDao;
    }

    // 팔로우
    @Transactional
    public PostFollowRes follow(int userId, int followUserId, Boolean status) throws BaseException {
        // 유저가 없는 유저일 때
        if(followDao.checkExistsUser(followUserId) != 1){
            throw new BaseException(NO_EXISTED_USER);
        }
        try{
            int result;
            // 이미 팔로우 했던 유저일때
            if(followDao.checkFollowStatus(userId, followUserId) == 1){
                System.out.println(followDao.checkFollowStatus(userId,followUserId));
                result = followDao.switchFollowStatus(userId, followUserId, status);
            }
            // 처음 팔로우 할때
            else{
                result = followDao.follow(userId, followUserId);
            }
            if(result == 1){
                return new PostFollowRes(status);
            }
            throw new BaseException(FAILD_USER_FOLLOW);
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
