package com.sen.dto.response;

public class SellerRatingResponse {
    private String sellerLogin;
    private double averageRating;
    private Long totalReviews;

    public SellerRatingResponse(String sellerLogin, double averageRating, long totalReviews) {
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public String getSellerId() {
        return sellerLogin;
    }

    public void setSellerLogin(String sellerLogin) {
        this.sellerLogin = sellerLogin;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public Long getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(Long totalReviews) {
        this.totalReviews = totalReviews;
    }
}
