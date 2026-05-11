package com.ticket.concert.global.auth;

import com.ticket.concert.domain.LoginUser;

public class UserContextHolder {

    private static final ThreadLocal<LoginUser> CURRENT = new ThreadLocal<>();

    private UserContextHolder() {}

    public static void set(LoginUser user){
        CURRENT.set(user);
    }

    public static LoginUser get(){
        return CURRENT.get();
    }

    public static void remove(){
        CURRENT.remove();
    }
}
