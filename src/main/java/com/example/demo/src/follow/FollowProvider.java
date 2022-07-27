package com.example.demo.src.follow;

import com.example.demo.config.BaseException;
import com.example.demo.src.follow.model.GetFollowerRes;
import com.example.demo.src.follow.model.GetFollowingRes;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class FollowProvider {
    private final FollowDao followDao;

    public FollowProvider(FollowDao followDao){
        this.followDao = followDao;
    }

    // 팔로우 조회
    public List<GetFollowerRes> getFollowers(int userId) throws BaseException {
        try{
            List<GetFollowerRes> getFollowerRes = followDao.getFollowers(userId);
            return getFollowerRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로잉 조회
    @Transactional(readOnly = true)
    public List<GetFollowingRes> getFollowings(int userId) throws BaseException {
        try{
            List<GetFollowingRes> getFollowingRes = followDao.getFollowings(userId);
            return getFollowingRes;
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
