package com.ticket.concert.global.regex;

public class UserRegex {

    // 8~20자, 숫자/영문/특수문자 중 2종류 이상 포함
    public static final String PASSWORD = "^(?=.*[A-Za-z])(?=.*\\d)|(?=.*[A-Za-z])(?=.*[!@#$%^&*])|(?=.*\\d)(?=.*[!@#$%^&*]).{8,20}$";
    public static final String EMAIL = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
}
