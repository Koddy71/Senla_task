package com.sen.exception;

public class UserIsBlockedException extends RuntimeException{
    public UserIsBlockedException(String login){
        super("Профиль пользователя " + login + " удалён");
    }
}
