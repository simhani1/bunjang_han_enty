package com.example.demo.utils;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
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
            return true;
        else
            return false;
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
