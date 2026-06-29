package com.sen.exception;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException() {
        super("У этой покупки уже есть отзыв");
    }
}
