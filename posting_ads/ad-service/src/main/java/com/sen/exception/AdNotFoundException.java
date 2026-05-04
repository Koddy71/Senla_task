package com.sen.exception;

import java.util.UUID;

public class AdNotFoundException extends RuntimeException{
    public AdNotFoundException(UUID adId){
        super("Объявление не найдено" + adId);
    }
}
