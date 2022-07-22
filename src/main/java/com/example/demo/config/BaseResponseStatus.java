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
    LOG_IN_SUCCESS(true, 1002,"로그인에 성공하였습니다." ),
    MODIFY_STATUS_SUCCESS(true, 1003, "탈퇴처리 되었습니다."),
    MODIFY_PHONENUMBER_SUCCESS(true, 1004, "전화번호가 수정되었습니다."),
    MODIFY_GENDER_SUCCESS(true, 1005, "성별이 수정되었습니다."),
    ADD_ACCOUNT_SUCCESS(true, 1006, "계좌가 추가되었습니다."),
    ADD_ADDRESS_SUCCESS(true, 1007, "배송지가 추가되었습니다."),
    POST_PRODUCT_SUCCESS(true, 1020, "등록이 완료되었습니다."),
    // [GET] /products
    GET_PRODUCTS_SUCCESS(true,1021,"전체 상품을 조회하였습니다."),
    GET_PRODUCT_SUCCESS(true, 1022, "상품 조회에 성공하였습니다."),
    // [GET] /banner
    GET_BANNER_SUCCESS(true, 1048, "배너 조회에 성공하였습니다."),

    // [GET] /c
    GET_CATEGORY_SUCCESS(true, 1059, "카테고리 조회에 성공하였습니다."),
    GET_LAST_CATEGORY_SUCCESS(true, 1060, "하위 카테고리 조회에 성공하였습니다."),

    MODIFY_PRODUCT_IS_DELETED_SUCCESS(true,1994,"상품이 삭제되었습니다."),
    MODIFY_PRODUCT_CONDITION_SUCCESS(true,1995,"상태변경이 완료되었습니다."),
    MODIFY_PRODUCT_SUCCESS(true,1996, "수정이 완료되었습니다."),
    COMMENT_DELETE_SUCCESS(true,1997,"댓글을 삭제하였습니다."),
    GET_COMMENTS_SUCCESS(true,1998, "댓글조회에 성공하였습니다."),
    POST_COMMENT_SUCCESS(true,1999,"댓글을 등록하였습니다."),



    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),

    // users
    EMPTY_ID(false, 2010, "아이디를 확인해주세요."),
    EMPTY_PWD(false, 2011, "비밀번호를 확인해주세요."),
    EMPTY_NICKNAME(false, 2012, "닉네임을 확인해주세요."),
    EMPTY_LOCATION(false, 2013, "주소를 확인해주세요."),
    USERS_EMPTY_PHONENUMBER(false, 2014, "전화번호를 확인해주세요."),
    INVALID_PHONENUMBER(false, 2015, "전화번호 형식을 확인해주세요."),
    INVALID_GENDER(false, 2020, "입력값을 확인해주세요."),
    INVALID_DATE(false, 2021, "올바른 날짜를 입력하세요."),
    INVALID_ACCOUNT(false, 2022, "올바른 계좌번호를 입력하세요."),
    EMPTY_NAME(false, 2023, "예금주 명을를 확인해주세요."),
    EMPTY_ACCOUNTNUM(false, 2024, "계좌번호를 확인해주세요."),



    // [POST] products
    EMPTY_PRODUCT_IMG(false, 2500, "사진을 한 개 이상 등록해주세요."),
    EXCESS_PRODUCT_IMG(false,2501, "사진을 12개까지만 등록해주세요."),
    NOT_ENOUGH_TITLE_LENGTH(false,2502, "상품명을 2글자 이상 입력해주세요."),
    NOT_SELECT_FIRST_CATEGORY(false, 2503, "카테고리를 선택해주세요"),
    NOT_SELECT_LAST_CATEGORY(false,2504, "하위 카테고리를 선택해주세요."),
    EMPTY_TAGS(false,2505, "태그를 입력해주세요."),
    NOT_ENOUGH_PRICE(false,2506, "100원 이상 입력해주세요."),
    NOT_ENOUGH_CONTENTS(false,2507, "상품 설명을 10글자 이상 입력해주세요."),
    NOT_ENOUGH_AMOUNT(false, 2508, "수량을 1개 이상 입력해주세요."),
    NOT_SELECT_IS_USED(false, 2509, "중고상품인지 새상품인지 선택해주세요."),
    NOT_SELECT_CHANGEABLE(false,2510, "교환 가능 여부를 선택해주세요."),
    NOT_SELECT_PAY(false, 2511, "번개페이 사용 여부를 선택해주세요."),
    NOT_SELECT_SHIPPING_FEE(false,2512, "배송비 포함 여부를 선택해주세요."),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // 회원가입(3010 ~ 3020)
    EXISTS_ID(false,3010,"이미 사용중인 아이디입니다."),
    EXISTS_NICKNAME(false,3011,"이미 사용중인 닉네임입니다."),
    EXISTS_PHONENUM(false,3012,"이미 사용중인 전화번호 입니다."),

    // 로그인(3020 ~ 3030)
    NO_EXISTED_ID(false, 3020, "존재하지 않는 아이디입니다."),
    NOT_ACTIVE_USER(false, 3021, "비활성 유저입니다."),
    FAILED_TO_LOGIN(false,3022,"없는 아이디거나 비밀번호가 틀렸습니다."),

    // 계좌 추가 (3490 ~ 3500)
    EXISTS_ACCOUNT(false,3030,"이미 사용중인 계좌번호 입니다."),
    INVALID_BANKID(false,3031,"지원되지 않는 은행입니다."),
    MAX_ACCOUNT_CNT(false,3032,"더이상 계좌 등록이 불가능합니다."),
    DELETE_ACCOUNT_FAIL(false,3033,"계좌 삭제에 실패했습니다."),
    EMPTY_ACCOUNT(false,3034,"계좌 목록이 비어있습니다."),
    INVALID_ACCOUNTID(false, 3035, "없는 계좌이거나 본인의 계좌가 아닙니다."),

    MAX_ADDRESS_CNT(false,3040,"더이상 주소 등록이 불가능합니다."),
    EXISTS_ADDRESS(false,3041,"이미 등록된 배송지입니다."),
    INVALID_ADDRESSID(false, 3042, "없는 배송지이거나 본인의 배송지가 아닙니다."),
    EMPTY_ADDRESS(false,3043,"배송지 목록이 비어있습니다."),

    NO_EXISTED_CATEGORY_LIST(false, 3590, "등록된 카테고리가 없습니다."),

    // 하위 카테고리 조회(3600 ~ 3610)
    NO_EXISTED_FIRST_CATEGORY(false, 3600, "상위 카테고리가 존재하지 않습니다."),
    NO_EXISTED_LAST_CATEGORY(false,3601, "하위 카테고리가 존재하지 않습니다."),

    // 상품 생성, 조회, 수정
    MODIFY_PRODUCT_IS_DELETED_FAILED(false,3996,"이 상품을 삭제할 수 없습니다."),
    MODIFY_PRODUCT_CONDITION_FAILED(false,3997,"상태변경을 실패하였습니다."),
    MODIFY_PRODUCT_FAILED(false,3998,"상품 수정을 실패했습니다."),
    INVALID_USER_DELETE_COMMENT(false,3999,"댓글을 삭제할 권한이 없습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    // 사용자 정보 수정(폰번호/생년월일/성별)
    MODIFY_FAIL_INFO(false,4030,"사용자 정보 수정에 실패하였습니다."),
    WITHDRAWL_FAIL(false, 4031, "탈퇴처리에 실파해였습니다."),
    MODIFY_FAIL_ACCOUNT(false,4032,"계좌정보 수정에 실패하였습니다."),
    MODIFY_FAIL_ADDRESS(false,4032,"배송지정보 수정에 실패하였습니다."),
    DELETE_FAIL_ADDRESS(false,4033,"배송지정보 삭제에 실패하였습니다.");




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
