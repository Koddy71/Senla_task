package com.sen.exception;

public class NotOwnerException extends RuntimeException{
    public NotOwnerException(){
        super("Не владелец");
    }
}
