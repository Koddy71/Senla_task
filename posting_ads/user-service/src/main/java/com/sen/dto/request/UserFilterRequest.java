package com.sen.dto.request;

import jakarta.validation.constraints.Min;

public class UserFilterRequest {
    @Min(1)
    private int page = 0;
    @Min(1)
    private int size = 20;

    public UserFilterRequest() {
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
