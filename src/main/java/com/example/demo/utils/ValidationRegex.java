package com.example.demo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationRegex {
    // 이메일 형식 체크
    public static boolean isRegexEmail(String target) {
        String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(target);
        return matcher.find();
    }

    // 전화 번호 형식 체크(자릿수 비교로 간단하게)
    public static boolean isRegexTelephoneNum(String target) {
        if (target.length() == 11)
            return false;
        else
            return true;
    }

    // 문자 인증번호 형식 체크(6자리)
    public static boolean isRegexCertCode(String target) {
        if(target.length() == 6)
            return false;
        else
            return true;
    }

    // 계좌번호 형식 체크(자릿수 비교로 간단하게)
    public static boolean isRegexAccountNum(String target) {
        if (target.length() == 11)
            return false;
        else
            return true;
    }

    /** 입력 date가 yyyy-MM-dd 형태로 들어옴 */
    public static boolean validationDate(String checkDate){
        try{
            SimpleDateFormat  dateFormat = new  SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(checkDate);
            return  true;
        }catch (ParseException  e){
            return  false;
        }
    }
}
