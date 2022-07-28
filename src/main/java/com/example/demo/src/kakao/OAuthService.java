package com.example.demo.src.kakao;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.kakao.model.GetUserIdRes;
import com.example.demo.src.kakao.model.PostCheckPhoneReq;
import com.example.demo.src.kakao.model.PostCheckPhoneRes;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.user.model.PostLoginRes;
import com.example.demo.utils.JwtService;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexTelephoneNum;


@Service
public class OAuthService {

    private final OAuthDao oAuthDao;
    private final OAuthProvider oAuthProvider;
    private final JwtService jwtService;
    private final UserDao userDao;


    public OAuthService(OAuthDao oAuthDao, OAuthProvider oAuthProvider, JwtService jwtService, UserDao userDao){
        this.oAuthDao = oAuthDao;
        this.oAuthProvider = oAuthProvider;
        this.jwtService = jwtService;
        this.userDao = userDao;
    }

    public String getKakaoAccessToken (String code) {
//        KakaoService
        String access_Token = "";
        String refresh_Token = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST 요청을 위해 기본값이 false인 setDoOutput을 true로
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST 요청에 필요로 요구하는 파라미터 스트림을 통해 전송
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=56457d412f4413b262b2355909466762"); // TODO REST_API_KEY 입력
            sb.append("&redirect_uri=http://njeat.shop:9000/app/users/kakao"); // TODO 인가코드 받은 redirect_uri 입력
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리에 포함된 클래스로 JSON파싱 객체 생성
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }

    public String createKakaoUser(String token) throws BaseException {

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + token); //전송할 header 작성, access_token전송

            //결과 코드가 200이라면 성공
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            //요청을 통해 얻은 JSON타입의 Response 메세지 읽어오기
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson 라이브러리로 JSON파싱
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            int id = element.getAsJsonObject().get("id").getAsInt();
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if(hasEmail){
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }

            // 사용자정보 저장

            System.out.println("id : " + id);
            System.out.println("email : " + email);

            br.close();

            return email;

        } catch (IOException e) {
            throw new BaseException(FAILED_KAKAO_CERTIFICATION);
        }
    }

    // 번호 db에 있는지 확인
    public PostCheckPhoneRes comparePhoneNum(String phoneNum) throws BaseException {
        // 폰번호 자릿수 체크
        if (isRegexTelephoneNum(phoneNum)){
            throw new BaseException(INVALID_PHONENUMBER);
        }
        // 폰번호 입력했는지 체크
        if (phoneNum.equals("")) {
            throw new BaseException(USERS_EMPTY_PHONENUMBER);
        }
        try{
            // 폰번호가 이미 존재한다면 로그인
            if(oAuthProvider.checkExistsPhoneNum(phoneNum) == 1){
                GetUserIdRes getUserIdRes = oAuthDao.getUserByPhoneNum(phoneNum);

                int userId = getUserIdRes.getUserId();
                String jwt = jwtService.createJwt(userId);
                return new PostCheckPhoneRes(true, userId, phoneNum, jwt);
            }
            // 폰번호가 없다면 회원가입
            return new PostCheckPhoneRes(false,0,phoneNum,"");
        } catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
