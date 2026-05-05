package com.ticket.concert.global.regex;

public class Regex {

    public static final String USER_ID = "^[A-Za-z0-9]{8,20}$";
    // 8~20자, 숫자/영문/특수문자 중 2종류 이상 포함
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[!@#$%^&*])|(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$";
}
