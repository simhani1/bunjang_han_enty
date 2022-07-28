package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.GetProductIdRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class UserProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


    // 로그인
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        // 존재하지 않는 아이디인지 체크
        if(userDao.checkId(postLoginReq.getId()) == 0){
            throw new BaseException(NO_EXISTED_ID);
        }
        // 탈퇴한 유저인지 체크
        if(userDao.checkStatus(postLoginReq.getId()).equals("inactive")){
            throw new BaseException(NOT_ACTIVE_USER);
        }
        User user = userDao.getPwd(postLoginReq);
        String decryptPwd;
        try {
            decryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPwd()); // 복호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        if (postLoginReq.getPwd().equals(decryptPwd)) { //비말번호가 일치한다면 userId를 가져온다.
            int userId = user.getUserId();
            String jwt = jwtService.createJwt(userId);
            return new PostLoginRes(userId,jwt);
        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // 해당 사용자의 판매중 상품 조회(userId == otherId 는 본인의 판매중 상품 조회)중
    @Transactional
    public List<GetUserProductRes> getUserProductRes_sel(int userId, int otherId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자가 판매중인 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(otherId, "sel");
            for(GetProductIdRes obj : productsId){
                // 삭제되지 않은 경우 false
                if(!userDao.checkProductIsDeleted(obj.getProductId()))
                    getUserProductRes.add(userDao.getUserProductRes(userId, otherId, obj.getProductId(), "sel"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 본인의 예약중 상품 조회
    @Transactional
    public List<GetUserProductRes> getUserProductRes_res(int userId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자의 예약중인 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId,"res");
            for(GetProductIdRes obj : productsId){
                // 삭제되지 않은 경우 false
                if(!userDao.checkProductIsDeleted(obj.getProductId()))
                    getUserProductRes.add(userDao.getUserProductRes(userId, userId, obj.getProductId(), "res"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 본인의 판매완료 상품 조회
    @Transactional
    public List<GetUserProductRes> getUserProductRes_fin(int userId) throws BaseException {
        try {
            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
            // 해당 사용자의 판매완료 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId, "fin");
            for(GetProductIdRes obj : productsId){
                // 삭제되지 않은 경우 false
                if(!userDao.checkProductIsDeleted(obj.getProductId()))
                    getUserProductRes.add(userDao.getUserProductRes(userId, userId, obj.getProductId(), "fin"));
            }
            return getUserProductRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
    public GetMyPageRes getMyPage(int userId) throws BaseException {
        try {
            GetMyPageRes getMyPageRes = userDao.getMyPage(userId);
            // 별점 평균 0.5단윈로 수정
            if(0 <= getMyPageRes.getStar() && getMyPageRes.getStar()<0.5)
                getMyPageRes.setStar(0);
            else if(0.5 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 1)
                getMyPageRes.setStar(0.5);
            else if(1 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 1.5)
                getMyPageRes.setStar(1);
            else if(1.5 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 2)
                getMyPageRes.setStar(1.5);
            else if(2 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 2.5)
                getMyPageRes.setStar(2);
            else if(2.5 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 3)
                getMyPageRes.setStar(2.5);
            else if(3 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 3.5)
                getMyPageRes.setStar(3);
            else if(3.5 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 4)
                getMyPageRes.setStar(3.5);
            else if(4 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 4.5)
                getMyPageRes.setStar(4);
            else if(4.5 <= getMyPageRes.getStar() && getMyPageRes.getStar() < 5)
                getMyPageRes.setStar(4.5);
            else if(4.5 <= getMyPageRes.getStar())
                getMyPageRes.setStar(5);
            return getMyPageRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상점후기 조회
    public List<GetShopReviewRes> getShopReview(int userId) throws BaseException {
        try {
            List<GetShopReviewRes> getShopReview = new ArrayList<>();
            // 해당 사용자의 판매완료 상품 id 값을 리스트에 저장
            List<GetProductIdRes> productsId = userDao.getProductId(userId, "fin");
            for(GetProductIdRes obj : productsId){
                // 삭제된 글일경우 true
                if(userDao.checkProductIsDeleted(obj.getProductId()))
                    continue;
                // 리뷰가 없다면 pass
                if(!userDao.checkReviewExist(obj.getProductId()))
                    continue;
                getShopReview.add(userDao.getShopReview(userId, obj.getProductId()));
            }
            Collections.sort(getShopReview, new GetShopReviewComparator());
            return getShopReview;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 찜하기
    public PostHeartRes addHeartList(int userId, int productId, boolean status) throws BaseException {
        // 해당 물건이 존재하는 물건인지 체크
        if(!userDao.checkProductExist(productId)){
            throw new BaseException(INVALID_PRODUCTID);
        }
        // 본인의 물건인 경우 찜하기 불가능
        if(userDao.checkProductOwner(userId, productId) == 1){
            throw new BaseException(YOUR_PRODUCT);
        }
        // 삭제된 물건인지 체크
        if(userDao.checkProductIsDeleted(productId)){
            throw new BaseException(DELETED_PRODUCT);
        }
        // 판매완료 상품인지 체크
        if(userDao.checkProductCondition(productId)){
            throw new BaseException(SOLD_OUT_PRODUCT);
        }
        try {
            int result;
            // 이미 찜해둔 상태인지 체크
            if(userDao.checkHeartListExists(userId, productId) == 1){
                result = userDao.addHeartList_modify(userId, productId, status);
            }
            else{
                result = userDao.addHeartList(userId, productId, true);
            }
            // 결과 반영이 성공적으로 이루어진 경우 요청받은 status 값을 반환
            if(result == 1){
                return new PostHeartRes(status);
            }
            throw new BaseException(FAILED_TO_ADD_HEARTLIST);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

     // 찜목록 조회록
    @Transactional
    public List<GetHeartProductsRes> getHeartProducts(int userId) throws BaseException {
        try {
            List<GetHeartProductsRes> getHeartProductsList = new ArrayList<>();
            // 해당 유저가 찜한 productId를 배열에 저장
            List<Integer> getHeartProductsId = userDao.getHeartProductsId(userId);
            // 찜한 물건이 없을 경우 빈 결과배열을 반환
            if(!getHeartProductsId.isEmpty()) {
                for (int productId : getHeartProductsId) {
                    getHeartProductsList.add(userDao.getHeartProducts(userId, productId));
                }
            }
            return getHeartProductsList;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
    // 해당 아이디 중복성 체크
    public int checkId(String id) throws BaseException {
        try {
            return userDao.checkId(id);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 닉네임 중복성 체크
    public int checkNickname(String nickname) throws BaseException {
        try {
            return userDao.checkNickname(nickname);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 전화번호 중복성 체크
    public int checkPhoneNum(String phoneNum) throws BaseException {
        try {
            return userDao.checkPhoneNum(phoneNum);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 이메일 중복성 체크
    public int checkEmail(String email) throws BaseException {
        try {
            return userDao.checkEmail(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}

// 상점후기 시간순 정렬
class GetShopReviewComparator implements Comparator<GetShopReviewRes>{
    @Override
    public int compare(GetShopReviewRes t1, GetShopReviewRes t2) {
        Timestamp time_t1 = t1.getTime();
        Timestamp time_t2 = t2.getTime();
        if(time_t1.before(time_t2))
            return 1;
        else if(time_t1.after(time_t2))
            return -1;
        else
            return 0;
    }
}
