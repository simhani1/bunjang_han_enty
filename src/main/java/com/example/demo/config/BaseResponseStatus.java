package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),
    SIGN_UP_SUCCESS(true, 1001, "회원가입에 성공하였습니다."),

    // [GET] /banner
    GET_BANNER_SUCCESS(true, 1048, "배너 조회에 성공하였습니다."),

    // [GET] /category
    GET_CATEGORY_SUCCESS(true, 1059, "카테고리 조회에 성공하였습니다."),
    GET_LAST_CATEGORY_SUCCESS(true, 1060, "하위 카테고리 조회에 성공하였습니다."),
    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // 회원가입(2010 ~ 2019)
    POST_EMPTY_ID(false, 2010, "아이디를 확인해주세요."),
    POST_EMPTY_PWD(false, 2011, "비밀번호를 확인해주세요."),
    POST_EMPTY_NICKNAME(false, 2012, "닉네임을 확인해주세요."),
    POST_EMPTY_LOCATION(false, 2013, "주소를 확인해주세요."),
    POST_USERS_EMPTY_PHONENUMBER(false, 2014, "전화번호를 확인해주세요."),
    POST_INVALID_PHONENUMBER(false, 2015, "전화번호 형식을 확인해주세요."),

    // 로그인(2020 ~ 2029)

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // 회원가입(3010 ~ 3020)
    POST_EXISTS_ID(false,3010,"이미 사용중인 아이디입니다."),
    POST_EXISTS_NICKNAME(false,3011,"이미 사용중인 닉네임입니다."),
    POST_EXISTS_PHONENUM(false,3012,"이미 사용중인 전화번호 입니다."),

    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),


    NO_EXISTED_CATEGORY_LIST(false, 3590, "등록된 카테고리가 없습니다."),

    // 하위 카테고리 조회(3600 ~ 3610)
    NO_EXISTED_FIRST_CATEGORY(false, 3600, "상위 카테고리가 존재하지 않습니다."),


    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다.");


    // 5000 : 필요시 만들어서 쓰세요
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
