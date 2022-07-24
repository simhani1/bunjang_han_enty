package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.GetProductByKeywordRes;
import com.example.demo.src.user.model.GetShopReviewRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class SearchProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final SearchDao searchDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public SearchProvider(SearchDao searchDao, JwtService jwtService) {
        this.searchDao = searchDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************

    // 검색어로 판매글 검색
    public List<GetProductByKeywordRes> getProductByKeyword(int userId, int lastProductId, String keyword, String type) throws BaseException {
        int amount = 3;
        try {
            List<GetProductByKeywordRes> getProductByKeywordRes = new ArrayList<>();
            // paging
            int cnt = 1;
            int productId = lastProductId;  // 마지막으로 삽입된 id값 다음부터 amount만큼 탐색 후 정보 저장
            int productId_end = searchDao.getLastProductId();
            while(cnt <= amount && productId <= productId_end) {
                // 해당 물건이 삭제된 경우 or 판매완료 상품인 경우 pass
                if(searchDao.checkProductIsDeleted(productId) || searchDao.checkProductCondition(productId)) {
                    productId++;
                    continue;
                }
                // 해당 글의 제목/본문에 키워드가 포함된다면 배열에 정보 저장
                if(searchDao.checkProductByKeyword(productId, keyword)) {
                    getProductByKeywordRes.add(searchDao.getProductByKeyword(userId, productId));
                    productId++;
                    cnt++;
                }
                // 해당 키워드가 포함되지 않은 경우 pass
                else
                    productId++;
            }
            if(type.equals("ascend"))
                Collections.sort(getProductByKeywordRes, new GetProductByKeywordComparatorAscend());
            else if(type.equals("descend"))
                Collections.sort(getProductByKeywordRes, new GetProductByKeywordComparatorDescend());
            else if(type.equals("recent"))
                Collections.sort(getProductByKeywordRes, new GetProductByKeywordComparatorRecent());
            return getProductByKeywordRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    // 로그인
//    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
//        // 존재하지 않는 아이디인지 체크
//        if(userDao.checkId(postLoginReq.getId()) == 0){
//            throw new BaseException(NO_EXISTED_ID);
//        }
//        // 탈퇴한 유저인지 체크
//        if(userDao.checkStatus(postLoginReq.getId()).equals("inactive")){
//            throw new BaseException(NOT_ACTIVE_USER);
//        }
//        User user = userDao.getPwd(postLoginReq);
//        String decryptPwd;
//        try {
//            decryptPwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPwd()); // 복호화
//            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 암호화된 값끼리 비교를 해야합니다.
//        } catch (Exception ignored) {
//            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
//        }
//        if (postLoginReq.getPwd().equals(decryptPwd)) { //비말번호가 일치한다면 userId를 가져온다.
//            int userId = user.getUserId();
//            String jwt = jwtService.createJwt(userId);
//            return new PostLoginRes(userId,jwt);
//        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
//            throw new BaseException(FAILED_TO_LOGIN);
//        }
//    }
//
//    // 본인의 판매중 상품 조회
//    public List<GetUserProductRes> getUserProductRes_sel(int userId) throws BaseException {
//        try {
//            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
//            // 해당 사용자가 판매중인 상품 id 값을 리스트에 저장
//            List<GetProductIdRes> productsId = userDao.getProductId(userId, "sel");
//            for(GetProductIdRes obj : productsId){
//                // 삭제되지 않은 경우 false
//                if(!userDao.checkProductIsDeleted(obj.getProductId()))
//                    getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "sel"));
//            }
//            return getUserProductRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 본인의 예약중 상품 조회
//    public List<GetUserProductRes> getUserProductRes_res(int userId) throws BaseException {
//        try {
//            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
//            // 해당 사용자의 예약중인 상품 id 값을 리스트에 저장
//            List<GetProductIdRes> productsId = userDao.getProductId(userId,"res");
//            for(GetProductIdRes obj : productsId){
//                // 삭제되지 않은 경우 false
//                if(!userDao.checkProductIsDeleted(obj.getProductId()))
//                    getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "res"));
//            }
//            return getUserProductRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 본인의 판매완료 상품 조회
//    public List<GetUserProductRes> getUserProductRes_fin(int userId) throws BaseException {
//        try {
//            List<GetUserProductRes> getUserProductRes = new ArrayList<>();
//            // 해당 사용자의 판매완료 상품 id 값을 리스트에 저장
//            List<GetProductIdRes> productsId = userDao.getProductId(userId, "fin");
//            for(GetProductIdRes obj : productsId){
//                // 삭제되지 않은 경우 false
//                if(!userDao.checkProductIsDeleted(obj.getProductId()))
//                    getUserProductRes.add(userDao.getUserProductRes(userId, obj.getProductId(), "fin"));
//            }
//            return getUserProductRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 마이페이지 조회(찜/후기/팔로워/팔로잉)
//    public GetMyPageRes getMyPage(int userId) throws BaseException {
//        try {
//            GetMyPageRes getMyPageRes = userDao.getMyPage(userId);
//            return getMyPageRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 상점후기 조회
//    public List<GetShopReviewRes> getShopReview(int userId) throws BaseException {
//        try {
//            List<GetShopReviewRes> getShopReview = new ArrayList<>();
//            // 해당 사용자의 판매완료 상품 id 값을 리스트에 저장
//            List<GetProductIdRes> productsId = userDao.getProductId(userId, "fin");
//            for(GetProductIdRes obj : productsId){
//                // 삭제된 글일경우 true
//                if(userDao.checkProductIsDeleted(obj.getProductId()))
//                    continue;
//                // 리뷰를 작성하지 않았다면 리뷰 작성시간이 null값이므로 필터링
//                if(userDao.getShopReview(obj.getProductId()).getTime() == null)
//                    continue;
//                getShopReview.add(userDao.getShopReview(obj.getProductId()));
//            }
//            Collections.sort(getShopReview, new com.example.demo.src.user.GetShopReviewComparator());
//            return getShopReview;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 찜하기
//    public PostHeartRes addHeartList(int userId, int productId, boolean status) throws BaseException {
//        // 해당 물건이 존재하는 물건인지 체크
//        if(!userDao.checkProductExist(productId)){
//            throw new BaseException(INVALID_PRODUCTID);
//        }
//        // 본인의 물건인 경우 찜하기 불가능
//        if(userDao.checkProductOwner(userId, productId) == 1){
//            throw new BaseException(YOUR_PRODUCT);
//        }
//        // 삭제된 물건인지 체크
//        if(userDao.checkProductIsDeleted(productId)){
//            throw new BaseException(DELETED_PRODUCT);
//        }
//        // 판매완료 상품인지 체크
//        if(userDao.checkProductCondition(productId)){
//            throw new BaseException(SOLD_OUT_PRODUCT);
//        }
//        try {
//            int result;
//            // 이미 찜해둔 상태인지 체크
//            if(userDao.checkHeartListExists(userId, productId) == 1){
//                result = userDao.addHeartList_modify(userId, productId, status);
//            }
//            else{
//                result = userDao.addHeartList(userId, productId, true);
//            }
//            // 결과 반영이 성공적으로 이루어진 경우 요청받은 status 값을 반환
//            if(result == 1){
//                return new PostHeartRes(status);
//            }
//            throw new BaseException(FAILED_TO_ADD_HEARTLIST);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    //////////////////////////////////////////////// VALIDATION ///////////////////////////////////////////////////
//    // 해당 아이디 중복성 체크
//    public int checkId(String id) throws BaseException {
//        try {
//            return userDao.checkId(id);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 해당 닉네임 중복성 체크
//    public int checkNickname(String nickname) throws BaseException {
//        try {
//            return userDao.checkNickname(nickname);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//
//    // 해당 전화번호 중복성 체크
//    public int checkPhoneNum(String phoneNum) throws BaseException {
//        try {
//            return userDao.checkPhoneNum(phoneNum);
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }
//}
//

// 낮은 가격순 정렬
class GetProductByKeywordComparatorDescend implements Comparator<GetProductByKeywordRes> {
    @Override
    public int compare(GetProductByKeywordRes t2, GetProductByKeywordRes t1) {
        if (t1.getPrice() > t2.getPrice())
            return 1;
        else if (t1.getPrice() < t2.getPrice())
            return -1;
        else
            return 0;
    }
}

// 높은 가격순 정렬
class GetProductByKeywordComparatorAscend implements Comparator<GetProductByKeywordRes> {
    @Override
    public int compare(GetProductByKeywordRes t2, GetProductByKeywordRes t1) {
        if (t1.getPrice() > t2.getPrice())
            return -1;
        else if (t1.getPrice() < t2.getPrice())
            return 1;
        else
            return 0;
    }
}

// 상점후기 시간순 정렬
class GetProductByKeywordComparatorRecent implements Comparator<GetProductByKeywordRes> {
    @Override
    public int compare(GetProductByKeywordRes getProductByKeywordRes, GetProductByKeywordRes t1) {
        return 0;
    }
}}