package com.sen.exception;

public class UserBlockedException extends RuntimeException{
    public UserBlockedException(String login){
        super("Профиль пользователя " + login + " удалён");
    }
}
