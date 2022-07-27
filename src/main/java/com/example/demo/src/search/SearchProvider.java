package com.example.demo.src.search;

import com.example.demo.config.BaseException;
import com.example.demo.src.search.model.GetKeywordsLogRes;
import com.example.demo.src.search.model.GetProductByKeywordRes;
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
    @Transactional
    public List<GetProductByKeywordRes> getProductByKeyword(int userId, int page, String keyword, String type) throws BaseException {
        int amount = 9;
        try {
            List<GetProductByKeywordRes> getProductByKeywordRes = new ArrayList<>();
            // 삭제되지 않은 productId를 배열에 저장
            List<Integer> getExistProductsIdByKeyword = searchDao.getExistProductsIdByKeyword(userId, keyword);
            // 검색어가 검색내역에 없다면 검색 내역에 저장
            if(!searchDao.existKeywordsLog(userId, keyword))
                searchDao.saveKeywordsLog(userId, keyword);
            // paging
            for(int i = page*amount; i < (page + 1) * amount; i++) {
                // 해당 페이지에서 요청하는 글의 번호보다 존재하는 글의 번호가 더 작은 경우
                if(i >= getExistProductsIdByKeyword.size())
                    break;
                int productId = getExistProductsIdByKeyword.get(i);
                // 해당 글의 제목/본문에 키워드가 포함된다면 배열에 정보 저장
                if(searchDao.checkProductByKeyword(productId, keyword))
                    getProductByKeywordRes.add(searchDao.getProductByKeyword(userId, productId));
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

    // 검색 내역 조회(최신 검색어 6개)
    public List<GetKeywordsLogRes> getKeywordsLog(int userId) throws BaseException {
        try{
            return searchDao.getKeywordsLog(userId);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 인기 검색어 조회
    public List<GetKeywordsLogRes> getHotKeywordsLog() throws BaseException {
        try{
            return searchDao.getHotKeywordsLog();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

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
        public int compare(GetProductByKeywordRes t1, GetProductByKeywordRes t2) {
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
}
