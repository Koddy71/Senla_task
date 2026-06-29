package com.sen.exception;

public class UserBlockedException extends RuntimeException{
    public UserBlockedException(String login){
        super("Аккаунт пользователя " + login + " удалён");
    }
}
